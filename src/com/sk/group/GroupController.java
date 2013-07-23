package com.sk.group;

import java.util.ArrayList;
import java.util.List;

import com.sk.util.PersonalData;

public class GroupController {

	public List<DataGroup> group(PersonalData... input) {
		List<DataGroup> ret = new ArrayList<>();
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
