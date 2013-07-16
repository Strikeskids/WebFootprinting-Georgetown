package com.sk.impl;

import com.sk.util.SiteScraperInfo;
import com.sk.util.parse.scrape.BasicGrabber;
import com.sk.util.parse.scrape.Grabber;
import com.sk.util.parse.scrape.GrabberSiteScraper;

@SiteScraperInfo(siteBase = "http://www.linkedin.com/", siteId = "linkedin")
public class LinkedInScraper extends GrabberSiteScraper {

	private static final Grabber[] grabbers = { new BasicGrabber("span.given-name", "firstName"),
			new BasicGrabber("span.family-name", "lastName"),
			new BasicGrabber("dd.summary-education ul li", "education"), new BasicGrabber("p.title", "jobTitle"),
			new BasicGrabber("dd span.locality", "location"), new BasicGrabber("dd.industry", "industry"),
			new BasicGrabber("div#profile-picture img", "src", "profilePictureUrl"),
			new BasicGrabber("span.full-name", "name") };

	public LinkedInScraper() {
		super(grabbers);
	}

}
