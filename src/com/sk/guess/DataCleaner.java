package com.sk.guess;

import com.sk.util.PersonalData;

public interface DataCleaner {

	/**
	 * Cleans the {@link PersonalData} input and returns a new {@link PersonalData} with the new values
	 * 
	 * @param in
	 *            The {@link PersonalData} to clean
	 * @param out
	 *            The {@link PersonalData} to store the cleaned data
	 * @return if the cleaning was successful
	 */
	public boolean clean(PersonalData in, PersonalData out);
}
