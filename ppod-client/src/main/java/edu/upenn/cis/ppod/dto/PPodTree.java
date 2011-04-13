package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodTree extends UuPPodDomainObjectWithLabel {

	@XmlElement
	private String newick;

	/** For JAXB. */
	@SuppressWarnings("unused")
	private PPodTree() {}

	public PPodTree(@CheckForNull final String pPodId, final String label,
			final String newick) {
		super(pPodId, label);
		this.newick = checkNotNull(newick);
	}

	public String getNewick() {
		return newick;
	}

	public void setNewick(final String newick) {
		this.newick = checkNotNull(newick);
	}

}
