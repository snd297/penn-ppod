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

	/** For JAXB. */
	PPodOtu() {}

	public PPodOtu(final String label) {
		super(label);
	}

	public PPodOtu(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	public PPodOtu(@CheckForNull final String pPodId, final String label,
			final String docId) {
		super(pPodId, label);
		this.docId = checkNotNull(docId);
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(final String docId) {
		this.docId = checkNotNull(docId);
	}
}
