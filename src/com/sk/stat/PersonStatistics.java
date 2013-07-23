package com.sk.stat;

import java.util.LinkedHashMap;

public class PersonStatistics extends LinkedHashMap<String, AttributeStatistics> {

	private static final long serialVersionUID = -7036835670634389519L;
	private final String firstName, lastName;

	public PersonStatistics(String first, String last) {
		this.firstName = first;
		this.lastName = last;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	@Override
	public String toString() {
		return String.format("Stat %s %s: %s", firstName, lastName, super.toString());
	}

}
