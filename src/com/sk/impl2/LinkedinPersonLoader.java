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
import com.sk.web.Request;

public class LinkedinPersonLoader extends AbstractLoader implements Extractor {

	private final Request request;
	private Document document;

	LinkedinPersonLoader(Element apiProfileRequest) throws MalformedURLException {
		this.request = new Request(getUrl(apiProfileRequest), "GET");
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
				return ret;
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