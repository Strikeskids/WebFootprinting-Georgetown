package com.sk.guess;

/**
 * A wrapper for a guessed value with a given confidence
 * 
 * @author Strikeskids
 * 
 */
public class GuessedValue {

	private final String value;
	private final double confidence;

	public GuessedValue(String value, double confidence) {
		if (confidence < 0 || confidence > 1)
			throw new IllegalArgumentException("Confidence must be in range 0.0-1.0");
		this.value = value;
		this.confidence = confidence;
	}

	/**
	 * Gets the confidence that this is the correct answer
	 * 
	 * @return The confidence from 0-1
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * Gets this value
	 * 
	 * @return The value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("%.2f%%|%s", getConfidence() * 100, getValue());
	}

}
