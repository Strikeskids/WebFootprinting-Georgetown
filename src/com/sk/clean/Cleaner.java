package com.sk.clean;

import com.sk.clean.impl.ContactCleaner;
import com.sk.clean.impl.GenderCleaner;
import com.sk.clean.impl.GenericCleaner;
import com.sk.clean.impl.LocationCleaner;
import com.sk.clean.impl.WikipediaRedirectCleaner;
import com.sk.util.data.PersonalData;

public class Cleaner {

	private static final String[] GENERIC_FIELDS = { "company", "education", "relationshipStatus" },
			WIKIPEDIA_FIELDS = { "jobTitle", "industry" };
	private static final DataCleaner[] CLEANERS = new DataCleaner[] { new LocationCleaner(), new GenderCleaner(),
			new GenericCleaner(GENERIC_FIELDS), new WikipediaRedirectCleaner(WIKIPEDIA_FIELDS),
			new ContactCleaner() };

	public PersonalData clean(PersonalData in) {
		PersonalData dirty = new PersonalData(true, in);
		PersonalData ret = new PersonalData(false, in);
		for (DataCleaner cleaner : CLEANERS) {
			cleaner.clean(dirty, ret);
		}
		return ret;
	}

}
