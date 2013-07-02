package com.sk.impl.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.util.parse.search.AbstractSearcher;
import com.sk.util.parse.search.NameSearcher;

public class WhitepagesNameSearcher extends AbstractSearcher implements NameSearcher {

	private static final String BASE = "http://www.whitepages.com/name/";

	@Override
	public boolean parse() throws IllegalStateException {
		Document doc = this.doc.get();
		if (doc == null)
			throw new IllegalStateException();
		List<URL> urls = new ArrayList<>();
		for (Element e : doc.select("div.result > ol > li.basic_info > a.name")) {
			try {
				urls.add(new URL(e.attr("abs:href")));
			} catch (MalformedURLException e1) {
			}
		}
		this.urls.set(urls.toArray(new URL[urls.size()]));
		return true;
	}

	@Override
	public void lookFor(String first, String last) throws IOException {
		StringBuilder build = new StringBuilder();
		if (first != null) {
			build.append(first.replaceAll(" ", ""));
			build.append("-");
		}
		build.append(last.replaceAll(" ", ""));
		load(new URL(BASE + build));
	}

}
