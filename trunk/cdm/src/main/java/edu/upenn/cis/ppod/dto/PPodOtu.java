package edu.upenn.cis.ppod.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodOtu extends UuPPodDomainObjectWithLabel {

	@XmlID
	@XmlAttribute
	private final String docId = UUID.randomUUID().toString();

	PPodOtu() {}

	public PPodOtu(final String label) {
		super(label);
	}

	public PPodOtu(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	public PPodOtu(final String pPodId, final Long version, final String label) {
		super(pPodId, version, label);
	}

	public String getDocId() {
		return docId;
	}

}
