package com.sk.util;

import java.util.Objects;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DocNavigator {

	protected final String field, selector, navigation[];
	protected final StringProcessor processor;

	public DocNavigator(String field, String... navigation) {
		this(field, StringProcessor.NIL, navigation);
	}

	public DocNavigator(String field, StringProcessor processor, String... navigation) {
		this.field = field;
		this.processor = processor == null ? StringProcessor.NIL : processor;
		this.navigation = navigation;
		this.selector = buildCssSelector();
	}

	public void navigate(JsonObject source, FieldBuilder destination) {
		navigate(source, destination, 0);
	}

	private void navigate(JsonElement source, FieldBuilder destination, int loc) {
		if (source == null)
			return;
		if (source.isJsonObject()) {
			if (loc < navigation.length) {
				JsonObject sourceObject = source.getAsJsonObject();
				String key = navigation[loc++];
				navigate(sourceObject.get(key), destination, loc);
			} else {
				addText(destination, Objects.toString(source));
			}
		} else if (source.isJsonArray()) {
			for (JsonElement element : source.getAsJsonArray()) {
				navigate(element, destination, loc);
			}
		} else if (source.isJsonPrimitive()) {
			if (loc == navigation.length) {
				JsonPrimitive primitive = source.getAsJsonPrimitive();
				if (primitive.isNumber() || primitive.isBoolean()) {
					addText(destination, Objects.toString(primitive));
				} else if (primitive.isString()) {
					addText(destination, primitive.getAsString());
				}
			}
		}
	}

	public void navigate(Document source, FieldBuilder destination) {
		for (Element found : source.select(selector)) {
			addText(destination, found.text());
		}
	}

	private void addText(FieldBuilder destination, String preText) {
		String modified = processor.process(preText);
		destination.put(field, modified);
	}

	protected String buildCssSelector() {
		StringBuilder ret = new StringBuilder(":root");
		ret = appendNavigation(ret);
		return ret.toString();
	}

	protected StringBuilder appendNavigation(StringBuilder ret) {
		for (String navigate : navigation) {
			ret.append(" > ");
			ret.append(navigate);
		}
		return ret;
	}
}
