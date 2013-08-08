package com.sk.parse.impl;

import static com.sk.parse.impl.GooglePlusApiLoader.SITE_KEY;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk.parse.util.IndividualExtractor;
import com.sk.parse.util.Parsers;
import com.sk.util.ApiUtility;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;
import com.sk.util.navigate.FieldNavigator;
import com.sk.web.Request;

public class GooglePlusPersonLoader extends IndividualExtractor {

	private final String BASE_URL = "https://www.googleapis.com/plus/v1/people/%s?fields=id%%2C"
			+ "displayName%%2Cname%%2Cgender%%2Curl%%2Cbirthday%%2CrelationshipStatus%%2CageRange%%2C"
			+ "organizations%%2CaboutMe%%2Cimage";

	private final Request request;
	private JsonObject json;

	GooglePlusPersonLoader(String id) throws MalformedURLException {
		request = new Request(String.format(BASE_URL, id));
		request.addQuery("key", ApiUtility.getAccessToken(SITE_KEY).getKey());
	}

	@Override
	protected PersonalData getResult() {
		FieldBuilder builder = new FieldBuilder();
		addDataTo(builder);
		PersonalData ret = new PersonalData(SITE_KEY);
		builder.addTo(ret);
		return ret;
	}

	private void addDataTo(FieldBuilder builder) {
		init();
		for (FieldNavigator navigator : navigators) {
			navigator.navigate(json, builder);
		}
		addOrganizationsTo(builder);
	}

	private void addOrganizationsTo(FieldBuilder builder) {
		init();
		if (json.has("organizations")) {
			for (JsonElement organizationElement : json.get("organizations").getAsJsonArray()) {
				JsonObject organization = organizationElement.getAsJsonObject();
				addOrganization(organization, builder);
			}
		}
	}

	private void addOrganization(JsonObject organization, FieldBuilder builder) {
		if (organization.get("type").getAsString().equals("work")) {
			builder.put(organization, "name", "company");
			builder.put(organization, "title", "jobTitle");
		} else {
			builder.put(organization, "name", "education");
		}
	}

	@Override
	protected Request getRequest() {
		return request;
	}

	@Override
	protected void parse(URL source, String data) {
		JsonElement parsed = Parsers.parseJSON(data);
		if (parsed != null && parsed.isJsonObject())
			json = parsed.getAsJsonObject();
		else
			json = new JsonObject();
	}

	private static final FieldNavigator[] navigators = { new FieldNavigator("blob", "aboutMe"),
			new FieldNavigator("id", "id"), new FieldNavigator("name", "displayName"),
			new FieldNavigator("firstName", "name", "givenName"),
			new FieldNavigator("lastName", "name", "familyName"), new FieldNavigator("gender", "gender"),
			new FieldNavigator("relationshipStatus", "relationshipStatus"),
			new FieldNavigator("birthday", "birthday"), new FieldNavigator("age", "ageRange"),
			new FieldNavigator("profilePictureUrl", "image", "url"),
			new FieldNavigator("email", "emails", "value") };

}
