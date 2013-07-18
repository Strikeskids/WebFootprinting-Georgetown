package com.sk.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NameComparison {

	private static NameComparison singleton;

	public static NameComparison get() {
		if (singleton == null) {
			synchronized (NameComparison.class) {
				if (singleton == null) {
					singleton = new NameComparison();
				}
			}
		}
		return singleton;
	}

	private final String key;

	private NameComparison() {
		JsonObject tokens = ApiUtility.getTokensFor("PiplApi");
		if (tokens == null || !tokens.has("name_key"))
			throw new IllegalArgumentException();
		key = tokens.get("name_key").getAsString();
	}

	private final Map<String, Set<String>> gathered = new HashMap<>();
	private final Map<String, JsonObject> rawObjects = new HashMap<>();

	public boolean isSameFirstName(String a, String b) {
		return getPossibilities(a).contains(format(b));
	}

	private static final String FIRST_BASE = "http://api.pipl.com/name/v2/json/?key=%s&first_name=%s";
	private static final String RAW_BASE = "http://api.pipl.com/name/v2/json/?key=%s&raw_name=%s";

	private static final JsonParser parser = new JsonParser();

	private JsonObject getForName(String raw) throws IOException {
		raw = raw.toLowerCase();
		if (rawObjects.containsKey(raw))
			return rawObjects.get(raw);
		else {
			JsonObject ret = parser.parse(
					new BufferedReader(new InputStreamReader(new URL(String.format(RAW_BASE, key,
							URLEncoder.encode(raw, "UTF-8"))).openStream()))).getAsJsonObject();
			rawObjects.put(raw, ret);
			return ret;
		}
	}

	public String[] parseName(String raw) {
		try {
			JsonObject names = getForName(raw);
			JsonObject name = names.get("name").getAsJsonObject();
			if (name.has("first") && name.has("last")) {
				return new String[] { name.get("first").getAsString(), name.get("last").getAsString() };
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isSameName(String[] a, String[] b) {
		if (a == null || b == null || a.length != 2 || b.length != 2)
			return false;
		return format(a[1]).equals(format(b[1])) && isSameFirstName(a[0], b[0]);
	}

	private Set<String> loadPossibilities(String name) {
		name = format(name);
		Set<String> ret = new HashSet<>();
		try {
			JsonObject json = parser.parse(
					new BufferedReader(new InputStreamReader(new URL(String.format(FIRST_BASE, key,
							URLEncoder.encode(name, "UTF-8"))).openStream()))).getAsJsonObject();
			addFrom(json, "full_names", ret);
			addFrom(json, "nicknames", ret);
			addFrom(json, "spellings", ret);
		} catch (IOException e) {
			e.printStackTrace();
			return ret;
		}
		ret.add(name);
		return ret;
	}

	public Set<String> getPossibilities(String name) {
		if (name == null)
			return new HashSet<>();
		name = format(name);
		if (gathered.containsKey(name))
			return gathered.get(name);
		Set<String> value = loadPossibilities(name);
		gathered.put(name, value);
		Queue<String> lookupQueue = new LinkedList<String>(value);
		while (!lookupQueue.isEmpty()) {
			String lookup = lookupQueue.poll();
			Set<String> lookupNames = (gathered.containsKey(lookup) ? gathered.get(lookup)
					: loadPossibilities(lookup));
			for (String foundName : lookupNames) {
				if (value.add(foundName)) {
					lookupQueue.add(foundName);
				}
			}
			gathered.put(lookup, lookupNames);
		}
		for (String key : value) {
			gathered.put(key, value);
		}
		return value;
	}

	private void addFrom(JsonObject wrap, String sub, Set<String> dest) {
		if (wrap == null)
			return;
		JsonElement inside = wrap.get(sub);
		if (inside == null || !inside.isJsonObject())
			return;
		JsonElement source = inside.getAsJsonObject().get("first");
		if (source != null && source.isJsonArray()) {
			for (JsonElement e : source.getAsJsonArray())
				dest.add(format(e.getAsString()));
		}
	}

	public String format(String input) {
		if (input == null)
			return "";
		return input.toLowerCase().replaceAll("[^A-Za-z]", "");
	}

}
