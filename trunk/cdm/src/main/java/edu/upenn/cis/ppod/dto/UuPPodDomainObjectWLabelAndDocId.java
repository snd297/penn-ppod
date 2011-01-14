package edu.upenn.cis.ppod.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlID;

import edu.umd.cs.findbugs.annotations.CheckForNull;

abstract class UuPPodDomainObjectWLabelAndDocId extends
		UuPPodDomainObjectWithLabel
		implements IHasDocId {

	protected UuPPodDomainObjectWLabelAndDocId() {}

	protected UuPPodDomainObjectWLabelAndDocId(final String label) {
		super(label);
	}

	protected UuPPodDomainObjectWLabelAndDocId(
			@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	protected UuPPodDomainObjectWLabelAndDocId(final String pPodId,
			final Long version, final String label) {
		super(pPodId, version, label);
	}

	

}
