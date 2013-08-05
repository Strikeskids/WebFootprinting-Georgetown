package com.sk.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sk.Driver;
import com.sk.util.LazyField;
import com.sk.util.PersonalData;

public abstract class OuterLoader extends PagingLoader {

	@Override
	public List<PersonalData> getOwnResults() {
		return ownResults.get();
	}

	private LazyField<List<PersonalData>> ownResults = new LazyField<>(new Callable<List<PersonalData>>() {
		@Override
		public List<PersonalData> call() throws Exception {
			return loadOwnResults();
		}
	});

	private List<PersonalData> loadOwnResults() {
		List<PersonalData> ret = new ArrayList<>();
		try {
			List<Future<List<PersonalData>>> futures = Driver.EXECUTOR.invokeAll(getExtractors());
			for (Future<List<PersonalData>> taskResult : futures) {
				ret.addAll(taskResult.get());
			}
		} catch (InterruptedException | ExecutionException ignored) {
		}
		return ret;
	}

	protected LazyField<Boolean> stopPaging = new LazyField<>(new Callable<Boolean>() {
		@Override
		public Boolean call() throws Exception {
			return hasBadNames();
		}
	});

	protected abstract boolean hasBadNames();

	protected abstract List<Extractor> getExtractors();

}
