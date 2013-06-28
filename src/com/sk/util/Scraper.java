package com.sk.util;

import java.io.IOException;
import java.net.URL;

public interface Scraper {
	/**
	 * Attempts to load HTML from the source {@link URL}
	 * 
	 * @param url
	 *            The source {@link URL} to get
	 * @throws IOException
	 *             if the file failed to be loaded
	 */
	public void load(URL url) throws IOException;

	/**
	 * Attempts to load HTML from the source {@link String}
	 * 
	 * @param source
	 *            A {@link String} containing the HTML
	 */
	public void load(String source);

	/**
	 * Attempts to parse the loaded HTML. Use this method after loading the data
	 * 
	 * @return <code>true</code> if the scraper parsed the HTML successfully and extracted data;
	 *         <code>false</code> otherwise
	 * @throws IllegalStateException
	 *             if attempting to parse before loading the data
	 */
	public boolean parse() throws IllegalStateException;

	/**
	 * Gets the {@link PersonalData} that was parsed from the loaded HTML. Use this method after
	 * {@link #parse()}ing the data the data
	 * 
	 * @return The {@link PersonalData} that was parsed
	 * @throws IllegalStateException
	 *             if trying to get the PersonalData before any has been loaded and parsed
	 * @see #parse()
	 */
	public PersonalData get() throws IllegalStateException;
}
