package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;

public class PPodDnaRow extends PPodDomainObject {

	private final String sequence;

	public PPodDnaRow(final Long version, final String sequence) {
		super(version);
		checkNotNull(sequence);
		this.sequence = sequence;
	}

	public String getSequence() {
		return sequence;
	}

}
