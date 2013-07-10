package com.sk.api.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.pipl.api.data.containers.Record;
import com.pipl.api.data.fields.Address;
import com.pipl.api.data.fields.DOB;
import com.pipl.api.data.fields.Education;
import com.pipl.api.data.fields.Email;
import com.pipl.api.data.fields.Job;
import com.pipl.api.data.fields.Name;
import com.pipl.api.data.fields.Phone;
import com.pipl.api.data.fields.Username;
import com.pipl.api.search.SearchAPIError;
import com.pipl.api.search.SearchAPIRequest;
import com.pipl.api.search.SearchAPIResponse;
import com.sk.api.ApiUtility;
import com.sk.impl.ScrapeController;
import com.sk.util.PersonalData;
import com.sk.util.parse.search.NameSearcher;

public class PiplApiSearcher implements NameSearcher {

	private final String key;
	private final ThreadLocal<URL[]> urls = new ThreadLocal<>();
	private final ThreadLocal<PersonalData[]> data = new ThreadLocal<>();

	public PiplApiSearcher() {
		JsonObject obj = ApiUtility.getTokensFor("PiplApi");
		if (obj.has("client_key"))
			key = obj.get("client_key").getAsString();
		else
			throw new RuntimeException("Failed to get key");
	}

	@Override
	public boolean lookForName(String first, String last) throws IOException {
		SearchAPIRequest req = new SearchAPIRequest.Builder().firstName(first).lastName(last).apiKey(key).build();
		SearchAPIResponse resp;
		try {
			resp = req.send();
		} catch (SearchAPIError | URISyntaxException e) {
			return false;
		}
		ScrapeController scrape = ScrapeController.getController();
		List<PersonalData> data = new ArrayList<>();
		List<URL> url = new ArrayList<>();
		for (Record possible : resp.getRecords()) {
			PersonalData cur = new PersonalData("pipl");
			for (Name n : possible.getNames()) {
				cur.put("name", n.getRaw());
				cur.put("first-name", n.getFirst());
				cur.put("last-name", n.getLast());
				break;
			}
			for (Address a : possible.getAddresses()) {
				cur.put("address", a.getRaw());
				cur.put("country", a.getCountry());
				cur.put("city", a.getCity());
				cur.put("house", a.getHouse());
				cur.put("apartament", a.getApartment());
				cur.put("po_box", a.getPoBox());
				cur.put("street", a.getStreet());
				cur.put("state", a.getState());
				break;
			}
			for (Phone ph : possible.getPhones()) {
				cur.put(ph.getType().replaceAll("_", "-"), ph.getDisplay());
			}
			for (Email e : possible.getEmails()) {
				cur.put("email", e.getAddress());
				break;
			}
			for (Username u : possible.getUsernames()) {
				cur.put("username", u.getContent());
				break;
			}
			for (DOB d : possible.getDobs()) {
				cur.put("age", d.age() + "");
				cur.put("dob", DateFormat.getDateInstance().format(d.getDateRange().middle()));
				break;
			}
			for (Job j : possible.getJobs()) {
				cur.put("job-title", j.getTitle());
				cur.put("industry", j.getIndustry());
				break;
			}
			for (Education e : possible.getEducations()) {
				cur.put("education", e.display());
			}
			if (scrape.isValid(possible.getSource().getUrl()))
				url.add(new URL(possible.getSource().getUrl()));
		}
		urls.set((URL[]) url.toArray());
		this.data.set((PersonalData[]) data.toArray());
		return true;
	}

	@Override
	public URL[] results() throws IllegalStateException {
		URL[] ret = urls.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}

	@Override
	public PersonalData[] getData() throws IllegalStateException {
		PersonalData[] ret = data.get();
		if (ret == null)
			throw new IllegalStateException();
		else
			return ret;
	}
}
