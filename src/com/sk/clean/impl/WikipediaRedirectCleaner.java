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

	private static final DocNavigator CONTINUE_TOKEN_NAVIGATOR = new DocNavigator("query-continue", "links",
			"plcontinue");
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
		JsonObject query = result.get("query").getAsJsonObject();
		for (JsonObject page : getPages(query)) {
			String title = page.get("title").getAsString();
			for (JsonObject link : getLinks(page)) {
				if (isPageLink(link))
					joinedTitleBuilder.put(link, "title", title);
			}
		}
		String continueToken = getLinkContinueToken(result);
		if (continueToken != null) {
			request.addQuery("plcontinue", continueToken);
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
		JsonObject query = result.get("query").getAsJsonObject();
		addChangesToMap(changeMap, query.get("normalized"));
		addChangesToMap(changeMap, query.get("redirects"));
		for (JsonObject page : getPages(query)) {
			String title = page.get("title").getAsString();
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
			String from = change.get("from").getAsString();
			String to = change.get("to").getAsString();
			changeMap = addChangeToMap(changeMap, from, to);
		}
	}

	private List<JsonObject> getPages(JsonObject query) {
		List<JsonObject> ret = new ArrayList<>();
		if (query.has("pages")) {
			JsonObject pages = query.get("pages").getAsJsonObject();
			for (Entry<String, JsonElement> pageEntry : pages.entrySet()) {
				ret.add(pageEntry.getValue().getAsJsonObject());
			}
		}
		return ret;
	}

	private List<JsonObject> getLinks(JsonObject page) {
		List<JsonObject> ret = new ArrayList<>();
		if (page.has("links")) {
			for (JsonElement linkElement : page.get("links").getAsJsonArray()) {
				JsonObject link = linkElement.getAsJsonObject();
				ret.add(link);
			}
		}
		return ret;
	}

	private boolean isDisambiguationPage(JsonObject page) {
		if (page.has("pageprops")) {
			JsonObject pageprops = page.get("pageprops").getAsJsonObject();
			return pageprops.has("disambiguation");
		} else {
			return false;
		}
	}

	private boolean isMissingPage(JsonObject page) {
		return page.has("missing");
	}

	private boolean isPageLink(JsonObject link) {
		return link.get("ns").getAsInt() == 0;
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
		ret.addQuery("prop", "pageprops");
		ret.addQuery("redirects", "");
		return ret;
	}

	private Request getDisambiguationPageRequest(Iterator<String> titles) {
		Request ret = getBaseRequest(titles);
		ret.addQuery("pllimit", "500");
		ret.addQuery("prop", "links");
		return ret;
	}

	private Request getBaseRequest(Iterator<String> titles) {
		try {
			Request ret = new Request(BASE);
			String joinedTitles = combineStrings(titles, 50);
			ret.addQuery("titles", joinedTitles);
			return ret;
		} catch (MalformedURLException impossible) {
			impossible.printStackTrace();
			return null;
		}
	}

}
