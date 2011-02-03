package edu.upenn.cis.ppod.dto;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class PPodProteinRow extends PPodMolecularRow {
	public final static Set<java.lang.Character> LEGAL_CHARS =
			ImmutableSet.of(
					'A',
					'B',
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
					'Z',
					'-',
					'?');

	@Override
	protected Set<Character> getLegalChars() {
		return LEGAL_CHARS;
	}
}
