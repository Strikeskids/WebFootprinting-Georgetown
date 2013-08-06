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
import com.sk.util.DocNavigator;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;
import com.sk.web.IOUtil;
import com.sk.web.Request;

public class PiplApiLoader extends PagingLoader {

	static final String SITE_KEY = "pipl";
	private static final String BASE_URL = "http://api.pipl.com/search/v3/json/?first_name=%s&last_name=%s";

	private final String url;
	private final String[] names;

	private JsonObject json;

	public PiplApiLoader(String first, String last) {
		names = new String[] { first, last };
		url = String.format(BASE_URL, IOUtil.urlEncode(first), IOUtil.urlEncode(last));
	}

	@Override
	protected boolean loadStopPaging() {
		return false;
	}

	@Override
	protected List<PersonalData> loadOwnResults() {
		List<PersonalData> ret = new ArrayList<>();
		for (JsonElement recordElement : getRecords()) {
			PersonalData record = getData(recordElement.getAsJsonObject());
			if (record != null)
				ret.add(record);
		}
		return ret;
	}

	private JsonArray getRecords() {
		init();
		return json.get("records").getAsJsonArray();
	}

	private PersonalData getData(JsonObject record) {
		FieldBuilder builder = new FieldBuilder();
		for (DocNavigator navigator : navigators) {
			navigator.navigate(record, builder);
		}
		if (!builder.compareNames(names))
			return null;
		PersonalData ret = new PersonalData(SITE_KEY);
		builder.addTo(ret);
		return ret;
	}

	@Override
	protected PagingLoader createNextPage() {
		return null;
	}

	@Override
	protected Request getRequest() {
		try {
			Request request = new Request(url, "GET");
			request.addQuery("key", ApiUtility.getAccessToken(SITE_KEY).getKey());
			return request;
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	@Override
	protected void parse(URL source, String data) {
		json = Parsers.parseJSON(data).getAsJsonObject();
	}

	private static final DocNavigator[] navigators = { new DocNavigator("name", "names", "display"),
			new DocNavigator("firstName", "names", "first"), new DocNavigator("lastName", "names", "last"),
			new DocNavigator("address", "addresses", "display"), new DocNavigator("phone", "phones", "display"),
			new DocNavigator("email", "emails", "address"), new DocNavigator("age", "dobs", "display"),
			new DocNavigator("profilePictureUrl", "images", "url"), new DocNavigator("jobTitle", "jobs", "title"),
			new DocNavigator("company", "jobs", "organization"), new DocNavigator("industry", "jobs", "industry"),
			new DocNavigator("education", "educations", "display"),
			new DocNavigator("username", "usernames", "content"), };

}
