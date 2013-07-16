package com.sk.util.parse.scrape;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sk.util.FieldBuilder;
import com.sk.util.PersonalData;

public class BasicGrabber implements Grabber {

	protected final String selector;
	protected final String attribute;
	protected final String property;

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
				destination.put(property, store);
				ret |= store.length() > 0;
			}
		}
		return ret;
	}
}
