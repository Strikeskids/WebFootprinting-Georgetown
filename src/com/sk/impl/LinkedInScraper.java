package com.sk.impl;

import com.sk.util.SiteScraperInfo;
import com.sk.util.parse.BasicGrabber;
import com.sk.util.parse.Grabber;
import com.sk.util.parse.GrabberSiteScraper;

@SiteScraperInfo(siteBase = "http://www.linkedin.com/", siteId = "linkedin")
public class LinkedInScraper extends GrabberSiteScraper {

	private static final Grabber[] grabbers = { new BasicGrabber("span.given-name", "first-name"),
			new BasicGrabber("span.family-name", "last-name"),
			new BasicGrabber("dd.summary-education ul li", "education"), new BasicGrabber("p.title", "job-title"),
			new BasicGrabber("dd span.locality", "location"), new BasicGrabber("dd.industry", "industry"),
			new BasicGrabber("div#profile-picture img", "src", "profile-picture-url") };

	public LinkedInScraper() {
		super(grabbers);
	}

}
