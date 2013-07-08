package com.sk.util.parse.search;

import java.io.IOException;

public interface AddressSearcher extends Searcher {

	/**
	 * Attempts to look for the given address and cityStateZip and parse them
	 * 
	 * @param address
	 *            The address to look for
	 * @param cityStateZip
	 *            The city and/or state and/or zip to look for
	 * @throws IOException
	 * @return if the parse was successful
	 */
	public boolean lookForAddress(String address, String cityStateZip) throws IOException;
}
