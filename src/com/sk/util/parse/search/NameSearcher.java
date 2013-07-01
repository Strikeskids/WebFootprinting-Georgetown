package com.sk.util.parse.search;

import java.io.IOException;

public interface NameSearcher extends Searcher {
	public void lookFor(String first, String last) throws IOException;
}
