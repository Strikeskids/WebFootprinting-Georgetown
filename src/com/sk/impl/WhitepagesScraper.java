package com.sk.impl;

import com.sk.util.SiteScraperInfo;
import com.sk.util.parse.scrape.BasicGrabber;
import com.sk.util.parse.scrape.Grabber;
import com.sk.util.parse.scrape.GrabberSiteScraper;

@SiteScraperInfo(siteBase = "http://www.whitepages.com/", siteId = "whitepages")
public class WhitepagesScraper extends GrabberSiteScraper {

	private static final Grabber[] grabbers = { new BasicGrabber("div.address.adr", "address"),
			new BasicGrabber("span.given-name", "firstName"), new BasicGrabber("span.family-name", "lastName"),
			new BasicGrabber("span.name.fn", "name"),
			new BasicGrabber("div.address.adr span.postal-code", "zipcode"),
			new BasicGrabber("div.address.adr span.locality", "city"),
			new BasicGrabber("div.address.adr span.region", "state"),
			new BasicGrabber("tr:contains(Age) td:not(:contains(Age)) span", "age"),
			new BasicGrabber("p.single_result_phone", "phone") };

	public WhitepagesScraper() {
		super(grabbers);
	}

}
