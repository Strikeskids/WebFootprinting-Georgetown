package com.sk.util.parse.search;

import java.io.IOException;
import java.net.URL;

import com.sk.util.parse.AbstractParser;

public abstract class AbstractParseSearcher extends AbstractParser implements Searcher {

	protected final ThreadLocal<URL[]> urls = new ThreadLocal<>();

	@Override
	public void load(URL url) throws IOException {
		urls.remove();
		super.load(url);
	}

	@Override
	public void load(String source, String baseURI) {
		urls.remove();
		super.load(source, baseURI);
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
