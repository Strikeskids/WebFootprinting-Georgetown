package com.sk.impl.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.util.parse.search.AbstractParseSearcher;
import com.sk.util.parse.search.AddressSearcher;
import com.sk.util.parse.search.NameSearcher;

public class WhitepagesSearcher extends AbstractParseSearcher implements AddressSearcher, NameSearcher {

	private static final String ADDRESS_BASE = "http://www.whitepages.com/search/FindNearby",
			NAME_BASE = "http://www.whitepages.com/name/";

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
		for (Element e : doc.select("div.result > ol > li.basic_info > a.name")) {
			try {
				url.add(new URL(e.attr("abs:href")));
			} catch (MalformedURLException e1) {
			}
		}
		urls.set(url.toArray(new URL[url.size()]));
		return true;
	}

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		StringBuilder build = new StringBuilder();
		if (first != null) {
			build.append(first.replaceAll(" ", ""));
			build.append("-");
		}
		build.append(last.replaceAll(" ", ""));
		load(new URL(NAME_BASE + build));
		return parse();
	}

	@Override
	public boolean lookForAddress(String address, String cityStateZip) throws IOException {
		load(new URL(ADDRESS_BASE + "?street=" + URLEncoder.encode(address, "UTF-8") + "&where="
				+ URLEncoder.encode(cityStateZip, "UTF-8")));
		return parse();
	}

}
