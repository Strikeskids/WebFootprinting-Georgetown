package com.sk;

import java.io.IOException;
import java.net.URL;

import com.sk.api.impl.LinkedinApiSearcher;
import com.sk.impl.search.AllNameSearcher;
import com.sk.util.PersonalData;
import com.sk.util.PersonalDataStorage;
import com.sk.util.parse.search.NameSearcher;

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
public class SearchController implements NameSearcher {

	private final ThreadLocal<PersonalDataStorage> store = new ThreadLocal<>();
	private NameSearcher[] use = { new AllNameSearcher(), new LinkedinApiSearcher() };

	@Override
	public URL[] results() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PersonalData[] getData() throws IllegalStateException {
		return getDataStorage().toArray();
	}

	public PersonalDataStorage getDataStorage() throws IllegalStateException {
		PersonalDataStorage ret = store.get();
		if (ret == null)
			throw new IllegalArgumentException();
		else
			return ret;
	}

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		boolean ret = false;
		PersonalDataStorage store = new PersonalDataStorage();
		for (NameSearcher n : use) {
			boolean success = n.lookForName(first, last);
			ret |= success;
			if (success)
				store.add(n.getData());
		}
		if (ret)
			this.store.set(store);
		return ret;
	}

}
