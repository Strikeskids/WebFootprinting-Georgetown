package com.sk.parse.impl;

import static com.sk.parse.impl.FacebookApiLoader.SITE_KEY;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonObject;
import com.sk.parse.util.IndividualExtractor;
import com.sk.parse.util.Parsers;
import com.sk.util.ApiUtility;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;
import com.sk.util.navigate.FieldNavigator;
import com.sk.web.IOUtil;
import com.sk.web.Request;

public class FacebookPersonLoader extends IndividualExtractor {

	private static final String BASE_URL = "https://graph.facebook.com/%s";

	private final Request request;

	private JsonObject json;

	FacebookPersonLoader(String id) throws MalformedURLException {
		String url = String.format(BASE_URL, IOUtil.urlEncode(id));
		request = new Request(url);
		request.addHeader("access_token", ApiUtility.getAccessToken(SITE_KEY).getKey());
	}

	@Override
	protected Request getRequest() {
		return request;
	}

	@Override
	protected void parse(URL source, String data) {
		json = Parsers.parseJSON(data).getAsJsonObject();
	}

	@Override
	protected PersonalData getResult() {
		init();
		FieldBuilder builder = new FieldBuilder();
		PersonalData ret = new PersonalData(SITE_KEY);
		for (FieldNavigator navigator : navigators) {
			navigator.navigate(json, builder);
		}
		builder.joinNames();
		builder.addTo(ret);
		return ret;
	}

	private static final FieldNavigator[] navigators = { new FieldNavigator("firstName", "first_name"),
			new FieldNavigator("lastName", "last_name"), new FieldNavigator("gender", "gender"),
			new FieldNavigator("email", "email"), new FieldNavigator("id", "id") };

}
