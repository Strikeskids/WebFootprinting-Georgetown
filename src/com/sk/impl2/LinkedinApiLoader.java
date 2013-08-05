package com.sk.impl2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sk.api.NameComparison;
import com.sk.parse.Extractor;
import com.sk.parse.OuterLoader;
import com.sk.parse.PagingLoader;
import com.sk.parse.Parsers;
import com.sk.web.IOUtil;
import com.sk.web.OAuthRequest;
import com.sk.web.Request;

public class LinkedinApiLoader extends OuterLoader {

	private static final int STEP_AMOUNT = 25;
	private static final String BASE_URL = "http://api.linkedin.com/v1/people-search:(people:"
			+ "(api-standard-profile-request,first-name,last-name),num-results)?count=" + STEP_AMOUNT
			+ "&first-name=%s&last-name=%s&start=%d";
	static final String SITE_KEY = "linkedin";

	private static final String PROFILE_REQUEST_ELEMENT = "api-standard-profile-request";

	private final String first, last, names[];
	private final int startIndex;
	private final String url;

	private Document document;

	public LinkedinApiLoader(String first, String last) {
		this(new String[] { first, last }, 0);
	}

	private LinkedinApiLoader(String[] names, int startIndex) {
		this.first = names[0];
		this.last = names[1];
		this.names = names;
		this.startIndex = startIndex;
		this.url = String.format(BASE_URL, IOUtil.urlEncode(first), IOUtil.urlEncode(last), startIndex);
	}

	@Override
	protected List<Extractor> getExtractors() {
		List<Extractor> ret = new ArrayList<>();
		boolean stop = false;
		for (Element person : getPeople()) {
			if (!checkNames(person)) {
				stop = true;
				break;
			}
			try {
				ret.add(new LinkedinPersonLoader(person.select(PROFILE_REQUEST_ELEMENT).first()));
			} catch (MalformedURLException ignored) {
			}
		}
		stopPaging.set(stop);
		return ret;
	}

	@Override
	protected PagingLoader createNextPage() {
		init();
		int numResults = getNumResults();
		int nextStart = startIndex + STEP_AMOUNT;
		if (nextStart >= numResults)
			return null;
		if (stopPaging.get())
			return null;
		return new LinkedinApiLoader(names, nextStart);
	}

	@Override
	protected boolean loadStopSearching() {
		for (Element person : getPeople()) {
			if (!checkNames(person))
				return true;
		}
		return false;
	}

	private Elements getPeople() {
		return document.select("person");
	}

	private boolean checkNames(Element person) {
		return NameComparison.get().isSameName(getNames(person), names);
	}

	private String[] getNames(Element person) {
		String first = person.select("first-name").text();
		String last = person.select("last-name").text();
		return new String[] { first, last };
	}

	private int getNumResults() {
		init();
		Element elem = document.select("num-results").first();
		if (elem == null)
			return 0;
		else
			return Integer.parseInt(elem.text());
	}

	@Override
	protected Request getRequest() {
		try {
			OAuthRequest ret = new OAuthRequest(url, "GET");
			ret.signOAuth(ApiUtility.getConsumerToken(SITE_KEY), ApiUtility.getOAuthToken(SITE_KEY));
			return ret;
		} catch (MalformedURLException e) {
			throw new RuntimeException("Failed to make URL");
		}
	}

	@Override
	protected void parse(URL source, String data) {
		document = Parsers.parseXML(data);
	}

}
