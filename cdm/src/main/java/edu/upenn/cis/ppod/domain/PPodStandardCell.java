package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PPodStandardCell extends PPodDomainObject {

	private final PPodCellType type;
	private final List<Integer> states = newArrayList();

	public PPodStandardCell(final PPodCellType type) {
		this.type = type;
	}

	public PPodStandardCell(final Long version, final PPodCellType type) {
		super(version);

		checkNotNull(type);
		this.type = type;
	}

	/**
	 * @return the states
	 */
	public List<Integer> getStates() {
		return states;
	}

	/**
	 * @return the type
	 */
	public PPodCellType getType() {
		return type;
	}

}
