package com.sk.util.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sk.util.StringProcessor;

public class RegexGrabber extends ProcessGrabber {

	public RegexGrabber(String selector, Pattern regex, String property) {
		super(selector, new RegexProcessor(regex), property);
	}

	public RegexGrabber(String selector, String attribute, Pattern regex, String property) {
		super(selector, attribute, new RegexProcessor(regex), property);
	}

	private static class RegexProcessor implements StringProcessor {
		private final Pattern pat;

		public RegexProcessor(Pattern pat) {
			this.pat = pat;
		}

		@Override
		public String process(String input) {
			if (input == null)
				return null;
			Matcher m = pat.matcher(input);
			if (!m.find())
				return null;
			return m.group("r");
		}
	}

}
