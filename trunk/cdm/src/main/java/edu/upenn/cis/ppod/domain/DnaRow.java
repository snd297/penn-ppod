package edu.upenn.cis.ppod.domain;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DnaRow {

	@CheckForNull
	private final Long version;

	private final String sequence;

	public DnaRow(final long version, final String sequence) {
		this.version = version;
		this.sequence = sequence;
	}

	public String getSequence() {
		return sequence;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}

}
