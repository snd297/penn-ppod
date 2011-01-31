package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodTree extends UuPPodDomainObjectWithLabel {

	private String newick;

	PPodTree() {}

	public PPodTree(final String pPodId,
			final Long version,
			final String label, final String newick) {
		super(pPodId, version, label);
		checkNotNull(label);
		checkNotNull(newick);
		this.newick = newick;
	}

	public PPodTree(@CheckForNull final String pPodId, final String label,
			final String newick) {
		super(pPodId, label);
		this.newick = newick;
	}

	public String getNewick() {
		return newick;
	}

	public void setNewick(final String newick) {
		this.newick = newick;
	}

}
