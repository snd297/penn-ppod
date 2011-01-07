package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class OtuSet {

	@CheckForNull
	private final Long version;
	private final String label;
	private final List<Otu> otus = newArrayList();
	private final List<DnaMatrix> dnaMatrices = newArrayList();
	private final List<StandardMatrix> standardMatrices = newArrayList();
	private final List<TreeSet> treeSets = newArrayList();

	public OtuSet(final Long version, final String label) {
		checkNotNull(version);
		checkNotNull(label);
		this.version = version;
		this.label = label;
	}

	public OtuSet(final String label) {
		checkNotNull(label);
		this.version = null;
		this.label = label;
	}

	public List<DnaMatrix> getDnaMatrices() {
		return dnaMatrices;
	}

	public String getLabel() {
		return label;
	}

	public List<Otu> getOtus() {
		return otus;
	}

	public List<StandardMatrix> getStandardMatrices() {
		return standardMatrices;
	}

	public List<TreeSet> getTreeSets() {
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
