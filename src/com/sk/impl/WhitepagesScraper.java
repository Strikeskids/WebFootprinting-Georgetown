package com.sk.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.sk.util.PersonalData;
import com.sk.util.parse.BasicGrabber;
import com.sk.util.parse.Grabber;
import com.sk.util.parse.GrabberSiteScraper;

public class WhitepagesScraper extends GrabberSiteScraper {

	private static final Grabber[] grabbers = { new BasicGrabber("div.address.adr", "address"),
			new BasicGrabber("span.given-name", "first-name"), new BasicGrabber("span.family-name", "last-name"),
			new BasicGrabber("tr:contains(Age) td:not(:contains(Age)) span", "age"), new Grabber() {
				private final Pattern phonePattern = Pattern
						.compile("(Home|Work) \\((\\d{3})\\) (\\d{3})-(\\d{4})");

				@Override
				public boolean grab(Document source, PersonalData destination) {
					Elements phone = source.select("p.single_result_phone");
					if (!phone.isEmpty()) {
						String text = phone.first().text();
						Matcher matcher = phonePattern.matcher(text);
						if (matcher.find()) {
							destination.put("phone-" + matcher.group(1).toLowerCase(),
									matcher.group(2) + matcher.group(3) + matcher.group(4));
							return true;
						}
					}
					return false;
				}
			} };

	public WhitepagesScraper(String siteId) {
		super(siteId, grabbers);
		// TODO Auto-generated constructor stub
	}

}
