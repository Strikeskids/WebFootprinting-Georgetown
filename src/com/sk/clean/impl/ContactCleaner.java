package com.sk.clean.impl;

import com.sk.clean.DataCleaner;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;

public class ContactCleaner implements DataCleaner {

	private static final String NON_DIGIT_REGEX = "\\D";
	private static final String PHONE_FIELD = "phone";
	private static final String TWITTER_FIELD = "twitter";
	private static final String EMAIL_FIELD = "email";

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		FieldBuilder ret = new FieldBuilder();
		cleanPhone(in, ret);
		ret.put(EMAIL_FIELD, in.remove(EMAIL_FIELD));
		ret.put(TWITTER_FIELD, in.remove(TWITTER_FIELD));
		ret.addTo(out);
		return true;
	}

	private FieldBuilder cleanPhone(PersonalData in, FieldBuilder out) {
		for (String phone : in.getAllValues(PHONE_FIELD)) {
			out.put(PHONE_FIELD, phone.replaceAll(NON_DIGIT_REGEX, ""));
		}
		in.remove(PHONE_FIELD);
		return out;
	}
	
}
