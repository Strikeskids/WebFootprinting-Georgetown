package com.sk.util.parse;

import java.io.IOException;
import java.net.URL;

/**
 * An interface for parsing HTML. Contains basic methods to structure parsing
 * 
 * @author Strikeskids
 * 
 */
public interface Parser {
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
	 * @param baseURI
	 *            The base URI to use if a relative link is encountered in the page
	 */
	public void load(String source, String baseURI);

	/**
	 * Attempts to parse the loaded HTML. Use this method after loading the data
	 * 
	 * @return <code>true</code> if the scraper parsed the HTML successfully and extracted data;
	 *         <code>false</code> otherwise
	 * @throws IllegalStateException
	 *             if attempting to parse before loading the data
	 */
	public boolean parse() throws IllegalStateException;
}
