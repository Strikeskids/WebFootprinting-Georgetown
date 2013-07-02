package com.sk.impl;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sk.impl.search.WhitepagesNameSearcher;
import com.sk.util.PersonalData;
import com.sk.util.SiteScraperInfo;
import com.sk.util.parse.scrape.BasicGrabber;
import com.sk.util.parse.scrape.Grabber;
import com.sk.util.parse.scrape.GrabberSiteScraper;

@SiteScraperInfo(siteBase = "http://www.whitepages.com/", siteId = "whitepages")
public class WhitepagesScraper extends GrabberSiteScraper {

	private static final WhitepagesNameSearcher wns = new WhitepagesNameSearcher();
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
			}, new Grabber() {
				@Override
				public boolean grab(Document source, PersonalData destination) {
					boolean ret = false;
					for (Element adjacent : source.select("td:contains(Associated) + td a")) {
						try {
							wns.load(new URL(adjacent.attr("abs:href")));
						} catch (IOException ex) {
							continue;
						}
						if (!wns.parse())
							continue;
						ret |= wns.results().length > 0;
						destination.addAdjacent(wns.results());
					}
					return ret;
				}

			} };

	public WhitepagesScraper() {
		super(grabbers);
	}

}
