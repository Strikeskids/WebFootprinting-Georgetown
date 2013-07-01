package com.sk.impl.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.util.parse.search.AbstractSearcher;
import com.sk.util.parse.search.AddressSearcher;

public class ZillowSearcher extends AbstractSearcher implements AddressSearcher {

	private static final String BASE = "http://www.zillow.com/homes/";

	@Override
	public boolean parse() throws IllegalStateException {
		Document doc = this.doc.get();
		if (doc == null)
			throw new IllegalStateException();
		URL found = null;
		for (Element e : doc.select(".selected-listing a.hdp-link.routable")) {
			try {
				found = new URL(e.attr("abs:href"));
				break;
			} catch (MalformedURLException e1) {
			}
		}
		if (found == null) {
			return false;
		} else {
			urls.set(new URL[] { found });
			return true;
		}
	}

	@Override
	public void lookFor(String address) throws IOException {
		load(new URL(BASE + address.replaceAll(" ", "-") + "_rb/"));
	}

}
