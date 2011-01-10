package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodTree extends UuPPodDomainObject {

	PPodTree() {}

	private String label;
	private String newick;

	public PPodTree(final String pPodId,
			final Long version,
			final String label, final String newick) {
		super(pPodId, version);
		checkNotNull(label);
		checkNotNull(newick);
		this.label = label;
		this.newick = newick;
	}

	public PPodTree(@CheckForNull final String pPodId, final String label,
			final String newick) {
		super(pPodId);
		this.label = label;
		this.newick = newick;
	}

	public PPodTree(final String label, final String newick) {
		this.label = label;
		this.newick = newick;
	}

	public String getLabel() {
		return label;
	}

	public String getNewick() {
		return newick;
	}

}
