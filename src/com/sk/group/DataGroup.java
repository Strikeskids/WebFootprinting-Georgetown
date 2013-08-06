package com.sk.group;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sk.group.image.DCTHash;
import com.sk.util.data.PersonalData;

public class DataGroup extends HashSet<PersonalData> {

	private static final long serialVersionUID = 1L;
	private static final int IMAGE_THRESHOLD = 16;
	private final Set<String> emails = new HashSet<>(), twitters = new HashSet<>(), phones = new HashSet<>();
	private final Set<Long> images = new HashSet<>();

	public DataGroup(PersonalData... initializer) {
		Collections.addAll(this, initializer);
	}

	public boolean matches(PersonalData data) {
		for (String email : data.getAllValues("email")) {
			if (emails.contains(email))
				return true;
		}
		for (String twitter : data.getAllValues("twitter")) {
			if (twitters.contains(twitter))
				return true;
		}
		for (String phone : data.getAllValues("phone")) {
			if (phones.contains(phone))
				return true;
		}
		if (data.containsKey("profilePicturePrint")) {
			for (final String print : data.getAllValues("profilePicturePrint")) {
				if (matchPrint(print))
					return true;
			}
		}
		return false;
	}

	private boolean matchPrint(String print) {
		long printHash = DCTHash.convertHash(print);
		for (long image : images) {
			if (DCTHash.compare(image, printHash, IMAGE_THRESHOLD))
				return true;
		}
		return false;
	}

	@Override
	public boolean add(PersonalData data) {
		if (super.add(data)) {
			Collections.addAll(emails, data.getAllValues("email"));
			Collections.addAll(twitters, data.getAllValues("twitter"));
			Collections.addAll(phones, data.getAllValues("phone"));
			for (String imageHash : data.getAllValues("profilePicturePrint")) {
				images.add(DCTHash.convertHash(imageHash));
			}
			emails.remove("");
			twitters.remove("");
			phones.remove("");
			return true;
		} else {
			return false;
		}
	}

}
