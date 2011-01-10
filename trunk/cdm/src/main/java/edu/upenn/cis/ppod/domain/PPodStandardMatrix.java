package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodStandardMatrix extends UuPPodDomainObjectWDocId {

	private String label;
	private final List<PPodStandardCharacter> characters = newArrayList();
	private final List<PPodStandardRow> rows = newArrayList();
	private final List<Long> columnVersions = newArrayList();

	PPodStandardMatrix() {}

	public PPodStandardMatrix(@CheckForNull final String pPodId,
			@CheckForNull final Long version,
			final String label) {
		super(pPodId, version);
		checkNotNull(label);
		this.label = label;
	}

	public PPodStandardMatrix(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId);
		checkNotNull(label);
		this.label = label;
	}

	public List<PPodStandardCharacter> getCharacters() {
		return characters;
	}

	public String getLabel() {
		return label;
	}

	public List<PPodStandardRow> getRows() {
		return rows;
	}

	public List<Long> getColumnVersions() {
		return columnVersions;
	}
}
