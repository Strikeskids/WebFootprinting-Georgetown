package com.sk.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;
import com.sk.util.WebSource;
import com.sk.util.parse.AbstractScraper;
import com.sk.util.parse.Scraper;

public class ScrapeController extends AbstractScraper {

	private Map<String, Scraper> scrapers = new HashMap<>();
	private ThreadLocal<Scraper> current = new ThreadLocal<>();;

	public ScrapeController() {
		// Initialize the scrapers
		scrapers.put("linkedin", new LinkedInScraper("linkedin"));
		scrapers.put("whitepages", new WhitepagesScraper("whitepages"));
	}

	@Override
	public void load(URL url) throws IOException {
		Optional<Scraper> scraper = setupScraper(url.getHost());
		if (scraper.isPresent())
			scraper.get().load(url);
	}

	@Override
	public void load(String html, String baseURI) {
		try {
			Optional<Scraper> scraper = setupScraper(new URL(baseURI).getHost());
			if (scraper.isPresent())
				scraper.get().load(html, baseURI);
		} catch (MalformedURLException ignored) {
		}
	}

	/**
	 * Attempts to setup the subScraper for the given host
	 * 
	 * @param host
	 *            The host to set up
	 * @return The {@link Scraper} setup if successful
	 */
	private Optional<Scraper> setupScraper(String host) {
		Optional<String> siteId = WebSource.get().getSiteId(host);
		if (siteId.isPresent() && scrapers.containsKey(siteId.get())) {
			Scraper subScraper = scrapers.get(siteId.get());
			current.set(subScraper);
			return Optional.of(subScraper);
		}
		return Optional.absent();
	}

	@Override
	public boolean parse() throws IllegalStateException {
		Optional<Scraper> sub = Optional.fromNullable(current.get());
		if (sub.isPresent() && sub.get().parse()) {
			personalData.set(sub.get().get());
			return true;
		} else {
			return false;
		}
	}

}
