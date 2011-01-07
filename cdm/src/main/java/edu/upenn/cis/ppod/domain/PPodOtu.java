package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PPodOtu extends UuPPodDomainObjectWDocId {

	private final String label;

	public PPodOtu(final String label) {
		this.label = label;
	}

	public PPodOtu(final String pPodId, final Long version, final String label) {
		super(pPodId, version);
		checkNotNull(label);
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

}
