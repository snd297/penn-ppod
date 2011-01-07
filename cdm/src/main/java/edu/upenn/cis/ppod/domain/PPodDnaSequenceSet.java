package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

final public class PPodDnaSequenceSet extends UuPPodDomainObject {

	private final String label;
	private final List<PPodDnaSequence> sequences = newArrayList();

	public PPodDnaSequenceSet(final String pPodId, final Long version,
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
