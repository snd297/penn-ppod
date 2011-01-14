package edu.upenn.cis.ppod.dto;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

final public class PPodDnaSequenceSet extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "sequence")
	private final List<PPodDnaSequence> sequences = newArrayList();

	PPodDnaSequenceSet() {}

	public PPodDnaSequenceSet(final String label) {
		super(label);
	}

	public PPodDnaSequenceSet(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
	}

	public PPodDnaSequenceSet(final String pPodId,
			final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	public List<PPodDnaSequence> getSequences() {
		return sequences;
	}

}
