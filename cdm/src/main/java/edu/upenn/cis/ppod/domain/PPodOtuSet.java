package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodOtuSet extends UuPPodDomainObjectWDocId
		implements IHasPPodId {

	PPodOtuSet() {}

	private String label;
	private final List<PPodOtu> otus = newArrayList();
	private final List<PPodDnaMatrix> dnaMatrices = newArrayList();
	private final List<PPodStandardMatrix> standardMatrices = newArrayList();
	private final List<PPodDnaSequenceSet> dnaSequenceSets = newArrayList();
	private final List<PPodTreeSet> treeSets = newArrayList();

	public PPodOtuSet(final String label) {
		checkNotNull(label);
		this.label = label;
	}

	public PPodOtuSet(@CheckForNull final String pPodId, final Long version,
			final String label) {
		super(pPodId, version);

		checkNotNull(label);
		this.label = label;
	}

	public PPodOtuSet(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId);
		this.label = label;
	}

	public List<PPodDnaMatrix> getDnaMatrices() {
		return dnaMatrices;
	}

	/**
	 * @return the dnaSequenceSets
	 */
	public List<PPodDnaSequenceSet> getDnaSequenceSets() {
		return dnaSequenceSets;
	}

	public String getLabel() {
		return label;
	}

	public List<PPodOtu> getOtus() {
		return otus;
	}

	public List<PPodStandardMatrix> getStandardMatrices() {
		return standardMatrices;
	}

	public List<PPodTreeSet> getTreeSets() {
		return treeSets;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setOtus(final List<? extends PPodOtu> otus) {
		this.otus.clear();
		this.otus.addAll(otus);
	}

}
