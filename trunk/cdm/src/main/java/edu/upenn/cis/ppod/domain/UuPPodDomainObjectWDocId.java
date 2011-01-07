package edu.upenn.cis.ppod.domain;

import java.util.UUID;

import javax.xml.bind.annotation.XmlID;

public class UuPPodDomainObjectWDocId extends UuPPodDomainObject {
	@XmlID
	private final String docId = UUID.randomUUID().toString();

	public UuPPodDomainObjectWDocId() {

	}

	public UuPPodDomainObjectWDocId(final String pPodId, final Long version) {
		super(pPodId, version);
	}

	public String getDocId() {
		return docId;
	}

}
