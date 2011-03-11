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

import java.util.List;

import com.google.common.annotations.VisibleForTesting;

import edu.upenn.cis.ppod.imodel.IChild;

/**
 * A row of cells.
 * 
 * @author Sam Donnelly
 * 
 * @param <C> the type of cell we have
 * @param <M> the parent of the row
 */
abstract class Row<C extends Cell<?, ?>, M extends Matrix<?>>
		implements IChild<M> {

	Row() {}

	/**
	 * Empty out this row's cells.
	 * <p>
	 * This method {@code null}s out the cell->row relationship.
	 * <p>
	 * This method calls {@code Cell.setParent(null)} on all cleared cells.
	 * <p>
	 * This method calls {@code Cell.setPosition(null)} on all cleared cells.
	 * 
	 * @return this
	 */
	@VisibleForTesting
	void clearCells() {
		for (final C clearedCell : getCells()) {
			clearedCell.setParent(null);
			clearedCell.setPosition(null);
		}
		getCells().clear();
	}

	/**
	 * Get the cells that make up this row.
	 * 
	 * @return the cells that make up this row
	 */
	public abstract List<C> getCells();

	/**
	 * Set the cells of this row.
	 * <p>
	 * This handles both sides of the {@code Row<->Cell} relationship.
	 * 
	 * @param cells the cells
	 * 
	 * @return any cells which were removed as a result of this operation
	 * 
	 * @throws IllegalStateException if {@code this.getParent() == null}
	 * @throws IllegalStateException if the owning matrix does not have the same
	 *             number of columns as {@code cells.size()}
	 */
	public abstract void clearAndAddCells(final List<? extends C> cells);

	void setCellsHelper(
			final List<? extends C> cells) {
		checkNotNull(cells);

		if (cells.equals(getCells())) {
			return;
		}
		final M matrix = getParent();

		checkState(matrix != null, "This row hasn't been added to a matrix yet");

		final List<C> removedCells = newArrayList(getCells());
		removedCells.removeAll(cells);

		clearCells();

		getCells().addAll(cells);

		int cellPos = -1;
		for (final C cell : getCells()) {
			cellPos++;
			cell.setPosition(cellPos);
		}
	}

}
