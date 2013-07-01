package com.sk.util.parse.scrape;

import org.jsoup.nodes.Document;

import com.sk.util.PersonalData;

public interface Grabber {
	/**
	 * Attempts to grab data from the source and place it in the destination
	 * 
	 * @param source
	 *            The {@link Document} source to grab data from.
	 * @param destination
	 *            The {@link PersonalData} destination to place it in
	 * @return <code>true</code> if successfully grabbed data; <code>false</code> otherwise
	 */
	public boolean grab(Document source, PersonalData destination);
}
