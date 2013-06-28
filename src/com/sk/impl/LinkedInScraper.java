package com.sk.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sk.util.PersonalData;
import com.sk.util.parse.SpecificSiteScraper;

public class LinkedInScraper extends SpecificSiteScraper {

	public LinkedInScraper(String siteId) {
		super(siteId);
	}

	@Override
	public boolean parse() throws IllegalStateException {
		Document doc = this.doc.get();
		if (doc == null)
			throw new IllegalStateException("Need to load before parsing");
		PersonalData ret = newData();

		Elements givenName = doc.getElementsByClass("given-name");
		if (!givenName.isEmpty())
			ret.put("first-name", givenName.first().text());
		Elements familyName = doc.getElementsByClass("family-name");
		if (!familyName.isEmpty())
			ret.put("last-name", familyName.first().text());
		Elements industry = doc.getElementsByClass("industry");
		if (!industry.isEmpty())
			ret.put("industry", industry.first().text());
		Element pfpDiv = doc.getElementById("profile-picture");
		if (pfpDiv != null) {
			Elements pfp = pfpDiv.getElementsByTag("img");
			if (!pfp.isEmpty())
				ret.put("profile-picture-url", pfp.first().attributes().get("src"));
		}
		Elements education = doc.getElementsByClass("summary-education");
		if (!education.isEmpty())
			ret.put("education", education.first().text());
		Elements title = doc.getElementsByClass("title");
		if (!title.isEmpty())
			ret.put("title", title.first().text());
		Elements location = doc.getElementsByClass("locality");
		if (!location.isEmpty())
			ret.put("location", location.first().text());
		personalData.set(ret);
		return true;
	}

}
