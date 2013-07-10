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
	private final String SINGLE = "https://www.googleapis.com/plus/v1/people/%s?key=%s&fields=id%%2CdisplayName%%2Cname(familyName%%2CgivenName)%%2Cgender%%2Curl%%2Cbirthday%%2CrelationshipStatus%%2CageRange";

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
				PersonalData dat = new PersonalData("g+");

				put(user, dat, "id", "id");
				put(user, dat, "displayName", "name");
				if (user.has("name")) {
					JsonObject name = user.get("name").getAsJsonObject();
					put(name, dat, "familyName", "last-name");
					put(name, dat, "givenName", "first-name");
				}
				put(user, dat, "gender", "gender");
				if (user.has("url")) {
					try {
						url.add(new URL(user.get("url").getAsString()));
					} catch (MalformedURLException ign) {
					}
				}
				put(user, dat, "relationshipStatus", "relationship-status");
				put(user, dat, "birthday", "birthday");
				if (user.has("ageRange")) {
					JsonObject range = user.get("ageRange").getAsJsonObject();
					dat.put("age", range.get("min").getAsString() + "-" + range.get("max").getAsString());
				}
				data.add(dat);
			}
		}

		urls.set(url.toArray(new URL[url.size()]));
		this.data.set(data.toArray(new PersonalData[data.size()]));
		return true;
	}

	private void put(JsonObject src, PersonalData dest, String skey, String dkey) {
		if (src.has(skey))
			dest.put(dkey, src.get(skey).getAsString());
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
