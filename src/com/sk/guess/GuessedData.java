package com.sk.guess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sk.util.PersonalData;

public class GuessedData extends HashMap<String, GuessedField> {

	private static final long serialVersionUID = 2510701995263580166L;

	private final String firstName, lastName;
	private final double threshold;

	/**
	 * Instantiates this GuessedData with the given first and last names and guess threshold
	 * 
	 * @param first
	 *            First name
	 * @param last
	 *            Last name
	 * @param threshold
	 *            The guess threshold to guess at
	 */
	public GuessedData(String first, String last, double threshold) {
		this.firstName = first;
		this.lastName = last;
		this.threshold = threshold;
	}

	public GuessedData(String first, String last) {
		this(first, last, 0.1d);
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public double getThreshold() {
		return threshold;
	}

	/**
	 * Generates a guess for the data provided
	 * 
	 * @param key
	 * @param cleaned
	 */
	public void generateGuess(String key, PersonalData... cleaned) {
		Map<String, Integer> values = new HashMap<>();
		int total = 0;
		for (PersonalData dat : cleaned) {
			String[] dataValues = dat.getAllValues(key);
			total += Math.max(dataValues.length, 1);
			for (String value : dat.getAllValues(key)) {
				if (values.containsKey(value))
					values.put(value, values.get(value) + 1);
				else
					values.put(value, 1);
			}
		}
		List<GuessedValue> confidentValues = new ArrayList<>();
		for (Entry<String, Integer> guess : values.entrySet()) {
			double confidence = guess.getValue() * 1d / total;
			if (confidence >= threshold) {
				confidentValues.add(new GuessedValue(guess.getKey(), confidence));
			}
		}
		put(key, new GuessedField(confidentValues));
	}

}
