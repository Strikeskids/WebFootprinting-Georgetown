package com.sk.stat;

import java.util.Arrays;

public class AttributeStatistics {

	private final String[] best, distinguished;
	private final double confidence, coverage;
	private final int total;

	public AttributeStatistics(String[] best, double confidence, double coverage, int total, String[] distinguished) {
		this.best = best;
		this.confidence = confidence;
		this.coverage = coverage;
		this.total = total;
		this.distinguished = distinguished;
	}

	public String[] getBestValues() {
		return best;
	}

	public String[] getDistinguishedValues() {
		return distinguished;
	}

	public double getConfidence() {
		return confidence;
	}

	public int getTotalValues() {
		return total;
	}

	public double getCoverage() {
		return coverage;
	}

	@Override
	public String toString() {
		if (getTotalValues() == getBestValues().length)
			return String.format("AttributeStat: %s %d %.2f", Arrays.toString(getBestValues()), getTotalValues(),
					getCoverage() * 100);
		else
			return String.format("AttributeStat: %s @ %.2f%% %.2f%% %d", Arrays.toString(getBestValues()),
					getConfidence() * 100, getCoverage() * 100, getTotalValues());
	}

}
