package com.sk.util;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

public class PersonalData extends HashMap<String, String> {

	private String webId;

	public PersonalData(String source) {
	}

	public PersonalData(String source, int initialCapacity) {
		super(initialCapacity);
	}

	public PersonalData(String source, Map<? extends String, ? extends String> m) {
		super(m);
	}

	public PersonalData(String source, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public Optional<String> get(String attribute) {
		if (containsKey(attribute))
			return Optional.of(super.get(attribute));
		else
			return Optional.absent();
	}

	/**
	 * Get the website id of the source of the {@link PersonalData}
	 * 
	 * @return The website id
	 */
	public String getWebsiteId() {
		return webId;
	}

	private static final long serialVersionUID = 4259139787451939836L;
}
