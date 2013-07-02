package com.sk.impl.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.util.parse.search.AbstractSearcher;
import com.sk.util.parse.search.AddressSearcher;

public class WhitepagesAddressSearcher extends AbstractSearcher implements AddressSearcher {

	private static final String BASE = "http://www.whitepages.com/search/FindNearby";

	@Override
	public boolean parse() throws IllegalStateException {
		Document doc = this.doc.get();
		if (doc == null)
			throw new IllegalStateException();
		List<URL> url = new ArrayList<>();
		for (Element list : doc.select("div#listings_frame div#listings div.result.household")) {
			for (Element link : list.select("ol li.basic_info > a")) {
				try {
					url.add(new URL(link.attr("abs:href")));
				} catch (MalformedURLException e1) {
				}
			}
			break;
		}
		urls.set(url.toArray(new URL[url.size()]));
		return true;
	}

	@Override
	public void lookFor(String address, String cityStateZip) throws IOException {
		load(new URL(BASE + "?street=" + URLEncoder.encode(address, "UTF-8") + "&where="
				+ URLEncoder.encode(cityStateZip, "UTF-8")));
	}
}
