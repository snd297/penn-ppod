/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.modelinterfaces.IMatrix;
import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapValue;
import edu.upenn.cis.ppod.modelinterfaces.IRow;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A row of cells.
 * 
 * @author Sam Donnelly
 * 
 * @param <C> the type of cell we have
 */
@MappedSuperclass
public abstract class Row<C extends Cell<?>>
		extends PPodEntity
		implements IRow, IOTUKeyedMapValue {

	protected Row() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		for (final C cell : getCells()) {
			cell.accept(visitor);
		}
		super.accept(visitor);
	}

	protected Row<C> addCellHelper(final C cell) {
		getCells().add(cell);
		setInNeedOfNewVersion();
		return this;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		int cellPosition = -1;
		for (final C cell : getCells()) {
			cellPosition++;
			cell.setPosition(cellPosition);
		}
	}

	/**
	 * Empty out and return this row's cells.
	 * <p>
	 * This method will not mark this object or parents as in need of a new pPOD
	 * version. Which can be useful to free up the cells for garbage collection
	 * after the cells are evicted but the matrix is still in the persistence
	 * context.
	 * <p>
	 * This method {@code null}s out the cell->row relationship.
	 * 
	 * @return this
	 */
	public Row<C> clearCells() {
		for (final C clearedCell : getCells()) {
			clearedCell.unsetRow();
			clearedCell.unsetPosition();
		}
		getCellsModifiable().clear();
		return this;
	}

	/**
	 * Get the cells that make up this row.
	 * 
	 * @return the cells that make up this row
	 */
	public List<C> getCells() {
		return Collections.unmodifiableList(getCellsModifiable());
	}

	/**
	 * Get a modifiable reference to this row's cells.
	 * 
	 * @return a modifiable reference to this row's cells
	 */
	protected abstract List<C> getCellsModifiable();

	/**
	 * Get the owner of this row.
	 * <p>
	 * TODO: eliminate wildcard in return by creating an interface.
	 * 
	 * @return
	 */
	@CheckForNull
	protected abstract OTUKeyedMap<?> getParent();

	/**
	 * Set the cells of this row.
	 * <p>
	 * This only handles both sides of the {@code Row<->Cell} relationship.
	 * 
	 * @param cells the cells
	 * 
	 * @return any cells which were removed as a result of this operation
	 * 
	 * @throws IllegalStateException if {@code this.getMatrix() == null}
	 * @throws IllegalStateException if the owning matrix does not have the same
	 *             number of columns as {@code cells.size()}
	 */
	public abstract List<C> setCells(final List<? extends C> cells);

	protected List<C> setCellsHelper(
			final List<? extends C> cells) {
		checkNotNull(cells);

		if (cells.equals(getCells())) {
			return Collections.emptyList();
		}

		final IMatrix matrix = getMatrix();

		checkState(matrix != null, "This row hasn't been added to a matrix yet");

		checkState(matrix.getColumnsSize() == cells.size(),
								"the matrix has different number of columns "
										+ matrix.getColumnsSize()
										+ " than cells "
										+ cells.size());

		final List<C> removedCells = newArrayList(getCells());
		removedCells.removeAll(cells);

		clearCells();

		int cellPos = -1;
		getCellsModifiable().addAll(cells);
		for (final Cell<?> cell : getCells()) {
			cellPos++;
			cell.setPosition(cellPos);
		}
		setInNeedOfNewVersion();
		return removedCells;
	}

	/**
	 * Reset the pPOD version info of this row and that of its matrix.
	 * 
	 * @return this {@code CharacterStateRow}
	 */
	@Override
	public Row<C> setInNeedOfNewVersion() {

		// So FindBugs knows it's okay
		final OTUKeyedMap<?> parent = getParent();
		if (parent != null) {
			parent.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
		return this;
	}
}
