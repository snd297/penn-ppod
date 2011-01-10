package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

public final class PPodStandardCell extends PPodDomainObject {

	private PPodCellType type;
	private final Set<Integer> states = newHashSet();

	public PPodStandardCell(final Long version, final PPodCellType type) {
		super(version);

		checkNotNull(type);
		this.type = type;
	}

	public PPodStandardCell() {

	}

	public void setInapplicable() {
		type = PPodCellType.INAPPLICABLE;
		states.clear();
	}

	public void setUnassigned() {
		type = PPodCellType.UNASSIGNED;
		states.clear();
	}

	public void setSingle(final Integer state) {
		checkNotNull(state);
		type = PPodCellType.SINGLE;
		states.clear();
		states.add(state);
	}

	public void setPolymorphic(final Set<Integer> states) {
		checkNotNull(states);
		checkArgument(states.size() > 1);
		type = PPodCellType.POLYMORPHIC;
		states.clear();
		states.addAll(states);
	}

	public void setUncertain(final Set<Integer> states) {
		checkNotNull(states);
		checkArgument(states.size() > 1);
		type = PPodCellType.POLYMORPHIC;
		states.clear();
		states.addAll(states);
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
