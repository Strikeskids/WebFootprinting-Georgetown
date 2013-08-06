package com.sk.util.navigate;

import com.google.gson.JsonElement;
import com.sk.util.StringProcessor;
import com.sk.util.data.FieldBuilder;

public class DomNavigator extends FieldNavigator {

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
