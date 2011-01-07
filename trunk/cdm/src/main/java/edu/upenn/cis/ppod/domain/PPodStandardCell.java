package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodStandardCell {
	@CheckForNull
	private final Long version;
	private final PPodCellType type;
	private final List<Integer> states = newArrayList();

	public PPodStandardCell(final PPodCellType type) {
		version = null;
		this.type = type;
	}

	public PPodStandardCell(final Long version, final PPodCellType type) {
		this.version = version;
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

	/**
	 * @return the version
	 */
	@Nullable
	public Long getVersion() {
		return version;
	}

}
