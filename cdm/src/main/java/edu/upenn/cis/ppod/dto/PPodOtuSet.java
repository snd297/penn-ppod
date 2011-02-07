package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodOtuSet extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "otu")
	private List<PPodOtu> otus = newArrayList();

	@XmlElement(name = "standardMatrix")
	private List<PPodStandardMatrix> standardMatrices = newArrayList();

	@XmlElement(name = "dnaMatrix")
	private List<PPodDnaMatrix> dnaMatrices = newArrayList();

	@XmlElement(name = "proteinMatrix")
	private List<PPodProteinMatrix> proteinMatrices = newArrayList();

	@XmlElement(name = "dnaSequenceSet")
	private List<PPodDnaSequenceSet> dnaSequenceSets = newArrayList();

	@XmlElement(name = "treeSet")
	private List<PPodTreeSet> treeSets = newArrayList();

	PPodOtuSet() {}

	public PPodOtuSet(final String label) {
		super(label);
	}

	public PPodOtuSet(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	public PPodOtuSet(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
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

	public List<PPodOtu> getOtus() {
		return otus;
	}

	public List<PPodProteinMatrix> getProteinMatrices() {
		return proteinMatrices;
	}

	public List<PPodStandardMatrix> getStandardMatrices() {
		return standardMatrices;
	}

	public List<PPodTreeSet> getTreeSets() {
		return treeSets;
	}

	public void setDnaMatrices(final List<PPodDnaMatrix> dnaMatrices) {
		checkNotNull(dnaMatrices);
		this.dnaMatrices = dnaMatrices;
	}

	public void setDnaSequenceSets(
			final List<PPodDnaSequenceSet> dnaSequenceSets) {
		checkNotNull(dnaSequenceSets);
		this.dnaSequenceSets = dnaSequenceSets;
	}

	public void setOtus(final List<PPodOtu> otus) {
		checkNotNull(otus);
		this.otus = otus;
	}

	public void setProteinMatrices(final List<PPodProteinMatrix> proteinMatrices) {
		checkNotNull(proteinMatrices);
		this.proteinMatrices = proteinMatrices;
	}

	public void setStandardMatrices(
			final List<PPodStandardMatrix> standardMatrices) {
		checkNotNull(standardMatrices);
		this.standardMatrices = standardMatrices;
	}

	public void setTreeSets(final List<PPodTreeSet> treeSets) {
		checkNotNull(treeSets);
		this.treeSets = treeSets;
	}
}
