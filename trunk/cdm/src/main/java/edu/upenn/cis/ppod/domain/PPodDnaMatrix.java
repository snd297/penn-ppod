package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

final public class PPodDnaMatrix extends UuPPodDomainObjectWDocId {

	private final String label;
	private final List<PPodDnaRow> rows = newArrayList();

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

}
