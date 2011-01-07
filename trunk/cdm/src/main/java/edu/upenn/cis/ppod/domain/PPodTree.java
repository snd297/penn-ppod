package edu.upenn.cis.ppod.domain;

public final class PPodTree extends UuPPodDomainObject {

	private final String label;
	private final String newick;

	public PPodTree(final String pPodId, final Long version,
			final String label, final String newick) {
		super(pPodId, version);
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
