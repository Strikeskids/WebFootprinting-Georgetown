package com.sk.parse.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.sk.util.LazyField;
import com.sk.util.data.PersonalData;

public abstract class IndividualExtractor extends AbstractLoader implements Extractor {

	private LazyField<List<PersonalData>> result = new LazyField<>(new Callable<List<PersonalData>>() {
		@Override
		public List<PersonalData> call() throws Exception {
			PersonalData ret = getResult();
			if (ret == null)
				return Arrays.asList();
			else
				return Arrays.asList(ret);
		}
	});

	@Override
	public final List<PersonalData> call() throws Exception {
		return getResults();
	}

	@Override
	public final List<PersonalData> getResults() {
		return result.get();
	}

	protected abstract PersonalData getResult();

}
