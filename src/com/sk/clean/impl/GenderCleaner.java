package com.sk.clean.impl;

import com.sk.clean.DataCleaner;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;

public class GenderCleaner implements DataCleaner {

	private static final String FIELD_NAME = "gender";

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		FieldBuilder builder = new FieldBuilder();
		for (String gender : in.getAllValues(FIELD_NAME)) {
			builder.put(FIELD_NAME, Gender.getFor(gender));
		}
		in.remove(FIELD_NAME);

		builder.addTo(out);
		return false;
	}

	private enum Gender {
		MALE("male", "m", "man", "boy"), FEMALE("female", "f", "girl", "woman"), OTHER("other");

		public final String[] values;

		private Gender(String... values) {
			this.values = values;
		}

		public boolean matches(String value) {
			for (String repr : this.values) {
				if (repr.equalsIgnoreCase(value))
					return true;
			}
			return false;
		}

		public static Gender getFor(String value) {
			for (Gender gender : values()) {
				if (gender.matches(value))
					return gender;
			}
			return null;
		}
	}

}
