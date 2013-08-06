package com.sk.clean.impl;

import com.sk.clean.DataCleaner;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;

public class GenericCleaner implements DataCleaner {

	private final String[] fields;

	public GenericCleaner(String... fields) {
		this.fields = fields;
	}

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		FieldBuilder builder = new FieldBuilder();
		for (String field : fields) {
			if (in.containsKey(field)) {
				String[] values = in.getAllValues(field);
				in.remove(field);
				for (String value : values) {
					builder.put(field, cleanValue(value));
				}
			}
		}
		builder.addTo(out);
		return !builder.isEmpty();
	}

	public static String cleanValue(String input) {
		return input.toLowerCase().replaceAll(companyParts, "").replaceAll("[^a-z0-9]", "");
	}

	private static String companyParts = "inc|ltd|co";

}
