package com.sk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk.web.Token;

public class NameComparison {

	private static LazyField<NameComparison> singleton = new LazyField<>(new Callable<NameComparison>() {
		@Override
		public NameComparison call() throws Exception {
			Token token = ApiUtility.getNamedToken("pipl", "name_key");
			return new NameComparison(token.getKey());
		}
	});

	public static NameComparison get() {
		return singleton.get();
	}

	private final String key;

	private NameComparison(String key) {
		this.key = key;
	}

	private final Map<String, Set<String>> gathered = new HashMap<>();
	private final Map<String, JsonObject> rawObjects = new HashMap<>();

	public boolean isSameFirstName(String a, String b) {
		HashSet<String> possA = new HashSet<String>(getPossibilities(a));
		possA.retainAll(getPossibilities(b));
		return !possA.isEmpty();
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

	public Set<String> getPossibilities(String name) {
		if (name == null)
			return new HashSet<>();
		name = format(name);
		if (gathered.containsKey(name))
			return gathered.get(name);
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
		gathered.put(name, ret);
		return ret;
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
