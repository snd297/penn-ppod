package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodStandardMatrix extends UuPPodDomainObject {

	private String label;
	private List<PPodStandardCharacter> characters = newArrayList();
	private final List<PPodStandardRow> rows = newArrayList();
	private List<Long> columnVersions = newArrayList();

	PPodStandardMatrix() {}

	public PPodStandardMatrix(final String pPodId,
			final Long version,
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

	public List<Long> getColumnVersions() {
		return columnVersions;
	}

	public String getLabel() {
		return label;
	}

	public List<PPodStandardRow> getRows() {
		return rows;
	}

	public void setCharacters(final List<PPodStandardCharacter> characters) {
		checkNotNull(characters);
		this.characters = characters;
	}

	public void setColumnVersions(final List<Long> columnVersions) {
		checkNotNull(columnVersions);
		this.columnVersions = columnVersions;
	}

	public void setLabel(final String label) {
		this.label = label;
	}
}
