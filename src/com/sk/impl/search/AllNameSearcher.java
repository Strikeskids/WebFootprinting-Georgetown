package com.sk.impl.search;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sk.util.parse.search.AbstractParseSearcher;
import com.sk.util.parse.search.NameSearcher;

public class AllNameSearcher extends AbstractParseSearcher implements NameSearcher {

	private static final NameSearcher[] interior = { GoogleSearcherImpl.GOOGLE_PLUS.searcher,
			GoogleSearcherImpl.LINKEDIN.searcher, GoogleSearcherImpl.TWITTER.searcher,
			new WhitepagesSearcher() };

	@Override
	public boolean parse() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean lookForName(String first, String last) throws IOException {
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
