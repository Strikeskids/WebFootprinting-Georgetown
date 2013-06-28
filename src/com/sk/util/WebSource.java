package com.sk.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

/**
 * A storage class for all the websites used. C
 * 
 * @author Strikeskids
 * 
 */
public class WebSource {

	private Map<String, URL> websites = new HashMap<>();

	private WebSource() {

	}

	private static WebSource source;

	/**
	 * Gets the singleton {@link WebSource}
	 * 
	 * @return The {@link WebSource} for the application
	 */
	public static WebSource get() {
		if (source == null) {
			synchronized (WebSource.class) {
				if (source == null)
					source = new WebSource();
			}
		}
		return source;
	}

	/**
	 * Gets the {@link URL} to the site from the specified website id
	 * 
	 * @param siteId
	 *            The website id
	 * @return The {@link URL} of the site or an absent {@link Optional} if the id was not found
	 */
	public Optional<URL> getUrl(String siteId) {
		if (websites.containsKey(siteId))
			return Optional.of(websites.get(siteId));
		else
			return Optional.absent();
	}

	/**
	 * Adds the specified site to the {@link WebSource}
	 * 
	 * @param siteId
	 *            The id of the site to add
	 * @param url
	 *            The base {@link URL} for the site
	 */
	public void addSite(String siteId, URL url) {
		websites.put(siteId, url);
	}

	public void addAll(WebSource source) {
		addAll(source.websites);
	}

	public void addAll(Map<String, URL> source) {
		websites.putAll(source);
	}

	public void addAll(String[] siteIds, URL[] urls) {
		for (int i = 0; i < siteIds.length && i < urls.length; ++i)
			addSite(siteIds[i], urls[i]);
	}

}
