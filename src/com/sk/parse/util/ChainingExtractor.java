package com.sk.parse.util;

import java.util.List;

import com.sk.util.data.PersonalData;

public interface ChainingExtractor extends Extractor {
	public List<PersonalData> getOwnResults();
}
