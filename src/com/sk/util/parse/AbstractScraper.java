package com.sk.util.parse;

import java.io.IOException;
import java.net.URL;

import com.sk.util.PersonalData;

public abstract class AbstractScraper extends AbstractParser implements Scraper {

	protected ThreadLocal<PersonalData> data;

	public AbstractScraper() {
	}

	@Override
	public void load(URL url) throws IOException {
		data.remove();
		super.load(url);
	}

	@Override
	public void load(String source, String baseURI) {
		data.remove();
		super.load(source, baseURI);
	}

	@Override
	public PersonalData get() throws IllegalStateException {
		PersonalData ret = data.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}
}
