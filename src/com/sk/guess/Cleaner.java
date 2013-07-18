package com.sk.guess;

import com.sk.guess.impl.GenderCleaner;
import com.sk.guess.impl.GenericCleaner;
import com.sk.guess.impl.LocationCleaner;
import com.sk.util.PersonalData;

/**
 * Class that provides cleaning capabilities to clean up data garnered via API or scraping.
 * 
 * @author Strikeskids
 * 
 */
public class Cleaner {

	private static final String[] GENERIC_FIELDS = { "jobTitle", "industry", "company", "education",
			"relationshipStatus" };
	private static final DataCleaner[] CLEANERS = new DataCleaner[] { new LocationCleaner(), new GenderCleaner(),
			new GenericCleaner(GENERIC_FIELDS) };

	public Cleaner() {
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
		for (DataCleaner cleaner : CLEANERS) {
			cleaner.clean(in, ret);
		}
		return ret;
	}

}
