package com.sk.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class FieldBuilder {

	private Map<String, StringBuilder> builders = new HashMap<>();

	public void addTo(PersonalData data) {
		for (Entry<String, StringBuilder> entry : builders.entrySet()) {
			StringBuilder builder = entry.getValue();
			if (!builder.toString().matches("[|]*")) {
				data.put(entry.getKey(), builder.substring(0, builder.length() - 1));
			}
		}
	}

	public void put(String key, Object value) {
		StringBuilder cur = builders.containsKey(key) ? builders.get(key) : new StringBuilder();
		if (value != null)
			cur.append(value);
		cur.append("|");
		builders.put(key, cur);
	}

}
