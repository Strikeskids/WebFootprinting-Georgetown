package com.sk.util.parse.scrape;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sk.util.FieldBuilder;
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
	public boolean grab(Document source, FieldBuilder destination) {
		Elements foundSet = source.select(selector);
		boolean ret = false;
		if (!foundSet.isEmpty()) {
			for (Element found : foundSet) {
				String store;
				if (attribute == null)
					store = found.text();
				else
					store = found.attributes().get(attribute);
				String ans = processor.process(store);
				destination.put(property, ans);
				
				ret |= ans != null;
			}
		}
		return ret;
	}

}
