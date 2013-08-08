package com.sk.clean.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk.clean.DataCleaner;
import com.sk.parse.util.Parsers;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;
import com.sk.util.navigate.DocNavigator;
import com.sk.web.IOUtil;
import com.sk.web.Request;

public class WikipediaRedirectCleaner implements DataCleaner {

	private static final String TITLES_KEY = "titles";
	private static final String MAXIMUM_LINKS_PER_REQUEST = "500";
	private static final String LINKS_LIMIT_KEY = "pllimit";
	private static final String REDIRECT_KEY = "redirects";
	private static final String PROPERTY_KEY = "prop";
	private static final String NAMESPACE_KEY = "ns";
	private static final String MISSING_PAGE_KEY = "missing";
	private static final String DISAMBIGUATION_PAGE_KEY = "disambiguation";
	private static final String PAGE_PROPERTIES_KEY = "pageprops";
	private static final String LINKS_KEY = "links";
	private static final String PAGES_KEY = "pages";
	private static final String TO_KEY = "to";
	private static final String FROM_KEY = "from";
	private static final String[] REDIRECT_KEYS = { REDIRECT_KEY, "normalized" };
	private static final String CONTINUE_LINK_KEY = "plcontinue";
	private static final String TITLE_KEY = "title";
	private static final String QUERY_KEY = "query";
	private static final DocNavigator CONTINUE_TOKEN_NAVIGATOR = new DocNavigator("query-continue", LINKS_KEY,
			CONTINUE_LINK_KEY);
	private static final String BASE = "http://en.wikipedia.org/w/api.php?format=json&action=query";

	private final String[] chosenFields;

	public WikipediaRedirectCleaner(String... attributeNames) {
		this.chosenFields = attributeNames;
	}

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		FieldBuilder builder = new FieldBuilder();

		Set<String> disambiguationTitles = new TreeSet<>();
		Map<String, String> changeMap = getChangeMapAndDisambiguation(in, disambiguationTitles);

		performDisambiguationSearches(changeMap, disambiguationTitles);
		for (String attributeName : chosenFields) {
			for (String value : in.getAllValues(attributeName)) {
				String changed = getActualValue(changeMap, value);
				builder.put(attributeName, changed);
			}
			in.remove(attributeName);
		}
		builder.addTo(out);
		return !builder.isEmpty();
	}

	private Map<String, String> getChangeMapAndDisambiguation(PersonalData input, Set<String> disambiguation) {
		Map<String, String> changeMap = getBaseChangeMap(input);
		Iterator<String> titles = new ArrayList<>(changeMap.keySet()).iterator();
		while (titles.hasNext()) {
			Set<String> currentDisambiguation = performRedirectRequest(changeMap, titles);
			disambiguation.addAll(currentDisambiguation);
		}
		return changeMap;
	}

	private Map<String, String> performDisambiguationSearches(Map<String, String> changeMap,
			Set<String> disambiguation) {
		Iterator<String> titles = disambiguation.iterator();
		FieldBuilder joinedTitleBuilder = new FieldBuilder();
		while (titles.hasNext()) {
			Request request = getDisambiguationPageRequest(titles);
			joinedTitleBuilder = disambiguationSearch(joinedTitleBuilder, request);
		}
		Map<String, String> joinedTitles = new HashMap<>();
		joinedTitleBuilder.addTo(joinedTitles);
		for (Entry<String, String> title : joinedTitles.entrySet()) {
			addChangeToMap(changeMap, title.getKey(), title.getValue());
		}
		return changeMap;
	}

	private FieldBuilder disambiguationSearch(FieldBuilder joinedTitleBuilder, Request request) {
		JsonObject result = getWikiResult(request);
		JsonObject query = result.get(QUERY_KEY).getAsJsonObject();
		for (JsonObject page : getPages(query)) {
			String title = page.get(TITLE_KEY).getAsString();
			for (JsonObject link : getLinks(page)) {
				if (isPageLink(link))
					joinedTitleBuilder.put(link, TITLE_KEY, title);
			}
		}
		String continueToken = getLinkContinueToken(result);
		if (continueToken != null) {
			request.addQuery(CONTINUE_LINK_KEY, continueToken);
			return disambiguationSearch(joinedTitleBuilder, request);
		} else {
			return joinedTitleBuilder;
		}
	}

	private String getLinkContinueToken(JsonObject result) {
		for (String token : CONTINUE_TOKEN_NAVIGATOR.navigate(result))
			return token;
		return null;
	}

	private Set<String> performRedirectRequest(Map<String, String> changeMap, Iterator<String> titles) {
		Request request = getRedirectRequest(titles);
		Set<String> disambiguation = new HashSet<>();
		JsonObject result = getWikiResult(request);
		JsonObject query = result.get(QUERY_KEY).getAsJsonObject();
		for (String key : REDIRECT_KEYS)
			addChangesToMap(changeMap, query.get(key));
		for (JsonObject page : getPages(query)) {
			String title = page.get(TITLE_KEY).getAsString();
			if (isDisambiguationPage(page)) {
				disambiguation.add(title);
			} else if (isMissingPage(page)) {
				String from = getActualValue(changeMap, title);
				String to = GenericCleaner.cleanValue(from);
				addChangeToMap(changeMap, from, to);
			}
		}
		return disambiguation;
	}

	private Map<String, String> getBaseChangeMap(PersonalData input) {
		Map<String, String> changeMap = new TreeMap<>();
		for (String attributeName : chosenFields) {
			if (input.containsKey(attributeName)) {
				for (String value : input.getAllValues(attributeName)) {
					changeMap = addChangeToMap(changeMap, null, value);
				}
			}
		}
		return changeMap;
	}

	private Map<String, String> addChangeToMap(Map<String, String> changeMap, String from, String to) {
		if (from != null)
			changeMap.put(from, to);
		changeMap.put(to, null);
		return changeMap;
	}

	private String combineStrings(Iterator<String> values, int count) {
		if (count <= 0 || !values.hasNext())
			return "";
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < count && values.hasNext(); ++i) {
			ret.append(values.next());
			ret.append("|");
		}
		return ret.substring(0, ret.length() - 1);
	}

	private void addChangesToMap(Map<String, String> changeMap, JsonElement changes) {
		if (changes == null)
			return;
		for (JsonElement changeElement : changes.getAsJsonArray()) {
			JsonObject change = changeElement.getAsJsonObject();
			String from = change.get(FROM_KEY).getAsString();
			String to = change.get(TO_KEY).getAsString();
			changeMap = addChangeToMap(changeMap, from, to);
		}
	}

	private List<JsonObject> getPages(JsonObject query) {
		List<JsonObject> ret = new ArrayList<>();
		if (query.has(PAGES_KEY)) {
			JsonObject pages = query.get(PAGES_KEY).getAsJsonObject();
			for (Entry<String, JsonElement> pageEntry : pages.entrySet()) {
				ret.add(pageEntry.getValue().getAsJsonObject());
			}
		}
		return ret;
	}

	private List<JsonObject> getLinks(JsonObject page) {
		List<JsonObject> ret = new ArrayList<>();
		if (page.has(LINKS_KEY)) {
			for (JsonElement linkElement : page.get(LINKS_KEY).getAsJsonArray()) {
				JsonObject link = linkElement.getAsJsonObject();
				ret.add(link);
			}
		}
		return ret;
	}

	private boolean isDisambiguationPage(JsonObject page) {
		if (page.has(PAGE_PROPERTIES_KEY)) {
			JsonObject pageprops = page.get(PAGE_PROPERTIES_KEY).getAsJsonObject();
			return pageprops.has(DISAMBIGUATION_PAGE_KEY);
		} else {
			return false;
		}
	}

	private boolean isMissingPage(JsonObject page) {
		return page.has(MISSING_PAGE_KEY);
	}

	private boolean isPageLink(JsonObject link) {
		return link.get(NAMESPACE_KEY).getAsInt() == 0;
	}

	private String getActualValue(Map<String, String> changeMap, String title) {
		String prev = title;
		while (title != null) {
			prev = title;
			title = changeMap.get(title);
		}
		return prev;
	}

	private JsonObject getWikiResult(Request request) {
		try {
			String jsonString = IOUtil.read(request);
			return Parsers.parseJSON(jsonString).getAsJsonObject();
		} catch (IOException ex) {
			ex.printStackTrace();
			return new JsonObject();
		}
	}

	private Request getRedirectRequest(Iterator<String> titles) {
		Request ret = getBaseRequest(titles);
		ret.addQuery(PROPERTY_KEY, PAGE_PROPERTIES_KEY);
		ret.addQuery(REDIRECT_KEY, "");
		return ret;
	}

	private Request getDisambiguationPageRequest(Iterator<String> titles) {
		Request ret = getBaseRequest(titles);
		ret.addQuery(LINKS_LIMIT_KEY, MAXIMUM_LINKS_PER_REQUEST);
		ret.addQuery(PROPERTY_KEY, LINKS_KEY);
		return ret;
	}

	private Request getBaseRequest(Iterator<String> titles) {
		try {
			Request ret = new Request(BASE);
			String joinedTitles = combineStrings(titles, 50);
			ret.addQuery(TITLES_KEY, joinedTitles);
			return ret;
		} catch (MalformedURLException impossible) {
			impossible.printStackTrace();
			return null;
		}
	}

}
