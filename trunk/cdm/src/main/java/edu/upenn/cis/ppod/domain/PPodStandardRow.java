package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodStandardRow {

	@CheckForNull
	private final Long version;

	final List<PPodStandardState> states = newArrayList();

	public PPodStandardRow(final Long version) {
		this.version = version;
	}

	public List<PPodStandardState> getStates() {
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
