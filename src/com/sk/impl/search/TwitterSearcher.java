package com.sk.impl.search;

import java.util.regex.Pattern;

import com.sk.util.parse.search.GoogleSearcher;

public class TwitterSearcher extends GoogleSearcher {

	public TwitterSearcher() {
		super("twitter.com", Pattern.compile("https?://www.twitter.com/[^/]+$"));
	}

}
