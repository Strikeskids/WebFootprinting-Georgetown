package com.sk.parse.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.sk.util.LazyField;
import com.sk.util.data.PersonalData;

public abstract class PagingLoader extends AbstractLoader implements ChainingExtractor {

	private LazyField<List<PersonalData>> results = new LazyField<>(new Callable<List<PersonalData>>() {
		@Override
		public List<PersonalData> call() throws Exception {
			return initializeResults();
		}
	});
	private LazyField<PagingLoader> nextPage = new LazyField<>(new Callable<PagingLoader>() {
		@Override
		public PagingLoader call() throws Exception {
			return createNextPage();
		}
	});
	protected LazyField<Boolean> stopPaging = new LazyField<>(new Callable<Boolean>() {
		@Override
		public Boolean call() throws Exception {
			return loadStopPaging();
		}
	});

	private LazyField<List<PersonalData>> ownResults = new LazyField<>(new Callable<List<PersonalData>>() {
		@Override
		public List<PersonalData> call() throws Exception {
			return loadOwnResults();
		}
	});

	protected abstract boolean loadStopPaging();

	protected abstract List<PersonalData> loadOwnResults();

	@Override
	public List<PersonalData> getOwnResults() {
		return ownResults.get();
	}

	@Override
	public List<PersonalData> getResults() {
		return results.get();
	}

	private List<PersonalData> initializeResults() {
		List<PersonalData> ownResults = getOwnResults();
		if (ownResults == null)
			ownResults = Arrays.asList();
		List<PersonalData> results = new ArrayList<>(getOwnResults());
		PagingLoader nextPage = getNextPage();
		if (nextPage != null)
			results.addAll(nextPage.getResults());
		return results;
	}

	@Override
	public List<PersonalData> call() throws Exception {
		return getResults();
	}

	public final PagingLoader getNextPage() {
		return nextPage.get();
	}

	protected abstract PagingLoader createNextPage();

}
