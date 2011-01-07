package edu.upenn.cis.ppod.domain;

final public class PPodDnaSequence extends PPodDomainObject {

	private final String sequence;
	private final String name;

	public PPodDnaSequence(final Long version, final String sequence,
			final String name) {
		super(version);
		this.sequence = sequence;
		this.name = name;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	public String getName() {
		return name;
	}

}
