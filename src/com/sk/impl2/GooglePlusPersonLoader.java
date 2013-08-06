package com.sk.impl2;

import static com.sk.impl2.GooglePlusApiLoader.SITE_KEY;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk.parse.IndividualExtractor;
import com.sk.parse.Parsers;
import com.sk.util.ApiUtility;
import com.sk.util.DocNavigator;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;
import com.sk.web.Request;

public class GooglePlusPersonLoader extends IndividualExtractor {

	private final String BASE_URL = "https://www.googleapis.com/plus/v1/people/%s?fields=id%%2C"
			+ "displayName%%2Cname%%2Cgender%%2Curl%%2Cbirthday%%2CrelationshipStatus%%2CageRange%%2C"
			+ "organizations%%2CaboutMe%%2Cimage";

	private final Request request;
	private JsonObject json;

	GooglePlusPersonLoader(String id) throws MalformedURLException {
		request = new Request(String.format(BASE_URL, id), "GET");
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
		for (DocNavigator navigator : navigators) {
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
		json = Parsers.parseJSON(data).getAsJsonObject();
	}

	private static final DocNavigator[] navigators = { new DocNavigator("blob", "aboutMe"),
			new DocNavigator("id", "id"), new DocNavigator("name", "displayName"),
			new DocNavigator("firstName", "name", "givenName"),
			new DocNavigator("lastName", "name", "familyName"), new DocNavigator("gender", "gender"),
			new DocNavigator("relationshipStatus", "relationshipStatus"),
			new DocNavigator("birthday", "birthday"), new DocNavigator("age", "ageRange"),
			new DocNavigator("profilePictureUrl", "image", "url"), new DocNavigator("email", "emails", "value") };

}
