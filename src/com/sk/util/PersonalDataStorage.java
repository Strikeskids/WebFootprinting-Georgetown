package com.sk.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

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

	/**
	 * Adds the {@link PersonalData} to this {@link PersonalDataStorage}
	 * 
	 * @param input
	 *            The {@link PersonalData} to add
	 */
	public void add(PersonalData... input) {
		main: for (PersonalData data : input) {
			for (String attr : data.keySet()) {
				if (!attr.contains("name")) {
					String site = data.getWebsiteId();
					if (containsKey(site)) {
						get(site).add(data);
					} else {
						Set<PersonalData> toAdd = new LinkedHashSet<>();
						toAdd.add(data);
						put(site, toAdd);
					}
					continue main;
				}
			}
		}
	}

	/**
	 * Adds the contents of the {@link PersonalDataStorage} to this
	 * 
	 * @param pds
	 *            The {@link PersonalDataStorage} to add
	 */
	public void addStorage(PersonalDataStorage pds) {
		for (Set<PersonalData> lds : pds.values()) {
			for (PersonalData d : lds) {
				add(d);
			}
		}
	}

	/**
	 * Converts this {@link PersonalDataStorage} to an array
	 * 
	 * @return An array of {@link PersonalData}
	 */
	public PersonalData[] toArray() {
		List<PersonalData> ret = new ArrayList<PersonalData>();
		for (Set<PersonalData> cur : values())
			ret.addAll(cur);
		return ret.toArray(new PersonalData[ret.size()]);
	}

	/**
	 * Gets the number of elements in this {@link PersonalDataStorage}
	 * 
	 * @return the number of elements
	 */
	@Override
	public int size() {
		int ret = 0;
		for (Collection<?> coll : values())
			ret += coll.size();
		return ret;
	}

	private static Gson singleGson;
	private static final Object gsonLock = new Object();

	public static Gson getStorageGson() {
		if (singleGson == null) {
			synchronized (gsonLock) {
				singleGson = new GsonBuilder().registerTypeAdapter(PersonalData.class,
						new PersonalDataAdapter().nullSafe()).create();
			}
		}
		return singleGson;
	}

	private static class PersonalDataAdapter extends TypeAdapter<PersonalData> {

		private static final String siteToken = "XXSiteId";

		@Override
		public PersonalData read(JsonReader in) throws IOException {
			in.beginObject();
			Map<String, String> values = new HashMap<>();
			while (in.hasNext()) {
				values.put(in.nextName(), in.nextString());
			}
			in.endObject();

			if (!values.containsKey(siteToken))
				throw new IllegalArgumentException();
			return new PersonalData(values.remove(siteToken), values);
		}

		@Override
		public void write(JsonWriter out, PersonalData value) throws IOException {
			out.beginObject();
			out.name(siteToken).value(value.getWebsiteId());
			for (Entry<String, String> entry : value.entrySet()) {
				out.name(entry.getKey()).value(entry.getValue());
			}
			out.endObject();
		}

	}

}
