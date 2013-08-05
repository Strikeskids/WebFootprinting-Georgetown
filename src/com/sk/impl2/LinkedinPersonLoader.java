package com.sk.impl2;

import static com.sk.impl2.LinkedinApiLoader.SITE_KEY;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.parse.IndividualExtractor;
import com.sk.parse.Parsers;
import com.sk.util.DocNavigator;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;
import com.sk.util.UniversalDocNavigator;
import com.sk.web.OAuthRequest;
import com.sk.web.Request;

public class LinkedinPersonLoader extends IndividualExtractor {

	private static final String REQUEST_FIELDS = ":(first-name,last-name,headline,"
			+ "location:(name,country:(code)),industry,summary,specialties,positions,"
			+ "picture-url,main-address,phone-numbers,twitter-accounts)";

	private final OAuthRequest request;
	private Document document;

	LinkedinPersonLoader(Element apiProfileRequest) throws MalformedURLException {
		this.request = new OAuthRequest(getUrl(apiProfileRequest), "GET");
		this.request.signOAuth(ApiUtility.getConsumerToken(SITE_KEY), ApiUtility.getOAuthToken(SITE_KEY));
		addHeaders(apiProfileRequest);
	}

	private void addHeaders(Element apiProfileRequest) {
		for (Element headerInfo : apiProfileRequest.select("http-header")) {
			String key = headerInfo.select("name").text();
			String value = headerInfo.select("value").text();
			request.addHeader(key, value);
		}
	}

	private String getUrl(Element apiProfileRequest) {
		for (Element urlElement : apiProfileRequest.select("url")) {
			String ret = urlElement.text();
			if (ret.length() > 10)
				return ret + REQUEST_FIELDS;
		}
		return "";
	}

	@Override
	protected PersonalData getResult() {
		FieldBuilder builder = new FieldBuilder();
		for (DocNavigator grabber : navigators) {
			grabber.navigate(document, builder);
		}
		builder.joinNames();
		PersonalData data = new PersonalData(SITE_KEY);
		builder.addTo(data);
		return data;
	}

	@Override
	protected Request getRequest() {
		return request;
	}

	@Override
	protected void parse(URL source, String data) {
		document = Parsers.parseXML(data);
	}

	private static final DocNavigator[] navigators = { new UniversalDocNavigator("firstName", "first-name"),
			new UniversalDocNavigator("lastName", "last-name"),
			new UniversalDocNavigator("location", "location", "name"),
			new UniversalDocNavigator("country", "location", "country", "code"),
			new UniversalDocNavigator("industry", "person", "industry"),
			new UniversalDocNavigator("jobTitle", "positions", "title"),
			new UniversalDocNavigator("company", "positions", "company", "name"),
			new UniversalDocNavigator("blob", "person", "summary"),
			new UniversalDocNavigator("profilePictureUrl", "picture-url"),
			new UniversalDocNavigator("address", "main-address"),
			new UniversalDocNavigator("phone", "phone-number", "phone-number"),
			new UniversalDocNavigator("twitter", "twitter-account", "provider-account-name"), };

}
