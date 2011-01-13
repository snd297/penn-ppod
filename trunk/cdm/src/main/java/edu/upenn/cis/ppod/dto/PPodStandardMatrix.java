package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodStandardMatrix extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "character")
	private List<PPodStandardCharacter> characters = newArrayList();

	@XmlElement(name = "row")
	private final List<PPodStandardRow> rows = newArrayList();

	@XmlElement(name = "columnVersion")
	private List<Long> columnVersions = newArrayList();

	PPodStandardMatrix() {}

	public PPodStandardMatrix(final String pPodId,
			final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	public PPodStandardMatrix(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
	}

	public List<PPodStandardCharacter> getCharacters() {
		return characters;
	}

	public List<Long> getColumnVersions() {
		return columnVersions;
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

}
