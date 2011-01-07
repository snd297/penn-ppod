package edu.upenn.cis.ppod.domain;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodDnaSequence {
	@CheckForNull
	private final Long version;

	private final String sequence;

	public PPodDnaSequence(final Long version, final String sequence) {
		this.version = version;
		this.sequence = sequence;
	}

	public PPodDnaSequence(final String sequence) {
		this.version = null;
		this.sequence = sequence;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}

}
