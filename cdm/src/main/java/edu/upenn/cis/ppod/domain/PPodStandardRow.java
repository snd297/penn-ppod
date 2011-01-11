package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public final class PPodStandardRow extends PPodDomainObject implements IPPodRow {

	private List<PPodStandardCell> cells = newArrayList();

	public PPodStandardRow() {}

	public PPodStandardRow(final Long version) {
		super(version);
	}

	public List<PPodStandardCell> getCells() {
		return cells;
	}

	public void setCells(final List<PPodStandardCell> cells) {
		checkNotNull(cells);
		this.cells = cells;
	}

}
