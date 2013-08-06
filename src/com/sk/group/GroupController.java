package com.sk.group;

import java.util.ArrayList;
import java.util.List;

import com.sk.group.image.DCTHash;
import com.sk.util.data.PersonalData;

public class GroupController {

	public List<DataGroup> group(boolean grabImages, PersonalData... input) {
		List<DataGroup> ret = new ArrayList<>();
		if (grabImages)
			DCTHash.fingerprint(input);
		outer: for (PersonalData data : input) {
			for (DataGroup group : ret) {
				if (group.matches(data)) {
					group.add(data);
					continue outer;
				}
			}
			ret.add(new DataGroup(data));
		}
		return ret;
	}

	public List<DataGroup> group(PersonalData... input) {
		return group(false, input);
	}

	private GroupController() {
	}

	private static GroupController singleton;
	private static final Object slock = new Object();

	public static GroupController get() {
		if (singleton == null) {
			synchronized (slock) {
				if (singleton == null) {
					singleton = new GroupController();
				}
			}
		}
		return singleton;
	}

}
