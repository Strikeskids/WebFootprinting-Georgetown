package com.sk.impl2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk.parse.PagingLoader;
import com.sk.parse.Parsers;
import com.sk.util.ApiUtility;
import com.sk.util.DocNavigator;
import com.sk.util.FieldBuilder;
import com.sk.util.NameComparison;
import com.sk.util.PersonalData;
import com.sk.web.IOUtil;
import com.sk.web.Request;

public class FourSquareApiLoader extends PagingLoader {

	private static final String BASE_URL = "https://api.foursquare.com/v2/users/search?v=20130710&name=%s%%20%s";
	private static final String SITE_KEY = "foursquare";
	private static final String NON_PERSON_IDENTIFIER = "type";

	private final String names[], url;
	private JsonObject json;

	public FourSquareApiLoader(String first, String last) {
		this.names = new String[] { first, last };
		this.url = String.format(BASE_URL, IOUtil.urlEncode(first), IOUtil.urlEncode(last));
	}

	@Override
	protected List<PersonalData> loadOwnResults() {
		List<PersonalData> ret = new ArrayList<>();
		for (JsonElement personElement : getPeople()) {
			JsonObject person = personElement.getAsJsonObject();
			if (person.has(NON_PERSON_IDENTIFIER)) {
				continue;
			}
			if (!checkName(person))
				continue;
			PersonalData found = getResult(personElement.getAsJsonObject());
			ret.add(found);
		}
		return ret;
	}

	private JsonArray getPeople() {
		init();
		JsonObject response = json.get("response").getAsJsonObject();
		return response.get("results").getAsJsonArray();
	}

	private PersonalData getResult(JsonObject person) {
		FieldBuilder builder = new FieldBuilder();
		for (DocNavigator navigator : navigators) {
			navigator.navigate(person, builder);
		}
		PersonalData ret = new PersonalData(SITE_KEY);
		builder.joinNames();
		builder.addTo(ret);
		return ret;
	}

	private boolean checkName(JsonObject person) {
		String[] personNames = { person.get("firstName").getAsString(), person.get("lastName").getAsString() };
		return NameComparison.get().isSameName(names, personNames);
	}

	@Override
	protected PagingLoader createNextPage() {
		return null;
	}

	@Override
	protected Request getRequest() {
		try {
			Request request = new Request(url, "GET");
			request.addQuery("oauth_token", ApiUtility.getAccessToken(SITE_KEY).getKey());
			return request;
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	@Override
	protected void parse(URL source, String data) {
		json = Parsers.parseJSON(data).getAsJsonObject();
	}

	private static final DocNavigator[] navigators = { new DocNavigator("firstName", "firstName"),
			new DocNavigator("lastName", "lastName"), new DocNavigator("gender", "gender"),
			new DocNavigator("location", "homeCity"), new DocNavigator("blob", "bio"),
			new DocNavigator("email", "contact", "email"), new DocNavigator("twitter", "contact", "twitter"),
			new DocNavigator("phone", "contact", "phone"), };

	@Override
	protected boolean loadStopPaging() {
		return false;
	}

}
