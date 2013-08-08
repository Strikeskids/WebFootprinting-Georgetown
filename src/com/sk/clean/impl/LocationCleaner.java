package com.sk.clean.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk.clean.DataCleaner;
import com.sk.parse.util.Parsers;
import com.sk.util.data.FieldBuilder;
import com.sk.util.data.PersonalData;
import com.sk.web.IOUtil;
import com.sk.web.Request;

public class LocationCleaner implements DataCleaner {

	private static final String ADDRESS_COMPONENTS_KEY = "address_components";
	private static final String TYPES_ARRAY_KEY = "types";
	private static final String COMPONENT_VALUE_KEY = "short_name";
	private static final String RESULTS_KEY = "results";
	private static final String STATUS_KEY = "status";

	private static final String VALID_STATUS_VALUE = "OK";

	private static final String PRIMARY_LOCATION_FIELD = "location";
	private static final String PRIMARY_ADDRESS_FIELD = "address";
	private static final String[] LOCATION_FIELDS = { PRIMARY_ADDRESS_FIELD, PRIMARY_LOCATION_FIELD, "zipcode",
			"house", "apartment", "street", "state", "country", "city", "poBox" };

	private static final String CONFUSING_STRING_REGEX = "(?:(?i)metro area|\\.)";
	private static final Pattern WORD_DELIMITER_PATTERN = Pattern.compile("_([a-z0-9])");

	private static final String BASE_URL = "http://maps.googleapis.com/maps/api/geocode/json?language=en&sensor=false&address=%s";

	@Override
	public boolean clean(PersonalData dirty, PersonalData clean) {
		String[] locations = getPreferredLocationValues(dirty);
		if (locations == null)
			return false;
		FieldBuilder cleanedLocations = getCleanedLocations(locations);
		cleanedLocations.addTo(clean);
		removeLocationFields(dirty);
		return true;
	}

	private FieldBuilder getCleanedLocations(String... locations) {
		FieldBuilder ret = new FieldBuilder();
		for (String location : locations) {
			ret = cleanLocation(ret, location);
		}
		return ret;
	}

	private FieldBuilder cleanLocation(FieldBuilder output, String location) {
		JsonObject geocodedData = getGeocodedData(location);
		if (!hasValidStatus(geocodedData))
			return output;
		JsonArray results = geocodedData.get(RESULTS_KEY).getAsJsonArray();
		for (JsonElement resultElement : results) {
			JsonArray addressComponents = getAddressComponents(resultElement);
			addAddressComponents(output, addressComponents);
		}
		return output;
	}

	private String[] getPreferredLocationValues(PersonalData in) {
		if (in.containsKey(PRIMARY_ADDRESS_FIELD)) {
			return in.getAllValues(PRIMARY_ADDRESS_FIELD);
		} else if (in.containsKey(PRIMARY_LOCATION_FIELD)) {
			String[] ret = in.getAllValues(PRIMARY_LOCATION_FIELD);
			return removeConfusingStrings(ret);
		} else {
			return null;
		}
	}

	private String[] removeConfusingStrings(String[] confusedString) {
		for (int i = 0; i < confusedString.length; ++i)
			confusedString[i] = confusedString[i].replaceAll(CONFUSING_STRING_REGEX, "");
		return confusedString;
	}

	private boolean hasValidStatus(JsonObject object) {
		return object.has(STATUS_KEY) && object.get(STATUS_KEY).getAsString().equals(VALID_STATUS_VALUE);
	}

	private JsonArray getAddressComponents(JsonElement resultElement) {
		JsonObject result = resultElement.getAsJsonObject();
		return result.get(ADDRESS_COMPONENTS_KEY).getAsJsonArray();
	}

	private FieldBuilder addAddressComponents(FieldBuilder builder, JsonArray addressComponents) {
		for (int i = addressComponents.size() - 1; i >= 0; --i) {
			JsonObject addressComponent = addressComponents.get(i).getAsJsonObject();
			String typeName = getType(addressComponent);
			if (typeName == null)
				continue;
			String newName = formatTypeName(typeName);
			builder.put(addressComponent, COMPONENT_VALUE_KEY, newName);
		}
		return builder;
	}

	private String getType(JsonObject addressComponent) {
		JsonArray typesArray = addressComponent.get(TYPES_ARRAY_KEY).getAsJsonArray();
		if (typesArray.size() == 0)
			return null;
		else
			return typesArray.get(0).getAsString();
	}

	private String formatTypeName(String typeName) {
		StringBuffer newType = new StringBuffer("L");
		Matcher wordDelimiterMatcher = WORD_DELIMITER_PATTERN.matcher(typeName);
		while (wordDelimiterMatcher.find()) {
			String firstLetter = wordDelimiterMatcher.group(1);
			wordDelimiterMatcher.appendReplacement(newType, firstLetter.toUpperCase());
		}
		wordDelimiterMatcher.appendTail(newType);
		return newType.toString();
	}

	private void removeLocationFields(PersonalData input) {
		for (String field : LOCATION_FIELDS) {
			input.remove(field);
		}
	}

	private JsonObject getGeocodedData(String location) {
		String jsonString = getJsonString(location);
		return Parsers.parseJSON(jsonString).getAsJsonObject();
	}

	private String getJsonString(String location) {
		try {
			Request geocodeRequest = getGeocodeRequest(location);
			return IOUtil.read(geocodeRequest);
		} catch (IOException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	private Request getGeocodeRequest(String location) throws MalformedURLException {
		String encodedLocation = IOUtil.urlEncode(location);
		String url = String.format(BASE_URL, encodedLocation);
		Request ret = new Request(url);
		ret.addRandomUserAgent();
		return ret;
	}
}
