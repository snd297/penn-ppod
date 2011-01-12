package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;

final public class PPodDnaMatrix extends UuPPodDomainObject {

	PPodDnaMatrix() {}

	private String label;
	private final List<PPodDnaRow> rows = newArrayList();
	private final List<Long> columnVersions = newArrayList();

	public PPodDnaMatrix(@CheckForNull final String pPodId, final String label) {
		super(pPodId);
		checkNotNull(label);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public List<PPodDnaRow> getRows() {
		return rows;
	}

	public PPodDnaMatrix(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version);
		checkNotNull(label);
		this.label = label;

	}

	public List<Long> getColumnVersions() {
		return columnVersions;
	}

}
