/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Rows of a {@link CharacterStateMatrix}.
 * <p>
 * In Hibernate, composite elements may contain components but not collections -
 * that's why this is an entity and not a component class: it needs to contain a
 * {@code List<CharacterStateCell>}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = CharacterStateRow.TABLE)
public final class CharacterStateRow extends PPodEntity {

	/** This entitiy's table. Intentionally package-private. */
	static final String TABLE = "CHARACTER_STATE_ROW";

	static final String CELLS_INDEX_COLUMN = CharacterStateCell.TABLE
			+ "_POSITION";

	/**
	 * The column where a {@code CharacterStateRow}'s
	 * {@link javax.persistence.Id} gets stored. Intentionally package-private.
	 */
	static final String ID_COLUMN = TABLE + "_ID";

	/** The {@code CharacterStateCell}s that make up the row. */

	@XmlElement(name = "cell")
	@OneToMany
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = CharacterStateCell.ID_COLUMN) })
	@IndexColumn(name = CELLS_INDEX_COLUMN)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private final List<CharacterStateCell> cells = newArrayList();

	/** {@code CharacterStateCell}-><code>cells</code>Index lookup. */
	@org.hibernate.annotations.CollectionOfElements
	@JoinTable(name = "ROW_CELL_IDX", joinColumns = @JoinColumn(name = ID_COLUMN))
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = CharacterStateCell.ID_COLUMN))
	@Column(name = "CELL_IDX")
	private final Map<CharacterStateCell, Integer> cellIdx = newHashMap();

	/**
	 * Get an unmodifiable view of the cell to row position lookup map.
	 * 
	 * @return get an unmodifiable view of the cell to row position lookup map
	 */
	public Map<CharacterStateCell, Integer> getCellIdx() {
		return Collections.unmodifiableMap(cellIdx);
	}

	/**
	 * The {@code CharacterStateMatrix} to which this {@code CharacterStateRow}
	 * belongs.
	 */
// @ManyToOne
// @JoinColumn(name = CharacterStateMatrix.ID_COLUMN, insertable = false,
	// updatable = false, nullable = false)
// private CharacterStateMatrix matrix;

	CharacterStateRow() {}

	/**
	 * Add {@code cell} to the end of this row.
	 * 
	 * @param cell to be added
	 * 
	 * @return {@code cell}
	 * 
	 * @throws IllegalStateException if this row hasn't been added to a matrix
	 *             yet
	 * @throws IllegalStateException if a character has not been set for the
	 *             column we're trying to add the cell to
	 */
	public CharacterStateCell addCell(final CharacterStateCell cell) {
		checkNotNull(cell);
// if (getMatrix() == null) {
// throw new IllegalStateException(
// "This row hasn't been added to a matrix yet");
// }
// if (getMatrix().getCharacters().size() < getCells().size() + 1) {
// throw new IllegalStateException("the matrix has less characters "
// + getMatrix().getCharacterIdx().size()
// + " than the row is about to have "
// + (getCells().size() + 1));
// }
// if (getMatrix().getCharacters().size() > 0
// && getMatrix().getCharacters().get(getCells().size()) == null) {
// throw new IllegalStateException("Character is null at column "
// + cells.size());
// }
		cells.add(cell);
		cellIdx.put(cell, cells.size() - 1);
		// cell.setRow(this);
		resetPPodVersionInfo();
		return cell;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		// setMatrix((CharacterStateMatrix) parent);
		int i = 0;
		for (final CharacterStateCell cell : getCells()) {
			cellIdx.put(cell, i++);
		}
	}

	/**
	 * Empty out and return this row's cells. After calling this,
	 * {@link #getCells()}{@code .size()} will be {@code 0}.
	 * 
	 * @return the cleared cells
	 */
	public List<CharacterStateCell> clearCells() {
		final List<CharacterStateCell> clearedCells = newArrayList(cells);
		cells.clear();
		return clearedCells;
	}

	/**
	 * Get an unmodifiable view of the row.
	 * 
	 * @return an unmodifiable view of the row
	 */
	public List<CharacterStateCell> getCells() {
		return Collections.unmodifiableList(cells);
	}

	/**
	 * Getter.
	 * 
	 * @return the {@code CharacterStateMatrix} of which this is a row
	 */
// public CharacterStateMatrix getMatrix() {
// return matrix;
// }

	/**
	 * Remove the last cell in this row.
	 * 
	 * @return the removed cell, or <code>null</code> if the row was empty
	 * 
	 */
	public CharacterStateCell removeLastCell() {
		if (cells.size() == 0) {
			// nothing to do
			return null;
		}
		final CharacterStateCell oldPhyloCharMatrixCell = cells.remove(cells
				.size() - 1);
		cellIdx.remove(oldPhyloCharMatrixCell);
		// oldPhyloCharMatrixCell.setRow(null);
		resetPPodVersionInfo();
		return oldPhyloCharMatrixCell;
	}

	/**
	 * Reset the pPOD version info of this row and that of its matrix.
	 * 
	 * @return this {@code CharacterStateRow}
	 */
	@Override
	protected CharacterStateRow resetPPodVersionInfo() {
		if (getPPodVersionInfo() == null) {

		} else {
// if (matrix != null) {
// matrix.resetPPodVersionInfo();
// }
			super.resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Setter. Intentionally package-private.
	 * 
	 * @param matrix the {@code CharacterStateMatrix} of which this is a row.
	 *            This is nullable.
	 * 
	 * @return this {@code CharacterStateRow}
	 */
// CharacterStateRow setMatrix(final CharacterStateMatrix matrix) {
// this.matrix = matrix;
// return this;
// }

	/**
	 * Constructs a {@code String} with all attributes in name=value format.
	 * 
	 * @return a {@code String} representation of this object
	 */
	@Override
	public String toString() {
		final String TAB = ",";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("CharacterStateRow(").append("id=").append(TAB).append(
				"cells=").append(this.cells).append(")\n");

		return retValue.toString();
	}

	@Override
	public CharacterStateRow accept(final IVisitor visitor) {
		visitor.visit(this);
		for (final CharacterStateCell cell : getCells()) {
			cell.accept(visitor);
		}
		return this;
	}
}
