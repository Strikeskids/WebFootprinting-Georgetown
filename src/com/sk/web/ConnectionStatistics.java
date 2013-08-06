package com.sk.web;

import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionStatistics {

	private static final Map<String, AtomicInteger> connectionCount = new ConcurrentHashMap<>();

	public static void connecting(URL url) {
		String host = url.getHost();
		synchronized (connectionCount) {
			if (connectionCount.containsKey(host))
				connectionCount.get(host).incrementAndGet();
			else
				connectionCount.put(host, new AtomicInteger(1));
		}
	}

	public static String getSummaryString() {
		StringBuilder ret = new StringBuilder();
		for (Entry<String, AtomicInteger> entry : connectionCount.entrySet()) {
			ret.append(String.format("Host %10s %4d", entry.getKey(), entry.getValue().get()));
			ret.append("\n");
		}
		return ret.toString();
	}
}
