package com.sk.impl2;

import static com.sk.impl2.GooglePlusApiLoader.SITE_KEY;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk.parse.AbstractLoader;
import com.sk.parse.Extractor;
import com.sk.parse.Parsers;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;
import com.sk.web.Request;

public class GooglePlusPersonLoader extends AbstractLoader implements Extractor {

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
	public List<PersonalData> call() throws Exception {
		return getResults();
	}

	@Override
	public List<PersonalData> getResults() {
		FieldBuilder builder = new FieldBuilder();
		addDataTo(builder);
		PersonalData ret = new PersonalData(SITE_KEY);
		builder.addTo(ret);
		return Arrays.asList(ret);
	}

	private void addDataTo(FieldBuilder builder) {
		builder.put(json, "aboutMe", "blob");
		builder.put(json, "id", "id");
		builder.put(json, "displayName", "name");
		addNameTo(builder);
		builder.put(json, "gender", "gender");
		builder.put(json, "relationshipStatus", "relationshipStatus");
		builder.put(json, "birthday", "birthday");
		addAgeTo(builder);
		addOrganizationsTo(builder);
		addProfilePictureTo(builder);
		addEmailsTo(builder);
	}

	private void addNameTo(FieldBuilder builder) {
		if (json.has("name")) {
			JsonObject name = json.get("name").getAsJsonObject();
			builder.put(name, "familyName", "lastName");
			builder.put(name, "givenName", "firstName");
		}
	}

	private void addAgeTo(FieldBuilder builder) {
		if (json.has("ageRange")) {
			JsonObject range = json.get("ageRange").getAsJsonObject();
			builder.put("age", range.get("min").getAsString() + "-" + range.get("max").getAsString());
		}
	}

	private void addOrganizationsTo(FieldBuilder builder) {
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

	private void addProfilePictureTo(FieldBuilder builder) {
		if (json.has("image")) {
			builder.put(json.get("image").getAsJsonObject(), "url", "profilePictureUrl");
		}
	}

	private void addEmailsTo(FieldBuilder builder) {
		if (json.has("emails")) {
			for (JsonElement emailElement : json.get("emails").getAsJsonArray()) {
				builder.put(emailElement.getAsJsonObject(), "value", "email");
			}
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

}
