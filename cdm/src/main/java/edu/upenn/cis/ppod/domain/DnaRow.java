package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DnaRow {

	@CheckForNull
	private final Long version;

	private final List<DnaNucleotide> nucleotides = newArrayList();

	public DnaRow(final long version) {
		this.version = version;
	}

	public List<DnaNucleotide> getNucleotides() {
		return nucleotides;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}

}
