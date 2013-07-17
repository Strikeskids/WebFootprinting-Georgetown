package com.sk.guess;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A wrapper for all of the guesses for a given field
 * 
 * @author Strikeskids
 * 
 */
public class GuessedField {

	private final double totalConfidence;
	private final GuessedValue[] values;

	public GuessedField(GuessedValue... values) {
		if (values.length == 0) {
			this.totalConfidence = 0;
			this.values = values;
			return;
		}
		double totalConfidence = 0;
		for (GuessedValue value : values) {
			totalConfidence += value.getConfidence();
		}

		if (totalConfidence > 1 + 1e-6)
			throw new IllegalArgumentException("Confidence cannot be that high");
		this.totalConfidence = totalConfidence;
		this.values = values;
		Arrays.sort(this.values, new Comparator<GuessedValue>() {
			@Override
			public int compare(GuessedValue o1, GuessedValue o2) {
				return Double.compare(o2.getConfidence(), o1.getConfidence());
			}
		});
	}

	public GuessedField(List<GuessedValue> confidentValues) {
		this(confidentValues.toArray(new GuessedValue[confidentValues.size()]));
	}

	/**
	 * Gets the best guess of the value of this field
	 * 
	 * @return The best guess of the value of this field
	 */
	public String getBestGuess() {
		return (values.length > 0 ? values[0].getValue() : null);
	}

	/**
	 * Gets all of the guesses for this field
	 * 
	 * @return An array of {@link GuessedValue}s for this field
	 */
	public GuessedValue[] getGuesses() {
		return values;
	}

	/**
	 * Gets the confidence that at least one of the guesses is correct
	 * 
	 * @return The total confidence
	 */
	public double getTotalConfidence() {
		return totalConfidence;
	}

}
