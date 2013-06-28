package com.sk.util.parse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sk.util.PersonalData;

public class BasicGrabber implements Grabber {

	private final String selector, attribute, property;

	/**
	 * Creates a new {@link Grabber} that grabs all of the text from the first selected element and places it
	 * into the PersonalData with the key of property
	 * 
	 * @param selector
	 *            The css selector to find elements
	 * @param property
	 *            The property key to store the data
	 */
	public BasicGrabber(String selector, String property) {
		this(selector, null, property);
	}

	/**
	 * Creates a new {@link Grabber} that grabs the attribute from the first selected element and places it
	 * into the {@link PersonalData} wth the key of the property
	 * 
	 * @param selector
	 *            The css selector to find elements
	 * @param attribute
	 *            The attribute to grab
	 * @param property
	 *            The property key to store the data
	 */
	public BasicGrabber(String selector, String attribute, String property) {
		this.selector = selector;
		this.attribute = attribute;
		this.property = property;
	}

	@Override
	public boolean grab(Document source, PersonalData destination) {
		Elements found = source.select(selector);
		if (!found.isEmpty()) {
			Element first = found.first();
			String store;
			if (attribute == null)
				store = first.text();
			else
				store = first.attributes().get(attribute);
			if (store.length() > 0) {
				destination.put(property, store);
				return true;
			}
		}
		return false;
	}
}
