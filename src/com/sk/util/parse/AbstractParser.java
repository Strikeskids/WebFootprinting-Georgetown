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
	}

	/**
	 * Method called when a new document is loaded. Resets all local variables for proper state change
	 */
	protected void reset() {

	}

	@Override
	public void load(URL url) throws IOException {
		reset();
		doc.set(HttpConnection
				.connect(url)
				.timeout(8000)
				.header("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0").get());
	}

	@Override
	public void load(String source, String baseURI) {
		reset();
		doc.set(Jsoup.parse(source, baseURI));
	}

}
