package edu.upenn.cis.ppod.domain;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DnaSequence {
	@CheckForNull
	private final Long version;

	private final String sequence;

	public DnaSequence(final Long version, final String sequence) {
		this.version = version;
		this.sequence = sequence;
	}

	public DnaSequence(final String sequence) {
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
