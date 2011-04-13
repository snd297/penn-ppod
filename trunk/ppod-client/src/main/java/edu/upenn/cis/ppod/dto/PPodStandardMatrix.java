package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

@XmlAccessorType(XmlAccessType.PROPERTY)
public final class PPodStandardMatrix extends PPodMatrix<PPodStandardRow> {

	private List<PPodStandardCharacter> characters = newArrayList();

	/** For JAXB. */
	@SuppressWarnings("unused")
	private PPodStandardMatrix() {}

	public PPodStandardMatrix(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
	}

	public void addColumn(final int columnNumber,
			final PPodStandardCharacter character,
			final List<PPodStandardCell> column) {
		checkArgument(columnNumber >= 0);
		checkNotNull(character);
		checkNotNull(column);
		this.characters.add(columnNumber, character);

		for (int i = 0; i < getRows().size(); i++) {
			getRows().get(i).getCells().add(columnNumber, column.get(i));
		}
	}

	@XmlElement(name = "character")
	public List<PPodStandardCharacter> getCharacters() {
		return characters;
	}

	@XmlElement(name = "row")
	@Override
	public final List<PPodStandardRow> getRows() {
		return super.getRows();
	}

	public List<PPodStandardCell> removeColumn(final int columnNumber) {
		checkArgument(columnNumber >= 0);
		this.characters.remove(columnNumber);
		final List<PPodStandardCell> removedColumn = newArrayList();
		for (final PPodStandardRow row : getRows()) {
			removedColumn.add(row.getCells().remove(columnNumber));
		}
		return removedColumn;
	}

	public void setCharacters(final List<PPodStandardCharacter> characters) {
		checkNotNull(characters);
		this.characters = characters;
	}

}
