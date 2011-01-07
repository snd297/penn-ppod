package edu.upenn.cis.ppod.domain;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodTree {
	@CheckForNull
	private final Long version;
	private final String label;
	private final String newick;

	public PPodTree(final String label, final String newick) {
		this.version = null;
		this.label = label;
		this.newick = newick;
	}

	public PPodTree(final Long version, final String label, final String newick) {
		this.version = version;
		this.label = label;
		this.newick = newick;
	}

	public String getLabel() {
		return label;
	}

	public String getNewick() {
		return newick;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}
}
