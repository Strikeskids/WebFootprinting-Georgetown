package com.sk.api.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk.api.AbstractApiSearcher;
import com.sk.api.ApiUtility;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;

public class TwitterApiSearcher extends AbstractApiSearcher {

	public TwitterApiSearcher() {
		super(new ApiUtility(TwitterApi.class));
		util.init("gtgrab");
	}

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		List<PersonalData> found = new ArrayList<>();
		String[] names = new String[] { first, last };
		for (int i = 0; i < 50; ++i) {
			if (!parseResponse(getResponse(getNameRequest(first, last, i + 1)), found, names))
				break;
		}
		this.data.set(found.toArray(new PersonalData[found.size()]));
		return found.size() > 0;
	}

	public OAuthRequest getNameRequest(String first, String last, int page) {
		String query;
		try {
			query = URLEncoder.encode(first + " " + last, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			query = "";
		}
		return new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/users/search.json?count=20&page=" + page
				+ "&q=" + query);
	}

	public boolean parseResponse(Response resp, List<PersonalData> found, String... names) {
		String body = resp.getBody();
		if (body == null || body.length() == 0)
			return false;
		JsonElement parsed = new JsonParser().parse(resp.getBody());
		if (!parsed.isJsonArray())
			return false;
		JsonArray users = parsed.getAsJsonArray();
		for (JsonElement e : users) {
			if (!e.isJsonObject())
				continue;
			JsonObject user = e.getAsJsonObject();
			FieldBuilder builder = new FieldBuilder();
			builder.put(user, "name");
			if (!builder.compareNames(names))
				return false;
			builder.put(user, "id_str", "id");
			builder.put(user, "location");
			builder.put(user, "screen_name", "username");
			builder.put(user, "url", "homePage");
			builder.put(user, "profile_image_url", "profilePictureUrl");
			builder.put(user, "description", "blob");

			PersonalData add = new PersonalData("twitter");
			builder.addTo(add);
			found.add(add);
		}
		return true;
	}

}
