package com.sk.api;

import java.io.IOException;
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

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		return parseResponse(util.send(getNameRequest(first, last)));
	}

	/**
	 * Creates an OAuthRequest for the given API to search for the given first and last name
	 * 
	 * @param first
	 *            The first name to look for
	 * @param last
	 *            The last name to look for
	 * @return The OAuthRequest created
	 */
	public abstract OAuthRequest getNameRequest(String first, String last);

	/**
	 * Attempts to parse the given Response received by sending an OAuthRequest
	 * 
	 * @param resp
	 *            The response to parse
	 * @return <code>true</code> if the response was successfully parsed; <code>false</code> otherwise
	 */
	public abstract boolean parseResponse(Response resp);

}
