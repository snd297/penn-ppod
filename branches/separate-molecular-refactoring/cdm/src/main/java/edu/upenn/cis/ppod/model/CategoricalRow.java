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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A row of {@link CategoricalCell}s.
 * <p>
 * In Hibernate, composite elements may contain components but not collections -
 * that's why this is an entity and not a component class: it needs to contain a
 * {@code List<CategoricalCell>}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = CategoricalRow.TABLE)
public class CategoricalRow extends Row<CategoricalCell> {

	/** This entitiy's table. Intentionally package-private. */
	protected static final String TABLE = "CATEGORICAL_STATE_ROW";

	protected static final String CELLS_INDEX_COLUMN = PPodEntity.TABLE
														+ "_POSITION";

	/**
	 * The column where a {@code CharacterStateRow}'s
	 * {@link javax.persistence.Id} gets stored. Intentionally package-private.
	 */
	protected static final String ID_COLUMN = TABLE + "_ID";

	/**
	 * The {@code CategoricalCell}s that make up the row.
	 * <p>
	 * We don't cascade {@code SAVE_UPDATE} since there are so many cells and it
	 * slows things down quite a bit - at least for saves (haven't looked at
	 * update yet).
	 * <p>
	 * There is evidence that {@code DELETE_ORPHAN} slows things down so we're
	 * not including that either.
	 * <p>
	 * Remove is here so that the cells are deleted when the owning row is.
	 */
	@OneToMany(mappedBy = "row", cascade = CascadeType.REMOVE)
	@OrderBy("position")
	private final List<CategoricalCell> cells = newArrayList();

	@ManyToOne(fetch = FetchType.LAZY)
	@CheckForNull
	private OTUsToCategoricalRows otusToRows;

	CategoricalRow() {}

	@Override
	public void accept(final IVisitor visitor) {
		super.accept(visitor);
		visitor.visit(this);
	}

	@XmlElement(name = "cell")
	@Override
	protected List<CategoricalCell> getCells() {
		return cells;
	}

	@Nullable
	@Override
	public CategoricalMatrix getMatrix() {
		if (otusToRows == null) {
			return null;
		}
		return otusToRows.getMatrix();
	}

	/**
	 * Get an iterator over this row's cells.
	 * 
	 * @return an iterator over this row's cells
	 */
	public Iterator<CategoricalCell> iterator() {
		return Collections.unmodifiableList(getCells()).iterator();
	}

	/**
	 * Set the cells of this row.
	 * 
	 * @param newCells the cells.
	 * 
	 * @return any cells which were removed as a result of this operation
	 * 
	 * @throws IllegalStateException if {@code this.getMatrix() == null}
	 * @throws IllegalStateException if the owning matrix does not have the same
	 *             number of characters as {@code newCells.size()}
	 */
	@Override
	public List<CategoricalCell> setCells(
			final List<? extends CategoricalCell> cells) {
		checkNotNull(cells);

		if (cells.equals(getCells())) {
			return Collections.emptyList();
		}

		final Matrix matrix = getMatrix();

		checkState(matrix != null, "This row hasn't been added to a matrix yet");

		checkState(
				matrix.getColumnsSize() == cells.size(),
								"the matrix has different number of columns "
										+ matrix.getColumnsSize()
										+ " than cells "
										+ cells.size()
										+ " and cells > 0");

		final List<CategoricalCell> removedCells = newArrayList(getCells());
		removedCells.removeAll(cells);

		clearCells();
		for (int cellPos = 0; cellPos < cells.size(); cellPos++) {
			getCells().add(cells.get(cellPos));
			cells.get(cellPos).setRow(this);
			cells.get(cellPos).setPosition(cellPos);
		}
		setInNeedOfNewPPodVersionInfo();
		return removedCells;
	}

	/**
	 * Setter.
	 * 
	 * @param otusToRows the {@code CharacterStateMatrix} of which this is a row
	 * 
	 * @return this {@code CharacterStateRow}
	 */
	protected CategoricalRow setOTUsToRows(
			@CheckForNull final OTUsToCategoricalRows otusToRows) {
		this.otusToRows = otusToRows;
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
