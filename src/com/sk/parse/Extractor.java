package com.sk.parse;

import java.util.List;
import java.util.concurrent.Callable;

import com.sk.util.PersonalData;

public interface Extractor extends Callable<List<PersonalData>> {
	public List<PersonalData> getResults();
}
