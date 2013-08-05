package com.sk.impl2;

import static com.sk.impl2.WhitepagesLoader.SITE_KEY;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

import com.sk.parse.IndividualExtractor;
import com.sk.parse.Parsers;
import com.sk.util.DocNavigator;
import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;
import com.sk.util.StringProcessor;
import com.sk.util.UniversalDocNavigator;
import com.sk.web.Request;

public class WhitepagesPersonLoader extends IndividualExtractor {

	private final Request request;

	private Document document;

	WhitepagesPersonLoader(String url) throws MalformedURLException {
		request = new Request(url, "GET");
		request.addRandomUserAgent();
	}

	@Override
	protected PersonalData getResult() {
		PersonalData ret = new PersonalData(SITE_KEY);
		FieldBuilder builder = new FieldBuilder();
		for (DocNavigator navigator : navigators) {
			navigator.navigate(document, builder);
		}
		builder.addTo(ret);
		return ret;
	}

	@Override
	protected Request getRequest() {
		return request;
	}

	@Override
	protected void parse(URL source, String data) {
		document = Parsers.parseHTML(data, source.toExternalForm());
	}

	private static final Pattern AGE_PATTERN = Pattern.compile("([0-9]+\\s*-\\s*[0-9]+) years");
	private static final Pattern NAME_PATTERN = Pattern.compile("(.*?)\\s*(?:|[0-9] years old)\\s*$");

	private static final DocNavigator[] navigators = {
			new UniversalDocNavigator("address", "address.address.adr"),
			new UniversalDocNavigator("phone", "li.tel", "span", "a"),
			new UniversalDocNavigator("name", new StringProcessor() {
				@Override
				public String process(String input) {
					Matcher matcher = NAME_PATTERN.matcher(input);
					if (matcher.find())
						return matcher.group(1);
					else
						return null;
				}
			}, "h1.name"), new UniversalDocNavigator("age", new StringProcessor() {
				@Override
				public String process(String input) {
					Matcher matcher = AGE_PATTERN.matcher(input);
					if (matcher.find())
						return matcher.group(1);
					else
						return null;
				}
			}, "span.age") };

}
