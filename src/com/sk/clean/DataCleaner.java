package com.sk.clean;

import com.sk.util.data.PersonalData;

public interface DataCleaner {

	/**
	 * Cleans the {@link PersonalData} input and returns a new {@link PersonalData} with the new values Also
	 * removes any attributes related to allow easier cleaning in the future
	 * 
	 * @param dirty
	 *            The {@link PersonalData} to clean
	 * @param clean
	 *            The {@link PersonalData} to store the cleaned data
	 * @return if the cleaning was successful
	 */
	public boolean clean(PersonalData dirty, PersonalData clean);
}
