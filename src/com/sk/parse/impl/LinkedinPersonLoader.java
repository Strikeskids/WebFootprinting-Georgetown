package com.sk.parse.impl;

import static com.sk.parse.impl.LinkedinApiLoader.SITE_KEY;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.parse.util.IndividualExtractor;
import com.sk.parse.util.Parsers;
import com.sk.util.ApiUtility;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;
import com.sk.util.navigate.FieldNavigator;
import com.sk.util.navigate.DomNavigator;
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
		addHeaders(apiProfileRequest);
		this.request.signOAuth(ApiUtility.getConsumerToken(SITE_KEY), ApiUtility.getOAuthToken(SITE_KEY));
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
		init();
		FieldBuilder builder = new FieldBuilder();
		for (FieldNavigator grabber : navigators) {
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

	private static final FieldNavigator[] navigators = { new DomNavigator("firstName", "first-name"),
			new DomNavigator("lastName", "last-name"),
			new DomNavigator("location", "location", "name"),
			new DomNavigator("country", "location", "country", "code"),
			new DomNavigator("industry", "person", "industry"),
			new DomNavigator("jobTitle", "positions", "title"),
			new DomNavigator("company", "positions", "company", "name"),
			new DomNavigator("blob", "person", "summary"),
			new DomNavigator("profilePictureUrl", "picture-url"),
			new DomNavigator("address", "main-address"),
			new DomNavigator("phone", "phone-number", "phone-number"),
			new DomNavigator("twitter", "twitter-account", "provider-account-name"), };

}
