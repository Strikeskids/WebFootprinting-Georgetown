package com.sk.util.parse.scrape;

import org.jsoup.nodes.Document;

import com.sk.util.FieldBuilder;
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
		FieldBuilder builder = new FieldBuilder();
		boolean ret = false;
		for (Grabber g : grabbers) {
			ret |= g.grab(doc, builder);
		}
		if (ret) {
			PersonalData storage = newData();
			builder.addTo(storage);
			personalData.set(storage);
		}
		return ret;
	}

}
