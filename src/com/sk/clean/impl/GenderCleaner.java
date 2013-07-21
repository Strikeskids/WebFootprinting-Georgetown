package com.sk.clean.impl;

import com.sk.clean.DataCleaner;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;

public class GenderCleaner implements DataCleaner {

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		if (in.containsKey("gender")) {
			String[] genders = in.getAllValues("gender");
			in.remove("gender");
			FieldBuilder builder = new FieldBuilder();
			for (String gender : genders) {
				builder.put("gender", Gender.getFor(gender));
			}
			builder.addTo(out);
			return true;
		}
		return false;
	}

	private enum Gender {
		MALE("male", "m", "man", "boy"), FEMALE("female", "f", "girl", "woman"), OTHER("other");

		public final String[] values;

		private Gender(String... values) {
			this.values = values;
		}

		public static Gender getFor(String value) {
			for (Gender gender : values()) {
				for (String repr : gender.values) {
					if (repr.equalsIgnoreCase(value))
						return gender;
				}
			}
			return null;
		}
	}

}
