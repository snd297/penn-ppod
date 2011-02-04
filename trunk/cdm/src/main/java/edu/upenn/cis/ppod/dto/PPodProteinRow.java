package edu.upenn.cis.ppod.dto;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

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
					'?',
					'(',
					')',
					'{',
					'}');

	PPodProteinRow() {}

	public PPodProteinRow(final Long version, final String sequence) {
		super(version, sequence);
	}

	public PPodProteinRow(final String sequence) {
		super(sequence);
	}

	@Override
	Set<Character> getLegalChars() {
		return LEGAL_CHARS;
	}

}
