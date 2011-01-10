package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

final public class PPodDnaRow extends PPodDomainObject {

	private String sequence;
	private final List<Long> cellVersions = newArrayList();

	PPodDnaRow() {}

	public PPodDnaRow(final Long version, final String sequence) {
		super(version);
		checkNotNull(sequence);
		this.sequence = sequence;
	}

	public PPodDnaRow(final String sequence) {
		checkNotNull(sequence);
		this.sequence = sequence;
	}

	public String getSequence() {
		return sequence;
	}

	public List<Long> getCellVersions() {
		return cellVersions;
	}
}
