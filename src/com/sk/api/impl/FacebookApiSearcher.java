package com.sk.api.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk.Driver;
import com.sk.api.AbstractApiSearcher;
import com.sk.api.ApiUtility;
import com.sk.api.NameComparison;
import com.sk.threading.TaskGroup;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;
import com.sk.util.parse.AbstractParser;

public class FacebookApiSearcher extends AbstractApiSearcher {

	public FacebookApiSearcher() {
		super(new ApiUtility(FacebookApi.class));
		util.init("gtgrab");
	}

	private static final String SEARCH = "https://www.google.com/search?q=site%%3Afacebook.com%%20%s%%20%s";
	private static final String USER = "https://graph.facebook.com/%s";
	private static final JsonParser parser = new JsonParser();

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		List<PersonalData> data = new ArrayList<>();
		this.data.remove();
		for (String loc = getSearchRequest(first, last); loc != null; loc = parse(AbstractParser.getDocument(loc),
				data, first, last))
			;
		if (data.isEmpty())
			return false;
		this.data.set(data.toArray(new PersonalData[data.size()]));
		return true;
	}

	private String getSearchRequest(String first, String last) {
		try {
			return String.format(SEARCH, URLEncoder.encode(first, "UTF-8"), URLEncoder.encode(last, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	private static final int BAD_THRESHOLD = 3;
	private static final Pattern fbUrlMatch = Pattern.compile("^https?://www\\.facebook\\.com/([^/]+)$");

	private String parse(Document gsearchDoc, final List<PersonalData> data, String... names) {
		if (gsearchDoc == null)
			return null;
		TaskGroup tasks = new TaskGroup();
		NameComparison nameUtil = NameComparison.get();
		int badCount = 0;
		for (Element e : gsearchDoc.select("div.rc h3.r a")) {
			String href = e.attr("abs:href");
			Matcher m = fbUrlMatch.matcher(href);
			if (m.matches()) {
				String text = e.text();
				String[] parsed = text.split(" [|] ");
				if (parsed.length == 2 && parsed[1].equals("Facebook")
						&& nameUtil.isSameName(names, nameUtil.parseName(parsed[0]))) {
					try {
						final OAuthRequest person = new OAuthRequest(Verb.GET, String.format(USER,
								URLEncoder.encode(m.group(1), "UTF-8")));
						tasks.add(new Runnable() {
							@Override
							public void run() {
								Response resp = util.send(person);
								if (resp == null || resp.getBody() == null)
									return;
								JsonObject response = parser.parse(resp.getBody()).getAsJsonObject();
								FieldBuilder builder = new FieldBuilder();
								builder.put(response, "first_name", "firstName");
								builder.put(response, "last_name", "lastName");
								builder.put(response, "gender");
								builder.put(response, "email");
								builder.put(response, "id");
								PersonalData dat = new PersonalData("facebook");
								builder.addTo(dat);
								data.add(dat);
							}
						});
					} catch (UnsupportedEncodingException e1) {
						badCount++;
						continue;
					}
				}
			}
			if (badCount++ >= BAD_THRESHOLD)
				break;
		}
		tasks.submit(Driver.EXECUTOR);
		if (!tasks.waitFor())
			return null;
		Element e = gsearchDoc.select("#pnnext").first();
		if (e != null && badCount < BAD_THRESHOLD)
			return e.attr("abs:href");
		else
			return null;
	}
}
