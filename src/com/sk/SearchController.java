package com.sk;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sk.api.impl.FourSquareApiSearcher;
import com.sk.api.impl.GooglePlusApiSearcher;
import com.sk.api.impl.LinkedinApiSearcher;
import com.sk.api.impl.PiplApiSearcher;
import com.sk.api.impl.TwitterApiSearcher;
import com.sk.impl.search.WhitepagesSearcher;
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
	private NameSearcher[] use = { new WhitepagesSearcher(), new LinkedinApiSearcher(), new PiplApiSearcher(),
			new FourSquareApiSearcher(), new GooglePlusApiSearcher(), new TwitterApiSearcher() };

	private final Map<String, PersonalDataStorage> cache = new WeakHashMap<>();

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
		String joined = first + "|" + last;
		PersonalDataStorage store;
		boolean ret = false;

		if ((store = cache.get(joined)) != null) {
			ret = true;
		} else {
			this.store.remove();
			store = new PersonalDataStorage();
			List<Future<PersonalData[]>> futures = new ArrayList<>();
			for (NameSearcher n : use) {
				futures.add(Driver.EXECUTOR.submit(new SearchRunnable(n, first, last)));
			}
			for (int i = 0; i < futures.size(); ++i) {
				try {
					Future<PersonalData[]> fut = futures.get(i);
					PersonalData[] dat = fut.get();
					if (dat.length > 0) {
						ret = true;
						store.add(dat);
					}
				} catch (InterruptedException e) {
					--i;
					continue;
				} catch (ExecutionException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		cache.put(joined, store);

		if (ret)
			this.store.set(store);
		return ret;
	}

	private class SearchRunnable implements Callable<PersonalData[]> {

		private final NameSearcher ns;
		private final String first, last;

		private SearchRunnable(NameSearcher ns, String first, String last) {
			this.ns = ns;
			this.first = first;
			this.last = last;
		}

		@Override
		public PersonalData[] call() throws Exception {
			if (ns.lookForName(first, last))
				return ns.getData();
			else
				return new PersonalData[0];
		}
	}

}
