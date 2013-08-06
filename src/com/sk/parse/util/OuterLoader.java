package com.sk.parse.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sk.threading.UniversalExecutor;
import com.sk.util.PersonalData;

public abstract class OuterLoader extends PagingLoader {

	@Override
	protected List<PersonalData> loadOwnResults() {
		List<PersonalData> ret = new ArrayList<>();
		try {
			List<Future<List<PersonalData>>> futures = UniversalExecutor.search.invokeAll(getExtractors());
			for (Future<List<PersonalData>> taskResult : futures) {
				ret.addAll(taskResult.get());
			}
		} catch (InterruptedException | ExecutionException ignored) {
		}
		return ret;
	}

	protected abstract List<Extractor> getExtractors();

}
