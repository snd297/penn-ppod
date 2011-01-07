package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class StandardRow {

	@CheckForNull
	private final Long version;

	final List<StandardState> states = newArrayList();

	public StandardRow(final Long version) {
		this.version = version;
	}

	public List<StandardState> getStates() {
		return states;
	}

	/**
	 * @return the version
	 */
	@Nullable
	public Long getVersion() {
		return version;
	}
}
