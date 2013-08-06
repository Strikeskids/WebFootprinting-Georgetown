package com.sk.util.data;

import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk.stat.PersonStatistics;
import com.sk.util.LazyField;

public class DataGson {

	private static final LazyField<Gson> singleGson = new LazyField<>(new Callable<Gson>() {
		@Override
		public Gson call() throws Exception {
			return new GsonBuilder().registerTypeAdapter(PersonalData.class, PersonalData.getAdapter())
					.registerTypeAdapter(PersonStatistics.class, PersonStatistics.getAdapter()).create();
		}
	});

	public static Gson getGson() {
		return singleGson.get();
	}
}
