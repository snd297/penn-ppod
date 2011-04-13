package edu.upenn.cis.ppod.dto;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class PPodDnaRow extends PPodMolecularRow {

	public final static Set<java.lang.Character> LEGAL_CHARS =
			ImmutableSet.of(
					'A', 'a',
					'C', 'c',
					'G', 'g',
					'T', 't',
					'-',
					'?');

	PPodDnaRow() {}

	public PPodDnaRow(final String sequence) {
		super(sequence);
	}

	@Override
	protected Set<Character> getLegalChars() {
		return LEGAL_CHARS;
	}

}
