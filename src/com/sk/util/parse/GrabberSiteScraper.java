package com.sk.util.parse;

import org.jsoup.nodes.Document;

import com.sk.util.PersonalData;

/**
 * A site scraper that uses the grabbers to grab data from the source
 * 
 * @author Strikeskids
 * 
 */
public class GrabberSiteScraper extends SpecificSiteScraper {

	private final Grabber[] grabbers;

	public GrabberSiteScraper(Grabber... grabbers) {
		this.grabbers = grabbers;
	}

	@Override
	public boolean parse() throws IllegalStateException {
		Document doc = this.doc.get();
		if (doc == null)
			throw new IllegalStateException();
		PersonalData storage = newData();
		boolean ret = false;
		for (Grabber g : grabbers) {
			ret |= g.grab(doc, storage);
		}
		if (ret)
			personalData.set(storage);
		return ret;
	}

}
