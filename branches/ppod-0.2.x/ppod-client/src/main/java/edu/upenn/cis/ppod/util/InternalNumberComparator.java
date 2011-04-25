/* from http://stackoverflow.com/questions/104599/sort-on-a-string-that-may-contain-a-number */
package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InternalNumberComparator implements Comparator<String> {
	private static Pattern splitter = Pattern.compile("(\\d+|\\D+)");

	private static ArrayList<String> split(final String s) {
		final ArrayList<String> r = newArrayList();
		final Matcher matcher = splitter.matcher(s);
		while (matcher.find()) {
			final String m = matcher.group(1);
			r.add(m);
		}
		return r;
	}

	public int compare(String s1, String s2) {
		checkNotNull(s1);
		checkNotNull(s2);

		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		// We split each string as runs of number/non-number strings
		final ArrayList<String> sa1 = split(s1);
		final ArrayList<String> sa2 = split(s2);
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
			si1 = sa1.get(i);
			si2 = sa2.get(i);
			if (!si1.equals(si2)) {
				break; // Until we find a difference
			}
		}
		// No difference found?
		if (i == sa1.size()) {
			return 0; // Same strings!
		}

		// Try to convert the different run of characters to number
		int val1, val2;
		try {
			val1 = Integer.parseInt(si1);
			val2 = Integer.parseInt(si2);
		} catch (final NumberFormatException e) {
			return s1.compareTo(s2); // Strings differ on a non-number
		}

		// Compare remainder of string
		for (i++; i < sa1.size(); i++) {
			si1 = sa1.get(i);
			si2 = sa2.get(i);
			if (!si1.equals(si2)) {
				return s1.compareTo(s2); // Strings differ
			}
		}

		// Here, the strings differ only on a number
		return val1 < val2 ? -1 : 1;
	}
}
