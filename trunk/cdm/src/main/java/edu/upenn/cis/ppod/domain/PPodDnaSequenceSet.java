package edu.upenn.cis.ppod.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class PPodDnaSequenceSet {

	@CheckForNull
	final Long version;

	private final List<PPodDnaSequence> sequences = newArrayList();

	public PPodDnaSequenceSet(final Long version) {
		this.version = version;
	}

	public List<PPodDnaSequence> getSequences() {
		return sequences;
	}

	@Nullable
	public Long getVersion() {
		return version;
	}

}
