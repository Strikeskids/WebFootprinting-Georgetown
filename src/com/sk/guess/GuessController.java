package com.sk.guess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sk.clean.Cleaner;
import com.sk.util.PersonalData;
import com.sk.util.PersonalDataStorage;

/**
 * Handles guessing the information about a certain person using the data from sites
 * 
 * @author Strikeskids
 * 
 */
public class GuessController {

	public GuessController() {
	}

	public GuessedData guess(String first, String last, PersonalDataStorage input) {
		Cleaner cleaner = new Cleaner();
		List<PersonalData> cleaned = new ArrayList<>();
		Set<String> attributes = new LinkedHashSet<>();
		for (PersonalData dirty : input.toArray()) {
			PersonalData copy = new PersonalData(dirty.getWebsiteId(), dirty);
			PersonalData clean = cleaner.clean(copy);
			if (clean.size() == 0)
				continue;
			cleaned.add(clean);
			for (String attr : clean.keySet())
				attributes.add(attr);
		}
		PersonalData[] cleanArray = cleaned.toArray(new PersonalData[cleaned.size()]);
		GuessedData ret = new GuessedData(first, last);
		Set<Character> prefixes = new HashSet<>();
		for (String attr : attributes) {
			if (prefixes.contains(attr.charAt(0)))
				continue;
			ret.generateGuess(attr, cleanArray);
			if (Character.isUpperCase(attr.charAt(0)) && ret.get(attr).getGuesses().length == 0) {
				prefixes.add(attr.charAt(0));
			}

		}
		return ret;
	}
}
