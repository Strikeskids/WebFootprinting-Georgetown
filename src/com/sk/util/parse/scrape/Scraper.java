package com.sk.util.parse.scrape;

import com.sk.util.PersonalData;
import com.sk.util.parse.DataLoader;
import com.sk.util.parse.Parser;

/**
 * A {@link PersonalData} scraper. Scrapes from various website sources
 * 
 * @author Strikeskids
 * 
 */
public interface Scraper extends Parser, DataLoader {

	/**
	 * Gets the {@link PersonalData} that was parsed from the loaded HTML. Use this method after
	 * {@link #parse()}ing the data the data
	 * 
	 * @return The {@link PersonalData} that was parsed
	 * @throws IllegalStateException
	 *             if trying to get the PersonalData before any has been loaded and parsed
	 * @see #parse()
	 */
	@Override
	public PersonalData get() throws IllegalStateException;
}
