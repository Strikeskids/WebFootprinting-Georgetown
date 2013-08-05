package com.sk.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.sk.util.LazyField;
import com.sk.util.PersonalData;

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

	@Override
	public List<PersonalData> getResults() {
		return results.get();
	}

	private List<PersonalData> initializeResults() {
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
