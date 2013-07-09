package com.sk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class PersonalDataStorage extends HashMap<String, List<PersonalData>> {

	private static final long serialVersionUID = 9177444068545121072L;

	public void add(PersonalData... input) {
		for (PersonalData data : input) {
			String site = data.getWebsiteId();
			if (containsKey(site)) {
				get(site).add(data);
			} else {
				List<PersonalData> toAdd = new ArrayList<>();
				toAdd.add(data);
				put(site, toAdd);
			}
		}
	}

}
