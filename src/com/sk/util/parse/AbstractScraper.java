package com.sk.util.parse;

import java.io.IOException;
import java.net.URL;

import com.sk.util.PersonalData;

public abstract class AbstractScraper extends AbstractParser implements Scraper {

	protected PersonalData data;

	public AbstractScraper() {
	}

	@Override
	public void load(URL url) throws IOException {
		data = null;
		super.load(url);
	}

	@Override
	public void load(String source, String baseURI) {
		data = null;
		super.load(source, baseURI);
	}

	@Override
	public PersonalData get() throws IllegalStateException {
		if (data == null)
			throw new IllegalStateException();
		else
			return data;
	}
}
