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

	/**
	 * Instantiates the {@link WebSource} with a copy of the websites and ids stored in the argument
	 * 
	 * @param copy
	 *            The {@link WebSource} to copy
	 */
	public WebSource(WebSource copy) {
		addAll(copy);
	}

	/**
	 * Instantiates the {@link WebSource} with the contents of the {@link String} array of ids and {@link URL}
	 * array provided
	 * 
	 * @param ids
	 *            The website ids to use
	 * @param urls
	 *            The {@link URL}s of the corresponding website ids
	 */
	public WebSource(String[] ids, URL[] urls) {
		addAll(ids, urls);
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
