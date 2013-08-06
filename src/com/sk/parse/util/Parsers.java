package com.sk.parse.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Parsers {

	private static final JsonParser jsonParser = new JsonParser();

	public static JsonElement parseJSON(String input) {
		return jsonParser.parse(input);
	}

	public static Document parseXML(String input) {
		return Jsoup.parse(input, "", Parser.xmlParser());
	}

	public static Document parseHTML(String input, String baseUri) {
		return Jsoup.parse(input, baseUri, Parser.htmlParser());
	}
}
