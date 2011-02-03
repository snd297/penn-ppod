package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

abstract class PPodMolecularRow extends PPodDomainObject {
	private String sequence;

	PPodMolecularRow() {}

	public PPodMolecularRow(final Long version, final String sequence) {
		super(version);
		checkNotNull(sequence);
		setSequence(sequence);
	}

	public PPodMolecularRow(final String sequence) {
		checkNotNull(sequence);
		this.sequence = sequence;
	}

	abstract protected Set<Character> getLegalChars();

	public final String getSequence() {
		return sequence;
	}

	public final void setSequence(final String sequence) {
		checkNotNull(sequence);
		for (int i = 0; i < sequence.length(); i++) {
			checkArgument(getLegalChars().contains(sequence.charAt(i)),
					"position " + i + " is " + sequence.charAt(i)
							+ " which is illegal");
		}
		this.sequence = sequence;
	}
}
