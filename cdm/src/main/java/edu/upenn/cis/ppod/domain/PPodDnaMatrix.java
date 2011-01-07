package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PPodDnaMatrix extends UuPPodDomainObject {

	private final List<PPodDnaRow> rows = newArrayList();

	public PPodDnaMatrix() {}

	public PPodDnaMatrix(final String pPodId, final Long version) {
		super(pPodId, version);
	}

	public List<PPodDnaRow> getRows() {
		return rows;
	}

}
