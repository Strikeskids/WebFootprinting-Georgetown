package com.sk.guess.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk.guess.DataCleaner;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;

public class WikipediaRedirectCleaner implements DataCleaner {

	private final String[] attr;

	public WikipediaRedirectCleaner(String... attributeNames) {
		this.attr = attributeNames;
	}

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		FieldBuilder builder = new FieldBuilder();
		Map<String, String> allValues = new TreeMap<>();
		for (String attributeName : attr) {
			if (in.containsKey(attributeName)) {
				String[] values = in.getAllValues(attributeName);
				in.remove(attributeName);
				for (String value : values) {
					allValues.put(value, value);
				}
			}
		}
		StringBuilder titles = new StringBuilder();
		Iterator<String> valueIterator = allValues.keySet().iterator();
		Map<String, String> queryMap = new HashMap<>();
		while (valueIterator.hasNext()) {
			for (int i = 0; i < 50 && valueIterator.hasNext(); ++i) {
				titles.append(valueIterator.next());
				titles.append("|");
			}
			queryMap.put("titles", titles.substring(0, titles.length() - 1));
			queryMap.put("prop", "");
			queryMap.put("redirects", "");
			
		}
		builder.addTo(out);
		return !builder.isEmpty();
	}

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
