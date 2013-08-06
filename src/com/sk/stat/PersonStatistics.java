package com.sk.stat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sk.util.data.DataGson;

public class PersonStatistics extends LinkedHashMap<String, AttributeStatistics> {

	private static final long serialVersionUID = -7036835670634389519L;
	private final String firstName, lastName;

	public PersonStatistics(String first, String last) {
		this.firstName = first;
		this.lastName = last;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	@Override
	public String toString() {
		return String.format("Stat %s %s: %s", firstName, lastName, super.toString());
	}

	public static TypeAdapter<PersonStatistics> getAdapter() {
		return new PersonStatisticsAdapter().nullSafe();
	}

	private static class PersonStatisticsAdapter extends TypeAdapter<PersonStatistics> {
		@Override
		public PersonStatistics read(JsonReader in) throws IOException {
			in.beginObject();
			String first, last;
			if (!in.nextName().equals("firstName"))
				return null;
			first = in.nextString();
			if (!in.nextName().equals("lastName"))
				return null;
			last = in.nextString();
			if (!in.nextName().equals("attributes"))
				return null;
			PersonStatistics ret = new PersonStatistics(first, last);
			in.beginObject();
			while (in.hasNext()) {
				ret.put(in.nextName(),
						(AttributeStatistics) DataGson.getGson().fromJson(in, AttributeStatistics.class));
			}
			in.endObject();
			in.endObject();
			return ret;
		}

		@Override
		public void write(JsonWriter out, PersonStatistics value) throws IOException {
			out.beginObject();
			out.name("firstName").value(value.firstName).name("lastName").value(value.lastName);
			out.name("attributes").beginObject();
			for (Map.Entry<String, AttributeStatistics> entry : value.entrySet()) {
				out.name(entry.getKey());
				DataGson.getGson().toJson(DataGson.getGson().toJsonTree(entry.getValue(), AttributeStatistics.class),
						out);
			}
			out.endObject();
			out.endObject();
		}
	}

}
