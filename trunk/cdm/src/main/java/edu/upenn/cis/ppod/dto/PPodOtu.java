package edu.upenn.cis.ppod.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class PPodOtu extends UuPPodDomainObjectWithLabel {

	@XmlID
	@XmlAttribute
	@CheckForNull
	private String docId = UUID.randomUUID().toString();

	PPodOtu() {}

	public PPodOtu(final String label) {
		super(label);
	}

	public PPodOtu(final String pPodId, final Long version, final String label) {
		super(pPodId, version, label);
	}

	public PPodOtu(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	@Nullable
	public String getDocId() {
		return docId;
	}

	public void setDocId(@CheckForNull final String docId) {
		this.docId = docId;
	}

}
