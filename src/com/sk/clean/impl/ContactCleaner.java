package com.sk.clean.impl;

import com.sk.clean.DataCleaner;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;

public class ContactCleaner implements DataCleaner {

	@Override
	public boolean clean(PersonalData in, PersonalData out) {
		FieldBuilder ret = new FieldBuilder();
		for (String phone : in.getAllValues("phone")) {
			ret.put("phone", phone.replaceAll("\\D", ""));
		}
		in.remove("phone");
		ret.put("email", in.remove("email"));
		ret.put("twitter", in.remove("twitter"));
		ret.addTo(out);
		return true;
	}

}
