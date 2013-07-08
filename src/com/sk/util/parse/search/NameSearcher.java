package com.sk.util.parse.search;

import java.io.IOException;

public interface NameSearcher extends Searcher {
	/**
	 * Attempts to look for the given names and parse them
	 * 
	 * @param first
	 * @param last
	 * @throws IOException
	 * @return if the parse was successful
	 */
	public boolean lookForName(String first, String last) throws IOException;
}
