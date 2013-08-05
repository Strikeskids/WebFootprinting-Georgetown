package com.sk.impl2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk.parse.Parsers;
import com.sk.util.LazyField;
import com.sk.web.IOUtil;
import com.sk.web.OAuthToken;
import com.sk.web.Token;

public class ApiUtility {

	private static final File TOKEN_STORE = new File("tokens2.json");

	public static OAuthToken getConsumerToken(String site) {
		JsonObject siteObject = getSiteObject(site);
		if (siteObject.has("client_key") && siteObject.has("client_secret")) {
			return new OAuthToken(siteObject.get("client_key").getAsString(), siteObject.get("client_secret")
					.getAsString());
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
		if (siteObject.has("users"))
			return siteObject.get("users").getAsJsonObject().entrySet();
		else
			return new HashSet<>(0, 1f);
	}

	private static Token extractToken(JsonElement userElement) {
		JsonObject user = userElement.getAsJsonObject();
		if (user.has("token")) {
			String token = user.get("token").getAsString();
			if (user.has("secret")) {
				String secret = user.get("secret").getAsString();
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
