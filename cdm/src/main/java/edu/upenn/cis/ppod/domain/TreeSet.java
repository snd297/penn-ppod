package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class TreeSet {
	@CheckForNull
	private final Long version;
	private final String label;
	private final List<Tree> trees = newArrayList();

	public TreeSet(final Long version, final String label) {
		this.version = version;
		this.label = label;
	}

	public TreeSet(final String label) {
		this.version = null;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public List<Tree> getTrees() {
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
