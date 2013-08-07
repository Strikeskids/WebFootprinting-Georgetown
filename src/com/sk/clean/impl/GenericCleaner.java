package com.sk.clean.impl;

import java.util.ArrayList;
import java.util.List;

import com.sk.clean.DataCleaner;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;

public class GenericCleaner implements DataCleaner {

	private static final String NON_ALPHANUMBERIC_REGEX = "[^a-z0-9]";
	private static final String COMPANY_PARTS = "inc|ltd|co";

	private final String[] fieldsToUse;

	public GenericCleaner(String... fields) {
		this.fieldsToUse = fields;
	}

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		FieldBuilder builder = new FieldBuilder();
		for (String field : fieldsToUse) {
			if (in.containsKey(field)) {
				List<String> cleanedValues = getCleanedValues(in, field);
				builder.putAll(field, cleanedValues);
			}
		}
		builder.addTo(out);
		return !builder.isEmpty();
	}
	
	private List<String> getCleanedValues(PersonalData in, String field) {
		List<String> ret = new ArrayList<>();
		for (String value : in.getAllValues(field)) {
			String cleaned = cleanValue(value);
			ret.add(cleaned);
		}
		in.remove(field);
		return ret;
	}

	public static String cleanValue(String input) {
		return input.toLowerCase().replaceAll(COMPANY_PARTS, "").replaceAll(NON_ALPHANUMBERIC_REGEX, "");
	}

}
