package com.sk.impl2;

import static com.sk.impl2.LinkedinApiLoader.SITE_KEY;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.parse.AbstractLoader;
import com.sk.parse.Extractor;
import com.sk.parse.Parsers;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;
import com.sk.util.parse.scrape.BasicGrabber;
import com.sk.util.parse.scrape.Grabber;
import com.sk.web.OAuthRequest;
import com.sk.web.Request;

public class LinkedinPersonLoader extends AbstractLoader implements Extractor {

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
	public List<PersonalData> call() throws Exception {
		return getResults();
	}

	@Override
	public List<PersonalData> getResults() {
		FieldBuilder builder = new FieldBuilder();
		for (Grabber grabber : grabbers) {
			grabber.grab(document, builder);
		}
		PersonalData data = new PersonalData(SITE_KEY);
		builder.addTo(data);
		return Arrays.asList(data);
	}

	@Override
	protected Request getRequest() {
		return request;
	}

	@Override
	protected void parse(URL source, String data) {
		document = Parsers.parseXML(data);
	}

	private static final Grabber[] grabbers = { new BasicGrabber("first-name", "firstName"),
			new BasicGrabber("last-name", "lastName"), new BasicGrabber("location name", "location"),
			new BasicGrabber("location country code", "country"),
			new BasicGrabber("person > industry", "industry"), new BasicGrabber("positions title", "jobTitle"),
			new BasicGrabber("positions company name", "company"), new BasicGrabber("person > summary", "blob"),
			new BasicGrabber("picture-url", "profilePictureUrl"), new BasicGrabber("main-address", "address"),
			new BasicGrabber("phone-number > phone-number", "phone"),
			new BasicGrabber("twitter-account > provider-account-name", "twitter") };

}
