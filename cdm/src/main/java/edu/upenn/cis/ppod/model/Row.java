package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;

import edu.upenn.cis.ppod.modelinterfaces.IMatrix;
import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapValue;
import edu.upenn.cis.ppod.modelinterfaces.IRow;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A row of cells.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Row<C extends Cell<?>> extends PPodEntity implements
		IRow, Iterable<C>, IOTUKeyedMapValue {

	protected Row() {}

	@Override
	public void accept(final IVisitor visitor) {
		for (final C cell : getCells()) {
			cell.accept(visitor);
		}
		super.accept(visitor);
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal();
		int cellPosition = -1;
		for (final C cell : getCells()) {
			cell.setPosition(++cellPosition);
		}
	}

	/**
	 * Get the cells that make up this row.
	 * 
	 * @return the cells that make up this row
	 */
	protected abstract List<C> getCells();

	/**
	 * Get the number of cells this row has.
	 * 
	 * @return the number of cells this row has
	 */
	public int getCellsSize() {
		return getCells().size();
	}

	protected List<C> setCellsHelper(
			final List<? extends C> cells) {
		checkNotNull(cells);

		if (cells.equals(getCells())) {
			return Collections.emptyList();
		}

		final IMatrix matrix = getMatrix();

		checkState(matrix != null, "This row hasn't been added to a matrix yet");

		checkState(matrix.getColumnsSize() != null,
				"matrix.getColumnSize() == null");

		checkState(
				matrix.getColumnsSize() == cells.size(),
								"the matrix has different number of columns "
										+ matrix.getColumnsSize()
										+ " than cells "
										+ cells.size()
										+ " and cells > 0");

		final List<C> removedCells = newArrayList(getCells());
		removedCells.removeAll(cells);

		clearCells();
		for (int cellPos = 0; cellPos < cells.size(); cellPos++) {
			getCells().add(cells.get(cellPos));
			cells.get(cellPos).setPosition(cellPos);
		}
		setInNeedOfNewPPodVersionInfo();
		return removedCells;
	}

	/**
	 * Set the cells of this row.
	 * <p>
	 * This only handles the {@code Row} side of the {@code Row->Cell}
	 * relationship.
	 * 
	 * @param newCells the cells.
	 * 
	 * @return any cells which were removed as a result of this operation
	 * 
	 * @throws IllegalStateException if {@code this.getMatrix() == null}
	 * @throws IllegalStateException if the owning matrix does not have the same
	 *             number of columns as {@code cells.size()}
	 */
	public abstract List<C> setCells(final List<? extends C> cells);

	/**
	 * Empty out and return this row's cells.
	 *<p>
	 * This method will not mark this object or parents as in need of a new pPOD
	 * version. Which can be useful to free up the cells for garbage collection
	 * after the row and cells are evicted but the matrix is still in the
	 * persistence context.
	 * <p>
	 * This method {@code null}s out the cell->row relationship.
	 * 
	 * @return the cleared cells - an empty list if {@code getCells() == 0} when
	 *         this method is called
	 */
	public List<C> clearCells() {
		final List<C> clearedCells = newArrayList(getCells());
		for (final C clearedCell : clearedCells) {
			clearedCell.unsetRow();
		}
		getCells().clear();
		return clearedCells;
	}

}
