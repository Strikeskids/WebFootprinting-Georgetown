package com.sk.util.parse.search;

import java.io.IOException;

public interface AddressSearcher extends Searcher {

	public void lookFor(String address, String cityStateZip) throws IOException;
}
