package com.sk.impl.search;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import com.sk.util.parse.search.AbstractSearcher;
import com.sk.util.parse.search.NameSearcher;

public class AllNameSearcher extends AbstractSearcher implements NameSearcher {

	private static final NameSearcher[] interior = { GoogleSearcherImpl.GOOGLE_PLUS.searcher,
			GoogleSearcherImpl.LINKEDIN.searcher, GoogleSearcherImpl.TWITTER.searcher,
			new WhitepagesNameSearcher() };

	private static final ThreadLocal<BitSet> success = new ThreadLocal<BitSet>() {
		@Override
		public BitSet initialValue() {
			return new BitSet(interior.length);
		}
	};

	@Override
	public boolean parse() throws IllegalStateException {
		BitSet parses = success.get();
		boolean ret = false;
		List<URL> insert = new ArrayList<>();
		for (int i = 0; i < interior.length; ++i) {
			boolean curParse = interior[i].parse();
			parses.set(i, curParse);
			ret |= curParse;
			if (curParse) {
				Collections.addAll(insert, interior[i].results());
			}
		}
		this.urls.set(insert.toArray(new URL[insert.size()]));
		return ret;
	}

	@Override
	public void lookFor(String first, String last) throws IOException {
		for (NameSearcher n : interior) {
			n.lookFor(first, last);
		}
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
