package com.sk.util.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sk.util.StringProcessor;

public class DocNavigator {

	protected final String selector;
	protected final List<String> navigation;
	protected final StringProcessor processor;

	public DocNavigator(String... navigation) {
		this(StringProcessor.NIL, navigation);
	}

	public DocNavigator(StringProcessor processor, String... navigation) {
		this.processor = processor == null ? StringProcessor.NIL : processor;
		this.navigation = Arrays.asList(navigation);
		this.selector = buildCssSelector();
	}

	public List<String> navigate(JsonElement source) {
		List<String> ret = new ArrayList<>();
		navigate(source, ret, navigation.iterator());
		return ret;
	}

	private void navigate(JsonElement source, List<String> destination, Iterator<String> nav) {
		if (source == null)
			return;
		if (source.isJsonObject()) {
			navigateObject(source.getAsJsonObject(), destination, nav);
		} else if (source.isJsonArray()) {
			navigateArray(source.getAsJsonArray(), destination, nav);
		} else if (source.isJsonPrimitive()) {
			navigatePrimitive(source.getAsJsonPrimitive(), destination, nav);
		}
	}

	private void navigateObject(JsonObject source, List<String> destination, Iterator<String> nav) {
		if (nav.hasNext()) {
			String key = nav.next();
			navigate(source.get(key), destination, nav);
		} else {
			addText(destination, source.toString());
		}
	}

	private void navigateArray(JsonArray source, List<String> destination, Iterator<String> nav) {
		for (JsonElement element : source) {
			navigate(element, destination, nav);
		}
	}

	private void navigatePrimitive(JsonPrimitive source, List<String> destination, Iterator<String> nav) {
		if (!nav.hasNext()) {
			JsonPrimitive primitive = source.getAsJsonPrimitive();
			if (primitive.isNumber() || primitive.isBoolean()) {
				addText(destination, primitive.toString());
			} else if (primitive.isString()) {
				addText(destination, primitive.getAsString());
			}
		}
	}

	public List<String> navigate(Document source) {
		List<String> ret = new ArrayList<>();
		for (Element found : source.select(selector)) {
			addText(ret, found.text());
		}
		return ret;
	}

	private void addText(List<String> list, String value) {
		list.add(processor.process(value));
	}

	protected String buildCssSelector() {
		StringBuilder ret = new StringBuilder(":root");
		ret.append(" > ");
		ret = appendNavigation(ret);
		return ret.toString();
	}

	protected StringBuilder appendNavigation(StringBuilder ret) {
		boolean started = false;
		for (String navigate : navigation) {
			if (started)
				ret.append(" > ");
			else
				started = true;
			ret.append(navigate);
		}
		return ret;
	}

}
