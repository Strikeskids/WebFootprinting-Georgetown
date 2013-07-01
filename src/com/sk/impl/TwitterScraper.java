package com.sk.impl;

import com.sk.util.SiteScraperInfo;
import com.sk.util.parse.BasicGrabber;
import com.sk.util.parse.Grabber;
import com.sk.util.parse.GrabberSiteScraper;

@SiteScraperInfo(siteBase = "https://twitter.com", siteId = "twitter")
public class TwitterScraper extends GrabberSiteScraper {

	private static final Grabber[] grabbers = { new BasicGrabber(".fullname .profile-field", "name"),
			new BasicGrabber(".location-and-url > span.url a", "title", "home-page"),
			new BasicGrabber(".location-and-url .location", "location") };

	public TwitterScraper() {
		super(grabbers);
	}

}
