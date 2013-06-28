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

	protected ThreadLocal<Document> doc = new ThreadLocal<>();

	public AbstractParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void load(URL url) throws IOException {
		doc.set(HttpConnection
				.connect(url)
				.header("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0").get());
	}

	@Override
	public void load(String source, String baseURI) {
		doc.set(Jsoup.parse(source, baseURI));
	}

}
