package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodTreeSet {
	@CheckForNull
	private final Long version;
	private final String label;
	private final List<PPodTree> trees = newArrayList();

	public PPodTreeSet(final Long version, final String label) {
		this.version = version;
		this.label = label;
	}

	public PPodTreeSet(final String label) {
		this.version = null;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public List<PPodTree> getTrees() {
		return trees;
	}

	/**
	 * @return the version
	 */
	@Nullable
	public Long getVersion() {
		return version;
	}

}
