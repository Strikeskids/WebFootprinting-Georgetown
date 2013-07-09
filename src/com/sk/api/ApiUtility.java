package com.sk.api;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ApiUtility {

	private final Class<? extends Api> type;
	private Token accessToken;
	private OAuthService service;
	private static final ServiceBuilder builder = new ServiceBuilder();

	public ApiUtility(Class<? extends Api> type) {
		this.type = type;
	}

	/**
	 * Initializes the utility for a given user. Will direct browser to page and ask for input of a pin
	 * 
	 * @param user
	 *            The user to initialize for
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void init(String user) {
		if (accessToken != null && service != null)
			return;
		synchronized (this) {
			JsonObject tokens = getTokens();
			if (tokens.has(type.getSimpleName())) {
				tokens = tokens.get(type.getSimpleName()).getAsJsonObject();
				synchronized (builder) {
					this.service = builder.provider(type).apiKey(tokens.get("client_key").getAsString())
							.apiSecret(tokens.get("client_secret").getAsString()).build();
				}
				if (tokens.has("users")) {
					final JsonObject users = tokens.get("users").getAsJsonObject();
					if (users.has(user)) {
						final JsonObject tokenData = users.get(user).getAsJsonObject();
						accessToken = new Token(tokenData.get("token").getAsString(), tokenData.get("secret")
								.getAsString());
						return;
					}
				}
				Token requestToken = service.getRequestToken();
				if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Action.BROWSE))
					throw new RuntimeException("Desktop unsupported");
				try {
					Desktop.getDesktop().browse(new URI(service.getAuthorizationUrl(requestToken)));
				} catch (IOException | URISyntaxException e) {
					throw new RuntimeException("Failed to create URL");
				}
				Scanner sc = new Scanner(System.in);
				System.out.print("Enter code: ");
				String code = sc.nextLine();
				sc.close();
				Verifier v = new Verifier(code);
				this.accessToken = service.getAccessToken(requestToken, v);
				if (!tokens.has("users"))
					tokens.add("users", new JsonObject());
				JsonObject tokenStore = new JsonObject();
				tokenStore.addProperty("token", accessToken.getToken());
				tokenStore.addProperty("secret", accessToken.getSecret());
				tokens.get("users").getAsJsonObject().add(user, tokenStore);
			} else {
				throw new RuntimeException("Invalid API Details");
			}
		}
	}

	/**
	 * Attempts to send an OAuthRequest
	 * 
	 * @param r
	 *            The Request to send
	 * @return The response received from the server
	 */
	public Response send(OAuthRequest r) {
		service.signRequest(accessToken, r);
		return r.send();
	}

	public OAuthService getService() {
		return service;
	}

	public Token getAccessToken() {
		return accessToken;
	}

	private static final String TOKEN_STORE = "tokens.json";
	private static JsonObject tokens;

	private static JsonObject getTokens() {
		if (tokens == null) {
			synchronized (ApiUtility.class) {
				try {
					tokens = new JsonParser().parse(new BufferedReader(new FileReader(TOKEN_STORE)))
							.getAsJsonObject();
				} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
					tokens = new JsonObject();
				}
			}
		}
		return tokens;
	}

	private static void saveTokens() {
		synchronized (ApiUtility.class) {
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(TOKEN_STORE)));
				pw.println(getTokens());
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				saveTokens();
			}
		}));
	}

}
