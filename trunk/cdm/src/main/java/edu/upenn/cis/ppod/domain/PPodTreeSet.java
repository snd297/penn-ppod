package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public final class PPodTreeSet extends UuPPodDomainObjectWDocId {

	private final String label;
	private final List<PPodTree> trees = newArrayList();

	public PPodTreeSet(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version);
		this.label = label;
	}

	public PPodTreeSet(final String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public List<PPodTree> getTrees() {
		return trees;
	}
}
