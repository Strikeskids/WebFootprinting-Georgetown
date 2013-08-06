package com.sk.parse.util;

import java.util.List;
import java.util.concurrent.Callable;

import com.sk.util.data.PersonalData;

public interface Extractor extends Callable<List<PersonalData>> {
	public List<PersonalData> getResults();
}
