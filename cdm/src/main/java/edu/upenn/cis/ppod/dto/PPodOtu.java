package edu.upenn.cis.ppod.dto;

public final class PPodOtu extends UuPPodDomainObjectWLabelAndDocId {

	PPodOtu() {}

	public PPodOtu(final String label) {
		super(label);
	}

	public PPodOtu(final String pPodId, final String label) {
		super(pPodId, label);
	}

	public PPodOtu(final String pPodId, final Long version, final String label) {
		super(pPodId, version, label);
	}

}
