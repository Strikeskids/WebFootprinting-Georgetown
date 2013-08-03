package com.sk.web;

public class Token {

	private final String key;

	public Token(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return String.format("Token %s", getKey());
	}
}
