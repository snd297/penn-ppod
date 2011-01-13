package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

abstract class UuPPodDomainObjectWithLabel extends PPodDomainObject
		implements IHasPPodId {

	@XmlAttribute
	private String label;

	@XmlAttribute(name = "pPodId")
	@CheckForNull
	private final String pPodId;

	protected UuPPodDomainObjectWithLabel() {
		pPodId = null;
	}

	protected UuPPodDomainObjectWithLabel(final String label) {
		checkNotNull(label);
		this.pPodId = null;
		this.label = label;
	}

	protected UuPPodDomainObjectWithLabel(
			final String pPodId,
			final Long version,
			final String label) {
		super(version);
		checkNotNull(pPodId);
		checkNotNull(label);
		this.pPodId = pPodId;
		this.label = label;
	}

	protected UuPPodDomainObjectWithLabel(@CheckForNull final String pPodId,
			final String label) {
		checkNotNull(label);
		this.pPodId = pPodId;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	@Nullable
	public String getPPodId() {
		return pPodId;
	}

	public void setLabel(final String label) {
		checkNotNull(label);
		this.label = label;
	}

}
