package com.sk.impl2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sk.parse.Extractor;
import com.sk.parse.OuterLoader;
import com.sk.parse.PagingLoader;
import com.sk.parse.Parsers;
import com.sk.util.LazyField;
import com.sk.web.IOUtil;
import com.sk.web.Request;

public class GoogleSearchLoader extends OuterLoader {

	private static final String SEARCH_URL = "https://www.google.com/search?q=";

	private Document document;
	private final SearchAcceptor acceptor;
	private final String url;

	private LazyField<List<SearchResult>> searchResults = new LazyField<>(new Callable<List<SearchResult>>() {
		@Override
		public List<SearchResult> call() throws Exception {
			return loadSearchResults();
		}
	});

	public GoogleSearchLoader(String query, SearchAcceptor acceptor) {
		this.url = SEARCH_URL + IOUtil.urlEncode(query);
		this.acceptor = acceptor;
	}

	private GoogleSearchLoader(URL url, SearchAcceptor acceptor) {
		this.url = url.toExternalForm();
		this.acceptor = acceptor;
	}

	@Override
	protected boolean loadStopSearching() {
		return searchResults.get().size() - getExtractors().size() > 3;
	}

	@Override
	protected List<Extractor> getExtractors() {
		List<Extractor> ret = new ArrayList<>();
		for (SearchResult result : searchResults.get()) {
			Extractor extract = acceptor.getExtractor(result);
			if (extract != null)
				ret.add(extract);
		}
		return ret;
	}

	private List<SearchResult> loadSearchResults() {
		List<SearchResult> results = new ArrayList<>();
		for (Element link : document.select("div.rc h3.r a")) {
			SearchResult result = getResult(link);
			if (result != null)
				results.add(result);
		}
		return results;
	}

	private SearchResult getResult(Element link) {
		return new SearchResult(link.text(), link.absUrl("href"));
	}

	@Override
	protected PagingLoader createNextPage() {
		if (stopPaging.get())
			return null;
		for (Element nextButton : document.select("#pnnext")) {
			try {
				URL nextUrl = new URL(nextButton.absUrl("href"));
				return new GoogleSearchLoader(nextUrl, acceptor);
			} catch (MalformedURLException ignored) {
			}
		}
		return null;
	}

	@Override
	protected Request getRequest() {
		try {
			Request request = new Request(url, "GET");
			request.addRandomUserAgent();
			return request;
		} catch (MalformedURLException ignored) {
			return null;
		}
	}

	@Override
	protected void parse(URL source, String data) {
		document = Parsers.parseHTML(data, source.toExternalForm());
	}

	static class SearchResult {

		public final String title, url;

		public SearchResult(String title, String url) {
			this.title = title;
			this.url = url;
		}
	}

	static interface SearchAcceptor {
		public Extractor getExtractor(SearchResult result);
	}

}
