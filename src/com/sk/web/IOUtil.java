package com.sk.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;

public class IOUtil {

	public static final String CHARSET_NAME = "UTF-8";
	public static final Charset CHARSET = Charset.forName(CHARSET_NAME);

	private static final String NONCE_ALPHABET = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik9ol0pQAZWSXEDCRFVTGBYHNUJMIKOLP";
	private static final Random random = new Random();

	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, CHARSET_NAME).replaceAll(Pattern.quote("+"), "%20")
					.replaceAll(Pattern.quote("*"), "%2A");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String urlDecode(String s) {
		try {
			return URLDecoder.decode(s, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, String> splitParams(String query) {
		Map<String, String> ret = new HashMap<>();
		if (query != null) {
			for (String part : query.split("&")) {
				int eqIndex = part.indexOf('=');
				ret.put(IOUtil.urlDecode(part.substring(0, eqIndex)),
						IOUtil.urlDecode(part.substring(eqIndex + 1)));
			}
		}
		return ret;
	}

	public static String joinParams(Map<String, String> params) {
		StringBuilder query = new StringBuilder();
		for (Entry<String, String> param : params.entrySet()) {
			if (query.length() > 0)
				query.append("&");
			query.append(IOUtil.urlEncode(param.getKey()));

			query.append("=");
			query.append(IOUtil.urlEncode(param.getValue()));
		}
		return query.toString();
	}

	public static String generateNonce() {
		return generateNonce(32);
	}

	public static String generateNonce(int length) {
		return generateString(NONCE_ALPHABET, length);
	}

	public static String generateString(String alphabet, int length) {
		if (alphabet.length() == 0)
			return null;
		StringBuilder ret = new StringBuilder(length);
		for (int i = 0; i < length; ++i)
			ret.append(alphabet.charAt(random.nextInt(alphabet.length())));
		return ret.toString();
	}

	public static String read(Request request) throws IOException {
		URLConnection conn = request.openConnection();
		conn.setDoInput(true);
		conn.connect();
		return read(conn.getInputStream());
	}

	public static String read(File file) throws IOException {
		return read(new FileInputStream(file));
	}

	public static String read(InputStream stream) throws IOException {
		return read(new InputStreamReader(stream, CHARSET));
	}

	public static String read(Reader inputReader) throws IOException {
		BufferedReader reader = new BufferedReader(inputReader);
		StringBuilder ret = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			ret.append(line);
			ret.append("\n");
		}
		reader.close();
		return ret.toString();
	}

}
