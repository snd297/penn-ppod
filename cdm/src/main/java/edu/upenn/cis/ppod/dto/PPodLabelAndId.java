package edu.upenn.cis.ppod.dto;

import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class PPodLabelAndId {

	public static final Comparator<PPodLabelAndId> LABEL_COMPARATOR =
			new java.util.Comparator<PPodLabelAndId>() {

				public int compare(final PPodLabelAndId o1,
						final PPodLabelAndId o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			};

	@XmlAttribute(name = "pPodId")
	private String pPodId;

	@XmlAttribute
	private String label;

	@SuppressWarnings("unused")
	private PPodLabelAndId() {}

	public PPodLabelAndId(final String pPodId, final String label) {
		this.pPodId = pPodId;
		this.label = label;
	}

	public String getPPodId() {
		return pPodId;
	}

	public String getLabel() {
		return label;
	}
}
