package com.sk.guess.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk.guess.DataCleaner;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;

public class CategoryCleaner implements DataCleaner {

	private final String[] sourceAttributes;
	private final int numberPages;

	public CategoryCleaner(int numberPages, String... sourceAttributes) {
		this.numberPages = numberPages;
		this.sourceAttributes = sourceAttributes;
	}

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		Map<String, List<String>> pages = new LinkedHashMap<>();
		Map<String, List<String>> categories = new HashMap<>();

		@SuppressWarnings("serial")
		Map<String, String> queryMap = new HashMap<String, String>() {
			{
				put("list", "search");
				put("srlimit", Objects.toString(numberPages, "10"));
				put("srprop", "");
				put("srnamespace", "0");
			}
		};
		Set<String> allPages = new TreeSet<>();
		for (String attr : sourceAttributes) {
			if (in.containsKey(attr)) {
				String[] values = in.getAllValues(attr);
				for (String value : values) {
					queryMap.put("srsearch", value);
					JsonObject wikiResult = readWiki(queryMap);
					if (wikiResult != null) {
						List<String> pageList = new ArrayList<>();
						for (JsonElement resultElement : wikiResult.get("query").getAsJsonObject().get("search")
								.getAsJsonArray()) {
							JsonObject result = resultElement.getAsJsonObject();
							String title = result.get("title").getAsString();
							if (!title.contains("disambiguation"))
								pageList.add(title);
						}
						allPages.addAll(pageList);
						pages.put(value, pageList);
					}
				}
			}
		}

		if (allPages.size() == 0)
			return false;

		Iterator<String> allPagesIterator = allPages.iterator();
		queryMap.clear();
		queryMap.put("prop", "categories");
		queryMap.put("cllimit", "500");
		StringBuilder titles = new StringBuilder();
		do {
			titles.delete(0, titles.length());
			for (int i = 0; i < CATEGORY_QUERY_COUNT && allPagesIterator.hasNext(); ++i) {
				titles.append(allPagesIterator.next());
				titles.append("|");
			}
			queryMap.put("titles", titles.substring(0, titles.length() - 1));
			JsonObject wikiResult = readWiki(queryMap);
			if (wikiResult != null) {
				for (Entry<String, JsonElement> element : wikiResult.get("query").getAsJsonObject().get("pages")
						.getAsJsonObject().entrySet()) {
					JsonObject page = element.getValue().getAsJsonObject();
					String title = page.get("title").getAsString();
					List<String> categoryList = new ArrayList<>();
					for (JsonElement categoryElement : page.get("categories").getAsJsonArray()) {
						String categoryTitle = categoryElement.getAsJsonObject().get("title").getAsString();
						String category = categoryTitle.substring(9);
						if (!BAD_CATEGORIES.matcher(category).find()) {
							categoryList.add(category);
						}
					}
					categories.put(title, categoryList);
				}
			}
		} while (allPagesIterator.hasNext());

		FieldBuilder builder = new FieldBuilder();
		for (String attr : sourceAttributes) {
			if (in.containsKey(attr)) {
				String[] values = in.getAllValues(attr);
				in.remove(attr);
				final Multiset<String> categoryCount = HashMultiset.create();
				for (String value : values) {
					if (pages.containsKey(value)) {
						for (String page : pages.get(value)) {
							if (categories.containsKey(page)) {
								categoryCount.addAll(categories.get(page));
							}
						}
					}
				}
				PriorityQueue<String> bestCategories = new PriorityQueue<>(categoryCount.elementSet().size(),
						new Comparator<String>() {
							@Override
							public int compare(String o1, String o2) {
								return -Integer.compare(categoryCount.count(o1), categoryCount.count(o2));
							}
						});
				bestCategories.addAll(categoryCount.elementSet());
				for (int i = 0; i < numberPages && !bestCategories.isEmpty(); ++i) {
					builder.put("C" + attr, bestCategories.poll());
				}
			}
		}
		builder.addTo(out);
		return true;
	}

	private static final Pattern BAD_CATEGORIES = Pattern.compile("(?i)(?:article|pages|template|category)");
	private static final int CATEGORY_QUERY_COUNT = 25;

	private static final String BASE = "http://en.wikipedia.org/w/api.php?format=json&action=query";
	private static final JsonParser parser = new JsonParser();

	private JsonObject readWiki(Map<String, String> query) {
		try {
			StringBuilder urlb = new StringBuilder(BASE);
			for (Entry<String, String> param : query.entrySet()) {
				urlb.append("&");
				urlb.append(param.getKey());
				urlb.append("=");
				urlb.append(URLEncoder.encode(param.getValue(), "UTF-8"));
			}
			HttpURLConnection wikiConn = (HttpURLConnection) new URL(urlb.toString()).openConnection();
			wikiConn.addRequestProperty("User-Agent", "WebfootprintingGrabber/1.0 (gtgrab@strikeskids.com)");
			return parser.parse(new BufferedReader(new InputStreamReader(wikiConn.getInputStream())))
					.getAsJsonObject();
		} catch (IOException ex) {
			return null;
		}
	}
}
