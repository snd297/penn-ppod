package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodOtuSet {

	@CheckForNull
	private final Long version;
	private final String label;
	private final List<PPodOtu> otus = newArrayList();
	private final List<PPodDnaMatrix> dnaMatrices = newArrayList();
	private final List<PPodStandardMatrix> standardMatrices = newArrayList();
	private final List<PPodDnaSequenceSet> dnaSequenceSets = newArrayList();
	private final List<PPodTreeSet> treeSets = newArrayList();

	public PPodOtuSet(final Long version, final String label) {
		checkNotNull(version);
		checkNotNull(label);
		this.version = version;
		this.label = label;
	}

	public PPodOtuSet(final String label) {
		checkNotNull(label);
		this.version = null;
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

	/**
	 * @return the version
	 */
	@Nullable
	public Long getVersion() {
		return version;
	}

}
