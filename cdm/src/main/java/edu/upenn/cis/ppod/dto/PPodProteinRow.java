package edu.upenn.cis.ppod.dto;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class PPodProteinRow extends PPodMolecularRow {
	public final static Set<java.lang.Character> LEGAL_CHARS =
			ImmutableSet.of(
					'A',
					'C',
					'D',
					'E',
					'F',
					'G',
					'H',
					'I',
					'K',
					'L',
					'M',
					'N',
					'P',
					'Q',
					'R',
					'S',
					'T',
					'V',
					'W',
					'X',
					'Y',
					'*',
					'1',
					'2',
					'3',
					'4',
					'-',
					'?');

	public final static Set<Character> DELIMITERS =
			ImmutableSet.of(
					'(',
					')',
					'{',
					'}');

	public final static Set<Character> LEGAL_CHARS_AND_DELIMITERS =
			ImmutableSet.copyOf(Sets.union(DELIMITERS, LEGAL_CHARS));

	PPodProteinRow() {}

	public PPodProteinRow(final Long version, final String sequence) {
		super(version, sequence);
	}

	public PPodProteinRow(final String sequence) {
		super(sequence);
	}

	@Override
	Set<Character> getLegalCharsAndDelimiters() {
		return LEGAL_CHARS_AND_DELIMITERS;
	}

}
