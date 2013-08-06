package com.sk.util.navigate;

import org.jsoup.nodes.Document;

import com.google.gson.JsonElement;
import com.sk.util.StringProcessor;
import com.sk.util.data.FieldBuilder;

public class FieldNavigator extends DocNavigator {

	protected final String field;

	public FieldNavigator(String field, String... navigation) {
		super(navigation);
		this.field = field;
	}

	public FieldNavigator(String field, StringProcessor processor, String... navigation) {
		super(processor, navigation);
		this.field = field;
	}

	public void navigate(JsonElement source, FieldBuilder destination) {
		for (String value : navigate(source)) {
			destination.put(field, value);
		}
	}

	public void navigate(Document source, FieldBuilder destination) {
		for (String value : navigate(source)) {
			destination.put(field, value);
		}
	}

}
