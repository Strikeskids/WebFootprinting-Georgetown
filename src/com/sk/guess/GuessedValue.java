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
		this.value = value;
		this.confidence = confidence;
	}

	public double getConfidence() {
		return confidence;
	}

	public String getValue() {
		return value;
	}

}
