package com.sk.util.parse.scrape;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sk.util.PersonalData;
import com.sk.util.StringProcessor;

public class ProcessGrabber extends BasicGrabber {

	private final StringProcessor processor;

	public ProcessGrabber(String selector, StringProcessor process, String property) {
		super(selector, property);
		this.processor = process;
	}

	public ProcessGrabber(String selector, String attribute, StringProcessor process, String property) {
		super(selector, attribute, property);
		this.processor = process;
	}

	@Override
	public boolean grab(Document source, PersonalData destination) {
		Elements found = source.select(selector);
		if (!found.isEmpty()) {
			for (Element ele : found) {
				String store;
				if (attribute == null)
					store = ele.text();
				else
					store = ele.attributes().get(attribute);
				String ans = processor.process(store);
				if (ans != null) {
					destination.put(property, ans);
					return true;
				}
			}
		}
		return false;
	}

}
