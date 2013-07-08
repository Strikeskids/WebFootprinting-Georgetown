package com.sk.util.parse.search;

import java.net.URL;

import com.sk.util.PersonalData;

public interface Searcher {

	/**
	 * Gets the found results
	 * 
	 * @return the {@link URL} results array
	 * @throws IllegalStateException
	 *             if the results method was called before searching
	 */
	public URL[] results() throws IllegalStateException;

	/**
	 * Gets the {@link PersonalData} from the found results
	 * 
	 * @return the {@link PersonalData} results array
	 * @throws IllegalStateException
	 */
	public PersonalData[] getData() throws IllegalStateException;
}
