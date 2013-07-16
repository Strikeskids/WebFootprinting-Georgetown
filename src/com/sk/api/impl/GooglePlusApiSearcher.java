package com.sk.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk.api.ApiUtility;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;
import com.sk.util.parse.search.NameSearcher;

public class GooglePlusApiSearcher implements NameSearcher {

	private final String key;
	private final ThreadLocal<URL[]> urls = new ThreadLocal<>();
	private final ThreadLocal<PersonalData[]> data = new ThreadLocal<>();

	public GooglePlusApiSearcher() {
		JsonObject obj = ApiUtility.getTokensFor("g+");
		if (obj.has("client_key"))
			key = obj.get("client_key").getAsString();
		else
			throw new RuntimeException("Failed to get key");
	}

	private final String URL = "https://www.googleapis.com/plus/v1/people?key=%s&query=%s%%20%s";
	private final String SINGLE = "https://www.googleapis.com/plus/v1/people/%s?key=%s&fields=id%%2CdisplayName%%2Cname%%2Cgender%%2Curl%%2Cbirthday%%2CrelationshipStatus%%2CageRange%%2Corganizations%%2CaboutMe";

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		List<PersonalData> data = new ArrayList<>();
		List<URL> url = new ArrayList<>();
		String searchLoc = String.format(URL, key, URLEncoder.encode(first, "UTF-8"),
				URLEncoder.encode(last, "UTF-8"));
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(new BufferedReader(new InputStreamReader(new URL(searchLoc).openStream())))
				.getAsJsonObject();
		if (obj.has("items")) {
			for (JsonElement e : obj.get("items").getAsJsonArray()) {
				String uid = e.getAsJsonObject().get("id").getAsString();
				JsonObject user = parser.parse(
						new BufferedReader(new InputStreamReader(new URL(String.format(SINGLE, uid, key))
								.openStream()))).getAsJsonObject();
				FieldBuilder builder = new FieldBuilder();

				builder.put(user, "aboutMe", "blob");
				builder.put(user, "id", "id");
				builder.put(user, "displayName", "name");
				if (user.has("name")) {
					JsonObject name = user.get("name").getAsJsonObject();
					builder.put(name, "familyName", "last-name");
					builder.put(name, "givenName", "first-name");
				}
				builder.put(user, "gender", "gender");
				if (user.has("url")) {
					try {
						url.add(new URL(user.get("url").getAsString()));
					} catch (MalformedURLException ign) {
					}
				}
				builder.put(user, "relationshipStatus", "relationship-status");
				builder.put(user, "birthday", "birthday");
				if (user.has("ageRange")) {
					JsonObject range = user.get("ageRange").getAsJsonObject();
					builder.put("age", range.get("min").getAsString() + "-" + range.get("max").getAsString());
				}
				if (user.has("organizations"))
					for (JsonElement organizationElement : user.get("organizations").getAsJsonArray()) {
						JsonObject organization = organizationElement.getAsJsonObject();
						if (organization.get("type").getAsString().equals("work")) {
							builder.put(organization, "name", "company");
							builder.put(organization, "title", "job-title");
						} else {
							builder.put(organization, "name", "education");
						}
					}

				PersonalData dat = new PersonalData("g+");
				builder.addTo(dat);

				data.add(dat);
			}
		}

		urls.set(url.toArray(new URL[url.size()]));
		this.data.set(data.toArray(new PersonalData[data.size()]));
		return true;
	}

	@Override
	public URL[] results() throws IllegalStateException {
		URL[] ret = urls.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}

	@Override
	public PersonalData[] getData() throws IllegalStateException {
		PersonalData[] ret = data.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}
}
