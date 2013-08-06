package com.sk.parse.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import com.sk.util.LazyField;
import com.sk.web.IOUtil;
import com.sk.web.Request;

public abstract class AbstractLoader {

	protected LazyField<String> rawData = new LazyField<>(new Callable<String>() {
		@Override
		public String call() {
			return loadAndParse();
		}
	});

	protected void init() {
		rawData.get();
	}

	private String loadAndParse() {
		Request request = getRequest();
		String data = loadData(request);
		parse(request.getFinalizedURL(), data);
		return data;
	}

	protected String loadData(Request request) {
		try {
			URLConnection conn = request.openConnection();
			conn.setDoInput(true);
			conn.connect();
			return IOUtil.readFrom(conn.getInputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
			return "";
		}
	}

	protected abstract Request getRequest();

	protected abstract void parse(URL source, String data);

}
