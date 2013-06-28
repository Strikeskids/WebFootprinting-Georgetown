package com.sk.util.parse;

import com.sk.util.PersonalData;

/**
 * A scraper for a specific site only (eg. LinkedIn, Facebook/ Google+)
 * 
 * @author Strikeskids
 * 
 */
public abstract class SpecificSiteScraper extends AbstractScraper {

	private final String siteId;

	public SpecificSiteScraper(String siteId) {
		this.siteId = siteId;
	}

	/**
	 * Gets the site that this scraper will scrape
	 * 
	 * @return The site id of this site
	 */
	public String getSite() {
		return siteId;
	}

	/**
	 * Creates a new {@link PersonalData} for the site that is scraped by this scraper
	 * 
	 * @return The new {@link PersonalData}
	 */
	protected PersonalData newData() {
		return new PersonalData(siteId);
	}
}
