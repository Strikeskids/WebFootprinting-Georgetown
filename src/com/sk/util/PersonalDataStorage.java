package com.sk.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores personal data by site name. Used for json encoding <br/>
 * <br/>
 * <code>
 * Gson gson = new Gson();<br/>
 * String json = gson.toJson(PersonalData);
 * </code>
 * 
 * @author Strikeskids
 * 
 */
public class PersonalDataStorage extends HashMap<String, Set<PersonalData>> {

	private static final long serialVersionUID = 9177444068545121072L;

	public void add(PersonalData... input) {
		for (PersonalData data : input) {
			String site = data.getWebsiteId();
			if (containsKey(site)) {
				get(site).add(data);
			} else {
				Set<PersonalData> toAdd = new LinkedHashSet<>();
				toAdd.add(data);
				put(site, toAdd);
			}
		}
	}

	public void addStorage(PersonalDataStorage pds) {
		for (Set<PersonalData> lds : pds.values()) {
			for (PersonalData d : lds) {
				add(d);
			}
		}
	}

	public PersonalData[] toArray() {
		List<PersonalData> ret = new ArrayList<PersonalData>();
		for (Set<PersonalData> cur : values())
			ret.addAll(cur);
		return ret.toArray(new PersonalData[ret.size()]);
	}

	@Override
	public int size() {
		int ret = 0;
		for (Collection<?> coll : values())
			ret += coll.size();
		return ret;
	}

}
