package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PPodStandardRow extends PPodDomainObject {

	final List<PPodStandardCell> cells = newArrayList();

	public PPodStandardRow() {}

	public PPodStandardRow(final Long version) {
		super(version);
	}

	public List<PPodStandardCell> getCells() {
		return cells;
	}

}
