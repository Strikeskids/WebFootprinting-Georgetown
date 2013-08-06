package com.sk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk.parse.util.Parsers;
import com.sk.web.IOUtil;
import com.sk.web.OAuthToken;
import com.sk.web.Token;

public class ApiUtility {

	private static final String ACCESS_SECRET_KEY = "secret";
	private static final String ACCESS_TOKEN_KEY = "token";
	private static final String USER_KEY = "users";
	private static final String CLIENT_SECRET_KEY = "client_secret";
	private static final String CLIENT_TOKEN_KEY = "client_key";

	private static final File TOKEN_STORE = new File("tokensNew.json");

	public static OAuthToken getConsumerToken(String site) {
		JsonObject siteObject = getSiteObject(site);
		if (siteObject.has(CLIENT_TOKEN_KEY) && siteObject.has(CLIENT_SECRET_KEY)) {
			return new OAuthToken(siteObject.get(CLIENT_TOKEN_KEY).getAsString(), siteObject
					.get(CLIENT_SECRET_KEY).getAsString());
		} else {
			return null;
		}
	}

	public static OAuthToken getOAuthToken(String site) {
		JsonObject siteObject = getSiteObject(site);
		for (Entry<String, JsonElement> userEntry : getUsers(siteObject)) {
			Token ret = extractToken(userEntry.getValue());
			if (ret != null && ret instanceof OAuthToken)
				return (OAuthToken) ret;
		}
		return null;
	}

	private static Set<Entry<String, JsonElement>> getUsers(JsonObject siteObject) {
		if (siteObject.has(USER_KEY))
			return siteObject.get(USER_KEY).getAsJsonObject().entrySet();
		else
			return new HashSet<>(0, 1f);
	}

	private static Token extractToken(JsonElement userElement) {
		JsonObject user = userElement.getAsJsonObject();
		if (user.has(ACCESS_TOKEN_KEY)) {
			String token = user.get(ACCESS_TOKEN_KEY).getAsString();
			if (user.has(ACCESS_SECRET_KEY)) {
				String secret = user.get(ACCESS_SECRET_KEY).getAsString();
				return new OAuthToken(token, secret);
			} else {
				return new Token(token);
			}
		} else
			return null;
	}

	public static Token getAccessToken(String site) {
		JsonObject siteObject = getSiteObject(site);
		for (Entry<String, JsonElement> userEntry : getUsers(siteObject)) {
			Token token = extractToken(userEntry.getValue());
			if (token != null)
				return token;
		}
		return getNamedToken(site, CLIENT_TOKEN_KEY);
	}

	public static Token getNamedToken(String site, String name) {
		JsonObject siteObject = getSiteObject(site);
		if (siteObject.has(name))
			return new Token(siteObject.get(name).getAsString());
		else
			return null;
	}

	private static JsonObject getSiteObject(String site) {
		JsonObject sites = tokenObject.get();
		if (sites.has(site))
			return sites.get(site).getAsJsonObject();
		else
			return new JsonObject();
	}

	private static LazyField<JsonObject> tokenObject = new LazyField<>(new Callable<JsonObject>() {
		@Override
		public JsonObject call() throws Exception {
			InputStream tokenStream = new FileInputStream(TOKEN_STORE);
			JsonObject ret = Parsers.parseJSON(IOUtil.readFrom(tokenStream)).getAsJsonObject();
			tokenStream.close();
			return ret;
		}
	});

}
