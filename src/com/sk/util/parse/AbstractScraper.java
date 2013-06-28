package com.sk.util.parse;

import java.io.IOException;
import java.net.URL;

import com.sk.util.PersonalData;

public abstract class AbstractScraper extends AbstractParser implements Scraper {

	/**
	 * The personal data to return in {@link #get()}. Store attributes and values in here. Should only
	 * {@link ThreadLocal#set(Object)} this once the PersonalData has all been loaded
	 */
	protected ThreadLocal<PersonalData> personalData = new ThreadLocal<>();

	public AbstractScraper() {
	}

	@Override
	public void load(URL url) throws IOException {
		personalData.remove();
		super.load(url);
	}

	@Override
	public void load(String source, String baseURI) {
		personalData.remove();
		super.load(source, baseURI);
	}

	@Override
	public PersonalData get() throws IllegalStateException {
		PersonalData ret = personalData.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}
}
