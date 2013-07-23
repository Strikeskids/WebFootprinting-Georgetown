package com.sk.clean.impl;

import com.sk.clean.DataCleaner;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;

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
					builder.put(field, value.toLowerCase().replaceAll("[^a-z0-9]", ""));
				}
			}
		}
		builder.addTo(out);
		return !builder.isEmpty();
	}

}