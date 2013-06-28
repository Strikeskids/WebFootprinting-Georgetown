package com.sk.util.parse;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

/**
 * Implements the load methods of the basic parser class. Classes should extend this and use the {@link #doc}
 * field to grab data
 * 
 * @author Strikeskids
 * 
 */
public abstract class AbstractParser implements Parser {

	protected Document doc;

	public AbstractParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void load(URL url) throws IOException {
		doc = HttpConnection.connect(url).get();
	}

	@Override
	public void load(String source, String baseURI) {
		doc = Jsoup.parse(source, baseURI);
	}

}
