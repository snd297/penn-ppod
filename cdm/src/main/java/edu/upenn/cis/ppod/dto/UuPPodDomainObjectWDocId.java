package edu.upenn.cis.ppod.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlID;

import edu.umd.cs.findbugs.annotations.CheckForNull;

class UuPPodDomainObjectWDocId extends UuPPodDomainObject implements IHasDocId {
	@XmlID
	private final String docId = UUID.randomUUID().toString();

	protected UuPPodDomainObjectWDocId() {}

	protected UuPPodDomainObjectWDocId(@CheckForNull final String pPodId) {
		super(pPodId);
	}

	protected UuPPodDomainObjectWDocId(final String pPodId,
			final Long version) {
		super(pPodId, version);
	}

	public String getDocId() {
		return docId;
	}

}
