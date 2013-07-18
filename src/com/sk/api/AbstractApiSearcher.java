package com.sk.api;

import java.net.URL;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;

import com.sk.util.PersonalData;
import com.sk.util.parse.search.NameSearcher;

public abstract class AbstractApiSearcher implements NameSearcher {

	protected final ThreadLocal<PersonalData[]> data = new ThreadLocal<>();
	protected final ApiUtility util;

	public AbstractApiSearcher(ApiUtility util) {
		this.util = util;
	}

	@Override
	public PersonalData[] getData() throws IllegalStateException {
		PersonalData[] ret = data.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}

	@Override
	public URL[] results() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	protected Response getResponse(OAuthRequest request) {
		return util.send(request);
	}

}
