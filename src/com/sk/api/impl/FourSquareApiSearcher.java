package com.sk.api.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.scribe.builder.api.Foursquare2Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk.api.AbstractApiSearcher;
import com.sk.api.ApiUtility;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;

public class FourSquareApiSearcher extends AbstractApiSearcher {

	public FourSquareApiSearcher() {
		super(new ApiUtility(Foursquare2Api.class));
		util.init("gtgrab");
	}

	private static final String URL = "https://api.foursquare.com/v2/users/search?v=20130710&oauth_token=%s&name=%s%%20%s";

	public OAuthRequest getNameRequest(String first, String last) {
		try {
			return new OAuthRequest(Verb.GET, String.format(URL, util.getAccessToken().getToken(),
					URLEncoder.encode(first, "UTF-8"), URLEncoder.encode(last, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean parseResponse(Response resp, String... names) {
		String body;
		if (resp == null || (body = resp.getBody()) == null)
			return false;
		JsonObject obj = new JsonParser().parse(body).getAsJsonObject();
		List<PersonalData> ret = new ArrayList<>();
		for (JsonElement e : obj.get("response").getAsJsonObject().get("results").getAsJsonArray()) {
			JsonObject user = e.getAsJsonObject();
			FieldBuilder builder = new FieldBuilder();
			if (user.has("type"))
				continue;
			builder.put(user, "firstName");
			builder.put(user, "lastName");
			if (!builder.compareNames(names))
				break;
			builder.put(user, "gender");
			builder.put(user, "homeCity", "location");
			builder.put(user, "bio", "blob");
			if (user.has("contact")) {
				JsonObject contact = user.get("contact").getAsJsonObject();
				builder.put(contact, "email");
				builder.put(contact, "twitter");
				builder.put(contact, "phone");
			}
			builder.joinNames();
			PersonalData dat = new PersonalData("foursquare");
			builder.addTo(dat);
			if (dat.size() > 0)
				ret.add(dat);
		}
		this.data.set(ret.toArray(new PersonalData[ret.size()]));
		return true;
	}

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		return parseResponse(getResponse(getNameRequest(first, last)), first, last);
	}

}
