package com.sk.impl.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.util.parse.search.NameSearcher;
import com.sk.util.parse.search.ScrapeSearcher;

public class GooglePlusSearcher extends ScrapeSearcher implements NameSearcher {

	@Override
	public boolean parse() throws IllegalStateException {
		Document doc = this.doc.get();
		if (doc == null)
			throw new IllegalStateException();
		List<URL> ret = new ArrayList<>();
		for (Element link : doc.select(".gecJce.Xk6Ee a.fmPSLc")) {
			try {
				ret.add(new URL(link.attr("abs:href")));
			} catch (MalformedURLException e) {
			}
		}
		this.urls.set(ret.toArray(new URL[ret.size()]));
		return true;
	}

	private static final String URL = "https://plus.google.com/s/%s%%20%s/people";

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		load(new URL(String.format(URL, URLEncoder.encode(first, "UTF-8"), URLEncoder.encode(last, "UTF-8"))));
		return parse();
	}

}
