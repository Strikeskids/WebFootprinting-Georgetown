package com.sk.impl;

import com.sk.util.SiteScraperInfo;
import com.sk.util.parse.BasicGrabber;
import com.sk.util.parse.Grabber;
import com.sk.util.parse.GrabberSiteScraper;

@SiteScraperInfo(siteBase = "https://plus.google.com", siteId = "g+")
public class GooglePlusScraper extends GrabberSiteScraper {

	private static final Grabber[] grabbers = { new BasicGrabber("div[guidedhelpid=profile_name]", "name"),
			new BasicGrabber("span:contains(Works) + span.FG2oob", "job-title"),
			new BasicGrabber("span:contains(Attended) + span.FG2oob", "education"),
			new BasicGrabber("span:contains(Lives) + span.FG2oob", "location") };

	public GooglePlusScraper() {
		super(grabbers);
	}

}