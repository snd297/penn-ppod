package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodOtu extends UuPPodDomainObjectWithLabel {

	@XmlID
	@XmlAttribute
	private String docId = UUID.randomUUID().toString();

	PPodOtu() {}

	public PPodOtu(final String label) {
		super(label);
	}

	public PPodOtu(final String pPodId, final Long version, final String docId,
			final String label) {
		this(pPodId, version, label);
		checkNotNull(docId);
		this.docId = docId;
	}

	public PPodOtu(final String pPodId, final Long version, final String label) {
		super(pPodId, version, label);
	}

	public PPodOtu(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(final String docId) {
		checkNotNull(docId);
		this.docId = docId;
	}

}
