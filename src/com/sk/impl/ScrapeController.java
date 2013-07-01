package com.sk.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;
import com.sk.util.PersonalData;
import com.sk.util.SiteScraperInfo;
import com.sk.util.WebSource;
import com.sk.util.parse.scrape.AbstractScraper;
import com.sk.util.parse.scrape.Scraper;

public class ScrapeController extends AbstractScraper {

	private Map<String, Scraper> scrapers = new HashMap<>();
	private ThreadLocal<Scraper> current = new ThreadLocal<>();

	private static ScrapeController singleton;

	public static ScrapeController getController() {
		if (singleton == null) {
			synchronized (ScrapeController.class) {
				if (singleton == null)
					singleton = new ScrapeController();
			}
		}
		return singleton;
	}

	private ScrapeController() {
		// Initialize the scrapers
		addScraper(LinkedInScraper.class);
		addScraper(WhitepagesScraper.class);
		addScraper(GooglePlusScraper.class);
		addScraper(TwitterScraper.class);
		addScraper(ZillowScraper.class);
	}

	private <T extends Scraper> void addScraper(Class<T> clazz) {
		if (!clazz.isAnnotationPresent(SiteScraperInfo.class))
			throw new IllegalArgumentException();
		try {
			SiteScraperInfo info = clazz.getAnnotation(SiteScraperInfo.class);
			scrapers.put(info.siteId(), clazz.newInstance());
			WebSource.get().addSite(info.siteId(), new URL(info.siteBase()));
		} catch (InstantiationException | IllegalAccessException | MalformedURLException ignored) {
		}
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

	/**
	 * Does the entire parsing process from start to finish. Convinience method
	 * 
	 * @param url
	 *            The {@link URL} to parse
	 * @return The {@link PersonalData} parsed
	 */
	public Optional<PersonalData> loadAndParse(URL url) {
		try {
			load(url);
			if (!parse())
				return Optional.absent();
			return Optional.of(get());
		} catch (IOException ignored) {
			return Optional.absent();
		}
	}

}
