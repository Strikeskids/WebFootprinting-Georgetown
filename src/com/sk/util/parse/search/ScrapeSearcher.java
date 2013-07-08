package com.sk.util.parse.search;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sk.impl.ScrapeController;
import com.sk.util.PersonalData;
import com.sk.util.parse.scrape.Scraper;

public abstract class ScrapeSearcher extends AbstractParseSearcher {

	private final ThreadLocal<PersonalData[]> data = new ThreadLocal<>();

	@Override
	protected void reset() {
		super.reset();
		data.remove();
	}

	@Override
	public PersonalData[] getData() throws IllegalStateException {
		PersonalData[] ret = data.get();
		Scraper scraper = ScrapeController.getController();
		if (ret == null) {
			List<PersonalData> out = new ArrayList<>();
			for (URL url : results()) {
				try {
					scraper.load(url);
				} catch (IOException e) {
					continue;
				}
				if (scraper.parse())
					out.add(scraper.get());
			}
			ret = out.toArray(new PersonalData[out.size()]);
			data.set(ret);
		}
		return ret;
	}

}
