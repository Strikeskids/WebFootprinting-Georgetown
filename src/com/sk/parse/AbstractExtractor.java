package com.sk.parse;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

import com.sk.util.PersonalData;
import com.sk.web.IOUtil;
import com.sk.web.Request;

public abstract class AbstractExtractor implements Extractor, Callable<List<PersonalData>> {

	protected String rawData;
	private final ReentrantLock dataLoadLock = new ReentrantLock();

	protected void init() {
		if (rawData == null) {
			try {
				dataLoadLock.lock();
				loadAndParse();
			} finally {
				dataLoadLock.unlock();
			}
		}
	}

	private void loadAndParse() {
		Request request = getRequest();
		rawData = loadData(request);
		parse(request.getFinalizedURL(), rawData);
	}

	protected String loadData(Request request) {
		try {
			URLConnection conn = request.openConnection();
			conn.setDoInput(true);
			conn.connect();
			return IOUtil.readFrom(conn.getInputStream());
		} catch (IOException ex) {
			return "";
		}
	}

	protected abstract Request getRequest();

	protected abstract void parse(URL source, String data);

	@Override
	public List<PersonalData> call() throws Exception {
		return getResults();
	}

}
