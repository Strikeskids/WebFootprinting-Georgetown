package com.sk.impl;

import java.util.regex.Pattern;

import com.sk.util.SiteScraperInfo;
import com.sk.util.parse.scrape.BasicGrabber;
import com.sk.util.parse.scrape.Grabber;
import com.sk.util.parse.scrape.GrabberSiteScraper;
import com.sk.util.parse.scrape.RegexGrabber;

@SiteScraperInfo(siteBase = "http://www.zillow.com", siteId = "zillow")
public class ZillowScraper extends GrabberSiteScraper {

	private static final Grabber[] grabbers = {
			new BasicGrabber(".prop-addr", "address"),
			new BasicGrabber(".label:contains(Zestimate) + .value", "house-value"),
			new RegexGrabber(".prop-facts-value:contains(beds)", Pattern.compile("(?<r>[0-9.]+)"), "num-bedrooms"),
			new RegexGrabber(".prop-facts-value:contains(bath)", Pattern.compile("(?<r>[0-9.]+)"), "num-bathrooms"),
			new RegexGrabber("div.prop-facts ul li:nth-child(3) .prop-facts-label", Pattern.compile("(?<r>.+):"),
					"property-type"), new BasicGrabber(".prop-facts-value:contains(sq ft)", "house-size"),
			new BasicGrabber(".prop-facts-label:contains(Lot) + .prop-facts-value", "lot-size"),
			new BasicGrabber(".prop-facts-label:contains(Year) + .prop-facts-value", "year-built") };

	public ZillowScraper() {
		super(grabbers);
	}

}
