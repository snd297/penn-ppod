package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

public final class PPodStandardCell extends PPodDomainObject {

	private PPodCellType type;
	private Set<Integer> states = newHashSet();

	public PPodStandardCell() {}

	public PPodStandardCell(final Long version, final PPodCellType type) {
		super(version);

		checkNotNull(type);
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

	public void setInapplicable() {
		type = PPodCellType.INAPPLICABLE;
		states.clear();
	}

	public void setPolymorphic(final Set<Integer> states) {
		checkNotNull(states);
		checkArgument(states.size() > 1);
		type = PPodCellType.POLYMORPHIC;
		this.states = states;
	}

	public void setSingle(final Integer state) {
		checkNotNull(state);
		type = PPodCellType.SINGLE;
		this.states.clear();
		this.states.add(state);
	}

	public void setStates(final Set<Integer> states) {
		checkNotNull(states);
		this.states = states;
	}

	public void setUnassigned() {
		type = PPodCellType.UNASSIGNED;
		states.clear();
	}

	public void setUncertain(final Set<Integer> states) {
		checkNotNull(states);
		checkArgument(states.size() > 1);
		type = PPodCellType.UNCERTAIN;
		this.states = states;
	}

}
