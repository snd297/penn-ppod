package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

public final class PPodStandardCell extends PPodDomainObject {

	private final PPodCellType type;
	private final Set<Integer> states = newHashSet();

	public PPodStandardCell(final Long version, final PPodCellType type) {
		super(version);

		checkNotNull(type);
		this.type = type;
	}

	public PPodStandardCell(final PPodCellType type) {
		this.type = type;
	}

	/**
	 * @return the states
	 */
	public Set<Integer> getStates() {
		return states;
	}

	/**
	 * @return the type
	 */
	public PPodCellType getType() {
		return type;
	}

}
