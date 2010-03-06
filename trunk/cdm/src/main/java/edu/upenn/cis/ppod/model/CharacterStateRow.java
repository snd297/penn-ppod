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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

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
public class CharacterStateRow extends PPodEntity {

	/** The position in the matrix. */
	@Column(name = "POSITION", nullable = false)
	private Integer position;

	/** This entitiy's table. Intentionally package-private. */
	static final String TABLE = "CHARACTER_STATE_ROW";

	static final String CELLS_INDEX_COLUMN = CharacterStateCell.TABLE
			+ "_POSITION";

	/**
	 * The column where a {@code CharacterStateRow}'s
	 * {@link javax.persistence.Id} gets stored. Intentionally package-private.
	 */
	static final String ID_COLUMN = TABLE + "_ID";

	/**
	 * The {@code CharacterStateCell}s that make up the row.
	 * <p>
	 * We don't cascade {@code SAVE_UPDATE} since there are so many cells and it
	 * slows things down quite a bit - at least for saves (haven't looked at
	 * update yet).
	 * <p>
	 * There is evidence that {@code DELETE_ORPHAN} slows things down so we're
	 * not including that either.
	 */
	@OneToMany(mappedBy = "row")
	@OrderBy("position")
	private final List<CharacterStateCell> cells = newArrayList();

	/** {@code CharacterStateCell}-><code>cells</code>Index lookup. */
	@org.hibernate.annotations.CollectionOfElements
	@JoinTable(name = TABLE + "_" + CharacterStateCell.TABLE + "_IDX", joinColumns = @JoinColumn(name = ID_COLUMN))
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = CharacterStateCell.ID_COLUMN))
	@Column(name = CharacterStateCell.TABLE + "_IDX")
	private final Map<CharacterStateCell, Integer> cellIdx = newHashMap();

	/**
	 * The {@code CharacterStateMatrix} to which this {@code CharacterStateRow}
	 * belongs.
	 */
	@ManyToOne
	@JoinColumn(name = CharacterStateMatrix.ID_COLUMN, nullable = false)
	@Nullable
	private CharacterStateMatrix matrix;

	CharacterStateRow() {}

	@Override
	public CharacterStateRow accept(final IVisitor visitor) {
		visitor.visit(this);
		for (final CharacterStateCell cell : getCells()) {
			cell.accept(visitor);
		}
		super.accept(visitor);
		return this;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		int i = 0;
		for (final CharacterStateCell cell : getCells()) {
			cellIdx.put(cell, i++);
		}
	}

	/**
	 * Empty out and return this row's cells. After calling this,
	 * {@link #getCells()}{@code .size()} will be {@code 0}.
	 * <p>
	 * 
	 * @return the cleared cells - an empty list if {@link #getCells() == 0}
	 *         when this method is called
	 */
	private List<CharacterStateCell> clearCells() {
		final List<CharacterStateCell> clearedCells = newArrayList(getCells());
		if (getCells().size() == 0) {
			return clearedCells;
		}
		for (final CharacterStateCell clearedCell : clearedCells) {
			clearedCell.setRow(null);
		}
		cells.clear();
		cellIdx.clear();
		return clearedCells;
	}

	/**
	 * Get an unmodifiable view of the cell to row position lookup map.
	 * 
	 * @return get an unmodifiable view of the cell to row position lookup map
	 */
	public Map<CharacterStateCell, Integer> getCellIdx() {
		return Collections.unmodifiableMap(cellIdx);
	}

	/**
	 * Get an unmodifiable view of the row.
	 * 
	 * @return an unmodifiable view of the row
	 */
	public List<CharacterStateCell> getCells() {
		return Collections.unmodifiableList(cells);
	}

	@XmlElement(name = "cell")
	private List<CharacterStateCell> getCellsModifiable() {
		return cells;
	}

	/**
	 * Getter. {@code null} at object creation.
	 * 
	 * @return the {@code CharacterStateMatrix} of which this is a row
	 */
	@Nullable
	public CharacterStateMatrix getMatrix() {
		return matrix;
	}

	/**
	 * Get the position.
	 * 
	 * @return the position
	 */
	@XmlAttribute
	public Integer getPosition() {
		return position;
	}

	/**
	 * Reset the pPOD version info of this row and that of its matrix.
	 * 
	 * @return this {@code CharacterStateRow}
	 */
	@Override
	public CharacterStateRow resetPPodVersionInfo() {
		if (getAllowResetPPodVersionInfo()) {
			if (isInNeedOfNewPPodVersionInfo()) {

			} else {
				checkState(getMatrix() != null);
				matrix.resetPPodVersionInfo();
				super.resetPPodVersionInfo();
			}
		}
		return this;
	}

	/**
	 * Set the cells of this row.
	 * 
	 * @param newCells the cells.
	 * 
	 * @return any cells which were removed as a result of this operation
	 * 
	 * @throws IllegalArgumentException if any of cells are such that {@code
	 *             cell.getRow() != this}
	 * @throws IllegalStateException if {@code this.getMatrix() == null}
	 */
	public List<CharacterStateCell> setCells(
			final List<CharacterStateCell> newCells) {
		checkNotNull(newCells);

		if (newCells.equals(getCells())) {
			return Collections.emptyList();
		}

		if (getMatrix() == null) {
			throw new IllegalStateException(
					"This row hasn't been added to a matrix yet");
		}

		if (getMatrix().getCharacters().size() != newCells.size()) {
			throw new IllegalStateException(
					"the matrix has different number of characters "
							+ getMatrix().getCharacters().size()
							+ " than cells " + newCells.size());
		}
		for (int newCellPos = 0; newCellPos < newCells.size(); newCellPos++) {
			if (getMatrix().getCharacters().size() > newCellPos
					&& getMatrix().getCharacters().get(newCellPos) == null) {
				throw new IllegalStateException("Character is null at column "
						+ newCells.size());
			}
		}

		final List<CharacterStateCell> clearedCells = newArrayList(getCells());
		clearedCells.removeAll(newCells);

		clearCells();
		for (int cellPos = 0; cellPos < newCells.size(); cellPos++) {
			getCellsModifiable().add(newCells.get(cellPos));
			newCells.get(cellPos).setRow(this);
			newCells.get(cellPos).setPosition(cellPos);
			cellIdx.put(getCells().get(cellPos), cellPos);
		}
		resetPPodVersionInfo();
		return clearedCells;
	}

	/**
	 * Setter.
	 * 
	 * @param matrix the {@code CharacterStateMatrix} of which this is a row
	 * 
	 * @return this {@code CharacterStateRow}
	 */
	CharacterStateRow setMatrix(@Nullable final CharacterStateMatrix matrix) {
		this.matrix = matrix;
		return this;
	}

	/**
	 * Set the position.
	 * 
	 * @param position the position to set
	 * 
	 * @return this
	 */
	CharacterStateRow setPosition(final Integer position) {
		this.position = position;
		return this;
	}

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

}
