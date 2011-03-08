package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@XmlAccessorType(XmlAccessType.NONE)
abstract class UuPPodDomainObjectWithLabel
		implements IHasPPodId {

	@XmlAttribute
	private String label;

	@XmlAttribute(name = "pPodId")
	// default naming makes it weird
	@CheckForNull
	private String pPodId;

	UuPPodDomainObjectWithLabel() {
		pPodId = null;
	}

	UuPPodDomainObjectWithLabel(final String label) {
		this.label = checkNotNull(label);
		this.pPodId = null;
	}

	UuPPodDomainObjectWithLabel(
			@CheckForNull final String pPodId,
			final String label) {
		this.label = checkNotNull(label);
		this.pPodId = pPodId;
	}

	public final String getLabel() {
		return label;
	}

	@Nullable
	public final String getPPodId() {
		return pPodId;
	}

	public final void setLabel(final String label) {
		this.label = checkNotNull(label);
	}

	public final void setPPodId(@CheckForNull final String pPodId) {
		this.pPodId = pPodId;
	}

}
