package com.sk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import com.google.gson.JsonObject;
import com.sk.threading.UniversalExecutor;
import com.sk.util.data.DataGson;
import com.sk.util.data.PersonalDataStorage;

/**
 * Main class. Run this to search for people.
 * 
 * @author Strikeskids
 * 
 */
public class Driver {

	private static int total = 25;

	public static void main(String[] args) throws IllegalStateException, IOException {
		if (Arrays.asList(args).contains("-gt")) {
			if (args.length < 3) {
				System.out.println("Usage: -gt number file");
			}
			total = Integer.parseInt(args[1]);
			JsonObject output = new JsonObject();
			while (true) {
				String[] names = nextName();
				if (names == null)
					break;
				String first = names[0], last = names[1];
				long start = System.currentTimeMillis();
				System.out.printf("Searching for %s %s...%n", first, last);
				PersonalDataStorage pds = SearchController.lookForName(first, last);
				if (pds.size() > 0) {
					output.add(first + "|" + last, DataGson.getGson().toJsonTree(pds));
					System.out.printf("Found %d possible results%n", pds.size());
				} else {
					System.out.println("None found");
				}
				System.out.printf("Query took %.3f seconds%n", (System.currentTimeMillis() - start) / 1000d);
			}
			String file = args[2];
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			w.append(output.toString());
			w.close();
		} else if (Arrays.asList(args).contains("-s") && args.length >= 2) {
			ServerSocket server = new ServerSocket(Integer.parseInt(args[1]), 3);
			while (true) {
				try {
					final Socket nextSock = server.accept();
					System.out.println("Received socket");
					UniversalExecutor.communicate.submit(new PhpCommunicator(nextSock));
				} catch (SocketTimeoutException ex) {
					break;
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			server.close();
		}
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
