/* from http://stackoverflow.com/questions/104599/sort-on-a-string-that-may-contain-a-number */
package edu.upenn.cis.ppod.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InternalNumberComparator implements Comparator<String> {
	Pattern splitter = Pattern.compile("(\\d+|\\D+)");

	public int compare(String s1, String s2) {
		// I deliberately use the Java 1.4 syntax,
		// all this can be improved with 1.5's generics

		// We split each string as runs of number/non-number strings
		ArrayList sa1 = split(s1);
		ArrayList sa2 = split(s2);
		// Nothing or different structure
		if (sa1.size() == 0 || sa1.size() != sa2.size()) {
			// Just compare the original strings
			return s1.compareTo(s2);
		}
		int i = 0;
		String si1 = "";
		String si2 = "";
		// Compare beginning of string
		for (; i < sa1.size(); i++) {
			si1 = (String) sa1.get(i);
			si2 = (String) sa2.get(i);
			if (!si1.equals(si2))
				break; // Until we find a difference
		}
		// No difference found?
		if (i == sa1.size())
			return 0; // Same strings!

		// Try to convert the different run of characters to number
		int val1, val2;
		try {
			val1 = Integer.parseInt(si1);
			val2 = Integer.parseInt(si2);
		} catch (NumberFormatException e) {
			return s1.compareTo(s2); // Strings differ on a non-number
		}

		// Compare remainder of string
		for (i++; i < sa1.size(); i++) {
			si1 = (String) sa1.get(i);
			si2 = (String) sa2.get(i);
			if (!si1.equals(si2)) {
				return s1.compareTo(s2); // Strings differ
			}
		}

		// Here, the strings differ only on a number
		return val1 < val2 ? -1 : 1;
	}

	ArrayList split(String s) {
		ArrayList r = new ArrayList();
		Matcher matcher = splitter.matcher(s);
		while (matcher.find()) {
			String m = matcher.group(1);
			r.add(m);
		}
		return r;
	}
}
