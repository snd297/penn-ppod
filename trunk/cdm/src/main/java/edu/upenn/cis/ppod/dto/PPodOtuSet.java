package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodOtuSet extends UuPPodDomainObject {

	private String label;

	private List<PPodOtu> otus = newArrayList();
	private List<PPodDnaMatrix> dnaMatrices = newArrayList();
	private List<PPodStandardMatrix> standardMatrices = newArrayList();
	private List<PPodDnaSequenceSet> dnaSequenceSets = newArrayList();
	private List<PPodTreeSet> treeSets = newArrayList();

	PPodOtuSet() {}

	public PPodOtuSet(final String label) {
		checkNotNull(label);
		this.label = label;
	}

	public PPodOtuSet(final String pPodId, final Long version,
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
		checkNotNull(label);
		this.label = label;
	}

	public void setOtus(final List<PPodOtu> otus) {
		checkNotNull(otus);
		this.otus = otus;
	}

}
