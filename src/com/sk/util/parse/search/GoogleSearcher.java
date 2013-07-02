package com.sk.util.parse.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class GoogleSearcher extends AbstractSearcher implements NameSearcher {

	private static final String BASE = "https://www.google.com/search?q=";

	private final String url;
	private final Pattern accept;

	public GoogleSearcher(String site, Pattern accept) {
		String encoded = "";
		try {
			encoded = URLEncoder.encode("site:" + site + " ", "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		this.url = BASE + encoded;
		this.accept = accept;
	}

	@Override
	public boolean parse() throws IllegalStateException {
		Document doc = this.doc.get();
		if (doc == null)
			throw new IllegalStateException();
		Set<String> found = new LinkedHashSet<>();
		for (Element e : doc.select("li.g div.rc div.f.kv cite")) {
			String cur = e.text();
			Matcher m = accept.matcher(cur);
			if (m.find())
				found.add(cur);
		}
		URL[] ret = new URL[found.size()];
		int loc = 0;
		for (String s : found) {
			try {
				ret[loc++] = new URL(s);
			} catch (MalformedURLException e1) {
			}
		}
		urls.set(ret);
		return true;
	}

	public void lookFor(String text) throws IOException {
		load(new URL(this.url + URLEncoder.encode(text, "UTF-8")));
	}

	@Override
	public void lookFor(String first, String last) throws IOException {
		lookFor(first + " " + last);
	}

}
