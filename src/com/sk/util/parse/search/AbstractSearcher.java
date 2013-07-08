package com.sk.util.parse.search;

import java.net.URL;

public abstract class AbstractSearcher implements Searcher {

	protected final ThreadLocal<URL[]> urls = new ThreadLocal<>();

	@Override
	public URL[] results() throws IllegalStateException {
		URL[] ret = urls.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}

}
