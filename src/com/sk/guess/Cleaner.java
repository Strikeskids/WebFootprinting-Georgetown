package com.sk.guess;

import com.sk.guess.impl.LocationCleaner;
import com.sk.util.PersonalData;

public class Cleaner {

	private static final DataCleaner[] cleaners = new DataCleaner[] { new LocationCleaner() };

	public Cleaner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Cleans the input {@link PersonalData} and returns a new instance with clean values
	 * 
	 * @param in
	 *            The {@link PersonalData} input
	 * @return The {@link PersonalData} new instance with cleaned values
	 */
	public PersonalData clean(PersonalData in) {
		PersonalData ret = new PersonalData(in.getWebsiteId());
		for (DataCleaner cleaner : cleaners) {
			cleaner.clean(in, ret);
		}
		return ret;
	}

}
