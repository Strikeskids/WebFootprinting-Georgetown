package com.sk.clean.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.clean.DataCleaner;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;
import com.sk.web.Base64Util;
import com.sk.web.Request;

public class GoogleSearchCleaner implements DataCleaner {

	private final String[] fieldsToUse;
	private static final String LINK_NAVIGATOR = "div.rc h3.r a";

	public GoogleSearchCleaner(String... fieldNames) {
		this.fieldsToUse = fieldNames;
	}

	@Override
	public boolean clean(PersonalData dirty, PersonalData clean) {
		FieldBuilder builder = new FieldBuilder();
		for (String field : fieldsToUse) {
			if (dirty.containsKey(field)) {
				cleanInto(builder, field, dirty.getAllValues(field));
				dirty.remove(field);
			}
		}
		builder.addTo(clean);
		return true;
	}

	private void cleanInto(FieldBuilder clean, String fieldName, String[] dirtyValues) {
		for (String value : dirtyValues) {
			String hash = getCombinedUrlHashes(value);
			clean.put(fieldName, hash);
		}
	}

	private String getCombinedUrlHashes(String value) {
		List<String> results = getTopResults(value);
		ByteBuffer buf = ByteBuffer.allocate(results.size() * 4);
		for (String u : results) {
			buf.putInt(u.hashCode());
		}
		return Base64Util.encode(buf.array());
	}

	private List<String> getTopResults(String value) {
		List<String> ret = new ArrayList<>();
		Document doc = getDocument(value);
		for (Element link : doc.select(LINK_NAVIGATOR)) {
			ret.add(link.absUrl("href"));
		}
		return ret;
	}

	private Document getDocument(String query) {
		Request request = getRequest(query);
		try {
			return Jsoup.parse(request.openConnection().getInputStream(), "UTF-8", request.getFinalizedURL()
					.toExternalForm());
		} catch (IOException e) {
			e.printStackTrace();
			return new Document(request.getFinalizedURL().toExternalForm());
		}
	}

	private Request getRequest(String query) {
		Request baseRequest;
		try {
			baseRequest = new Request("https://www.google.com/search");
		} catch (MalformedURLException e) {
			return null;
		}
		baseRequest.addQuery("q", query);
		baseRequest.addRandomUserAgent();
		return baseRequest;
	}

}
