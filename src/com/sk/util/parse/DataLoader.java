package com.sk.util.parse;

import com.sk.util.PersonalData;

public interface DataLoader {
	/**
	 * Gets the {@link PersonalData} that was loaded from the source
	 * 
	 * @throws IllegalStateException
	 *             if calling this method before actually loading the data
	 * @return The {@link PersonalData} loaded
	 */
	public PersonalData get() throws IllegalStateException;
}
