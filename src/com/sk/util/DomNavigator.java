package com.sk.util;

import com.google.gson.JsonElement;

public class DomNavigator extends DocNavigator {

	public DomNavigator(String field, String... navigation) {
		super(field, navigation);
	}

	public DomNavigator(String field, StringProcessor processor, String... navigation) {
		super(field, processor, navigation);
	}

	@Override
	public void navigate(JsonElement source, FieldBuilder destination) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String buildCssSelector() {
		StringBuilder ret = new StringBuilder("");
		ret = appendNavigation(ret);
		return ret.toString();
	}
}
