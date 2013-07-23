package com.sk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject;
import com.sk.util.PersonalDataStorage;

/**
 * Main class. Run this to search for people.
 * 
 * @author Strikeskids
 * 
 */
public class Driver {
	private static final int total = 25;

	public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				EXECUTOR.shutdown();
			}
		}));
	}

	public static void main(String[] args) throws IllegalStateException, IOException {
		SearchController searcher = new SearchController();
		JsonObject output = new JsonObject();
		while (true) {
			String[] names = nextName();
			if (names == null)
				break;
			String first = names[0], last = names[1];
			long start = System.currentTimeMillis();
			System.out.printf("Searching for %s %s...%n", first, last);
			if (searcher.lookForName(first, last)) {
				PersonalDataStorage pds = searcher.getDataStorage();
				output.add(first + "|" + last, PersonalDataStorage.getStorageGson().toJsonTree(pds));
				System.out.printf("Found %d possible results%n", pds.size());
			} else {
				System.out.println("None found");
			}
			System.out.printf("Query took %.3f seconds%n", (System.currentTimeMillis() - start) / 1000d);
		}
		System.out.print("Output file location: ");
		Scanner in = new Scanner(System.in);
		String file = in.nextLine();
		in.close();
		BufferedWriter w = new BufferedWriter(new FileWriter(file));
		w.append(output.toString());
		w.close();
		System.exit(0);
	}

	private static int count = 0;

	public static String[] nextName() {
		if (++count > total)
			return null;
		if (firsts == null) {
			try {
				BufferedReader r = new BufferedReader(new FileReader("firstnames.txt"));
				firsts = new LinkedList<String>();
				String first;
				while ((first = r.readLine()) != null) {
					firsts.add(first.toLowerCase());
				}
				r.close();
				Collections.shuffle(firsts);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (lasts == null) {
			try {
				BufferedReader r = new BufferedReader(new FileReader("lastnames.txt"));
				lasts = new LinkedList<String>();
				String last;
				while ((last = r.readLine()) != null) {
					lasts.add(last.toLowerCase());
				}
				r.close();
				Collections.shuffle(lasts);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String first = firsts.pollFirst();
		firsts.addLast(first);
		String last = lasts.pollFirst();
		lasts.addLast(last);
		return new String[] { first, last };
	}

	private static LinkedList<String> firsts, lasts;

	// public static String[] nextName() {
	// Scanner s = new Scanner(System.in);
	// String line = s.nextLine();
	// s.close();
	// if (line.length() == 0)
	// return null;
	// if (line.contains("|")) {
	// return line.split("[|]");
	// } else {
	// return line.split(" ");
	// }
	// }
}
