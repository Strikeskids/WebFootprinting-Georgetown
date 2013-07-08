package com.sk.util.parse.search;

import java.net.URL;

import com.sk.util.parse.AbstractParser;

public abstract class AbstractParseSearcher extends AbstractParser implements Searcher {

	protected final ThreadLocal<URL[]> urls = new ThreadLocal<>();
	
	@Override
	protected void reset() {
		super.reset();
		urls.remove();
	}

	@Override
	public URL[] results() throws IllegalStateException {
		URL[] ret = urls.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}

}
