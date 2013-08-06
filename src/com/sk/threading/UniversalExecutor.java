package com.sk.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UniversalExecutor {

	private static final int COMMUNICATION_THREAD_COUNT = 5;
	private static final int SEARCH_THREAD_COUNT = 20;
	public static ExecutorService search = Executors.newFixedThreadPool(SEARCH_THREAD_COUNT);
	public static ExecutorService communicate = Executors.newFixedThreadPool(COMMUNICATION_THREAD_COUNT);

	public static void shutdown() {
		search.shutdown();
		communicate.shutdown();
	}

}
