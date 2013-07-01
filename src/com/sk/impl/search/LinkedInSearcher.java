package com.sk.impl.search;

import java.util.regex.Pattern;

import com.sk.util.parse.search.GoogleSearcher;

public class LinkedInSearcher extends GoogleSearcher {

	public LinkedInSearcher() {
		super("linkedin.com", Pattern.compile("https?://www.linkedin.com/(?:pub|in)/(?!dir)"));
	}

}
