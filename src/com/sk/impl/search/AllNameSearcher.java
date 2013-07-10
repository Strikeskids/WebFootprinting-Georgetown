package com.sk.impl.search;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sk.util.parse.search.NameSearcher;
import com.sk.util.parse.search.ScrapeSearcher;

public class AllNameSearcher extends ScrapeSearcher implements NameSearcher {

	private static final NameSearcher[] interior = { new GooglePlusSearcher(),
			GoogleSearcherImpl.LINKEDIN.searcher, GoogleSearcherImpl.TWITTER.searcher, new WhitepagesSearcher() };

	@Override
	public boolean parse() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		reset();
		List<URL> insert = new ArrayList<>();
		boolean ret = false;
		for (final NameSearcher n : interior) {
			boolean curParse = n.lookForName(first, last);
			ret |= curParse;
			if (curParse) {
				Collections.addAll(insert, n.results());
			}
		}
		this.urls.set(insert.toArray(new URL[insert.size()]));
		return ret;
	}

	@Override
	public void load(URL url) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void load(String a, String b) {
		throw new UnsupportedOperationException();
	}

}
