package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class StandardCell {
	@CheckForNull
	private final Long version;
	private final CellType type;
	private final List<Integer> states = newArrayList();

	public StandardCell(final CellType type) {
		version = null;
		this.type = type;
	}

	public StandardCell(final Long version, final CellType type) {
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
	public CellType getType() {
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
