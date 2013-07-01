package com.sk.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

/**
 * This contains all the data about an individual garnered from a specific website <br />
 * Keys used
 * <table>
 * <tr>
 * <th>Key</th>
 * <th>Purpose</th>
 * </tr>
 * <tr>
 * <td>name</td>
 * <td>Individual's name</td>
 * </tr>
 * <tr>
 * <td>first-name</td>
 * <td>Individual's first name (or given name)</td>
 * </tr>
 * <tr>
 * <td>last-name</td>
 * <td>Individual's last name (or family name)</td>
 * </tr>
 * <tr>
 * <td>industry</td>
 * <td>The industry where the individual works</td>
 * </tr>
 * <tr>
 * <td>profile-picture-url</td>
 * <td>A {@link URL} to the individual's profile picture</td>
 * </tr>
 * <tr>
 * <td>education</td>
 * <td>A summary of the individual's education</td>
 * </tr>
 * <tr>
 * <td>job-title</td>
 * <td>The individual's job title</td>
 * </tr>
 * <tr>
 * <td>location</td>
 * <td>The individual's location</td>
 * </tr>
 * <tr>
 * <td>phone-home</td>
 * <td>Individual's home phone</td>
 * </tr>
 * <tr>
 * <td>phone-work</td>
 * <td>Individuals' work phone</td>
 * </tr>
 * <tr>
 * <td>phone-mobile</td>
 * <td>Individual's mobile phone</td>
 * </tr>
 * <tr>
 * <td>address</td>
 * <td>Individual's home address. Similar to location</td>
 * </tr>
 * <tr>
 * <td>age</td>
 * <td>individual's age</td>
 * </tr>
 * <tr>
 * <td>home-page</td>
 * <td>Individual's home page</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td></td>
 * </tr>
 * </table>
 * 
 * @author Strikeskids
 * 
 */
public class PersonalData extends HashMap<String, String> {

	private final String siteId;
	private final List<URL> adjacent = new ArrayList<URL>();

	public PersonalData(String source) {
		this.siteId = source;
	}

	public PersonalData(String source, int initialCapacity) {
		super(initialCapacity);
		this.siteId = source;
	}

	public PersonalData(String source, Map<? extends String, ? extends String> m) {
		super(m);
		this.siteId = source;
	}

	public PersonalData(String source, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		this.siteId = source;
	}

	public Optional<String> get(String attribute) {
		if (containsKey(attribute))
			return Optional.of(super.get(attribute));
		else
			return Optional.absent();
	}

	public void addAdjacent(URL... urls) {
		Collections.addAll(adjacent, urls);
	}

	public List<URL> getAdjacent() {
		return adjacent;
	}

	/**
	 * Get the website id of the source of the {@link PersonalData}
	 * 
	 * @return The website id
	 */
	public String getWebsiteId() {
		return siteId;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("PersonalData (");
		ret.append(siteId);
		ret.append(", ");
		ret.append(super.toString());
		if (adjacent.size() > 0) {
			ret.append(", ");
			ret.append(adjacent);
		}
		ret.append(")");
		return ret.toString();
	}

	private static final long serialVersionUID = 4259139787451939836L;
}
