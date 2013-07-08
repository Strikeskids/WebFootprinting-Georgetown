package com.sk.util.parse.search;

import java.net.URL;

public interface Searcher {

	/**
	 * Gets the found results
	 * 
	 * @return the {@link URL} results array
	 * @throws IllegalStateException
	 *             if the results method was called before searching
	 */
	public URL[] results() throws IllegalStateException;
}
