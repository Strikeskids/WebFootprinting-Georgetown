package com.sk.util.parse.scrape;

import com.sk.util.PersonalData;
import com.sk.util.parse.AbstractParser;

public abstract class AbstractScraper extends AbstractParser implements Scraper {

	/**
	 * The personal data to return in {@link #get()}. Store attributes and values in here. Should only
	 * {@link ThreadLocal#set(Object)} this once the PersonalData has all been loaded
	 */
	protected ThreadLocal<PersonalData> personalData = new ThreadLocal<>();

	public AbstractScraper() {
	}

	@Override
	protected void reset() {
		super.reset();
		personalData.remove();
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
