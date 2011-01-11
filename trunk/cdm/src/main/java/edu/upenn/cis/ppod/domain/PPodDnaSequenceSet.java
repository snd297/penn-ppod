package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import edu.umd.cs.findbugs.annotations.CheckForNull;

@XmlAccessorType(XmlAccessType.FIELD)
final public class PPodDnaSequenceSet extends UuPPodDomainObject {

	private String label;
	private final List<PPodDnaSequence> sequences = newArrayList();

	PPodDnaSequenceSet() {}

	public PPodDnaSequenceSet(final String label) {
		checkNotNull(label);
		this.label = label;
	}

	public PPodDnaSequenceSet(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId);
		checkNotNull(label);
		this.label = label;
	}

	public PPodDnaSequenceSet(final String pPodId,
			final Long version,
			final String label) {
		super(pPodId, version);
		checkNotNull(label);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public List<PPodDnaSequence> getSequences() {
		return sequences;
	}

}
