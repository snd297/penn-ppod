package edu.upenn.cis.ppod.domain;

final public class PPodDnaRow extends PPodDomainObject {

	private final String sequence;

	public PPodDnaRow(final Long version, final String sequence) {
		super(version);
		this.sequence = sequence;
	}

	public String getSequence() {
		return sequence;
	}
}
