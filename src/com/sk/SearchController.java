package com.sk;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sk.parse.impl.FacebookApiLoader;
import com.sk.parse.impl.FourSquareApiLoader;
import com.sk.parse.impl.GooglePlusApiLoader;
import com.sk.parse.impl.LinkedinApiLoader;
import com.sk.parse.impl.PiplApiLoader;
import com.sk.parse.impl.TwitterApiLoader;
import com.sk.parse.impl.WhitepagesLoader;
import com.sk.parse.util.Extractor;
import com.sk.parse.util.OuterLoader;
import com.sk.parse.util.PagingLoader;
import com.sk.util.PersonalDataStorage;
import com.sk.web.Request;

/**
 * Controls searching. Just search for a name and it will return a PersonalDataStorage of all of the data
 * found.
 * 
 * <br />
 * Use: <br/>
 * <code>
 * SearchController cont = new SearchController();<br/>
 * cont.lookFor(first, last);<br/>
 * PersonalDataStorage store = cont.getDataStorage();<br/>
 * </code>
 * 
 * @author Strikeskids
 * 
 */
public class SearchController extends OuterLoader {

	private final String first, last;

	public SearchController(String first, String last) {
		this.first = first;
		this.last = last;
	}

	public PersonalDataStorage getResultStorage() {
		PersonalDataStorage ret = new PersonalDataStorage();
		ret.addAll(getResults());
		return ret;
	}

	@Override
	protected List<Extractor> getExtractors() {
		List<Extractor> ret = new ArrayList<>();
		ret.add(new FacebookApiLoader(first, last));
		ret.add(new FourSquareApiLoader(first, last));
		ret.add(new GooglePlusApiLoader(first, last));
		ret.add(new LinkedinApiLoader(first, last));
		ret.add(new PiplApiLoader(first, last));
		ret.add(new TwitterApiLoader(first, last));
		ret.add(new WhitepagesLoader(first, last));
		return ret;
	}

	@Override
	protected boolean loadStopPaging() {
		return true;
	}

	@Override
	protected PagingLoader createNextPage() {
		return null;
	}

	@Override
	protected Request getRequest() {
		return null;
	}

	@Override
	protected void parse(URL source, String data) {
	}

	public static PersonalDataStorage lookForName(String first, String last) {
		return search(first, last).getResultStorage();
	}

	public static SearchController search(String first, String last) {
		return new SearchController(first, last);
	}
}
