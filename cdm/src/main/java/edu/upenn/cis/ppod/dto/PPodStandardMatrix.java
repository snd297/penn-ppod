package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkArgument;
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
			final long version,
			final String label) {
		super(pPodId, version, label);
	}

	public PPodStandardMatrix(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
	}

	public void addColumn(final int columnNumber,
			final long columnVersionNumber,
			final PPodStandardCharacter character,
			final List<PPodStandardCell> column) {
		checkArgument(columnNumber >= 0);
		checkNotNull(character);
		checkNotNull(column);
		this.columnVersions.add(columnNumber, columnVersionNumber);
		this.characters.add(columnNumber, character);

		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).getCells().add(columnNumber, column.get(i));
		}
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

	public List<PPodStandardCell> removeColumn(final int columnNumber) {
		checkArgument(columnNumber >= 0);
		this.columnVersions.remove(columnNumber);
		this.characters.remove(columnNumber);
		final List<PPodStandardCell> removedColumn = newArrayList();
		for (final PPodStandardRow row : rows) {
			removedColumn.add(row.getCells().remove(columnNumber));
		}
		return removedColumn;
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
