package com.sk.web;

public class OAuthToken extends Token {

	private final String secret;

	public OAuthToken(String key, String secret) {
		super(key);
		this.secret = secret;
	}

	public String getSecret() {
		return secret;
	}

	@Override
	public String toString() {
		return String.format("OAuthToken: KEY %s SECRET %s", getKey(), getSecret());
	}

}
