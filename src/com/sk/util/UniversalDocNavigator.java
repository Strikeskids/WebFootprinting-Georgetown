package com.sk.util;

import com.google.gson.JsonObject;

public class UniversalDocNavigator extends DocNavigator {

	public UniversalDocNavigator(String field, String... navigation) {
		super(field, navigation);
	}

	public UniversalDocNavigator(String field, StringProcessor processor, String... navigation) {
		super(field, processor, navigation);
	}

	@Override
	public void navigate(JsonObject source, FieldBuilder destination) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String buildCssSelector() {
		StringBuilder ret = new StringBuilder("");
		ret = appendNavigation(ret);
		return ret.toString();
	}
}
