package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.annotation.Nonnegative;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;

import edu.upenn.cis.ppod.modelinterfaces.ICell;
import edu.upenn.cis.ppod.modelinterfaces.IMatrix;
import edu.upenn.cis.ppod.modelinterfaces.IPPodVersioned;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A row of {@code Cell<?>}s
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Row<C extends Cell<?>> extends
		PPodEntity implements Iterable<C> {

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
		final List<C> clearedCells = newArrayList();
		for (final ICell clearedCell : clearedCells) {
			clearedCell.unsetRow();
		}
		getCells().clear();
		return clearedCells;
	}

	/**
	 * Get the cell at position {@code pPodCellPosition}.
	 * 
	 * @param pPodCellPosition the position of the cell we're interested in
	 * @return the cell at position {@code pPodCellPosition}
	 * 
	 * @throws IndexOutOfBoundsException if {@code pPodCellPosition} is out of
	 *             bounds for this row
	 */
	public C getCell(@Nonnegative final int pPodCellPosition) {
		return getCells().get(pPodCellPosition);
	}

	public int getCellPosition(final CategoricalCell cell) {
		checkNotNull(cell);
		checkArgument(this.equals(cell.getRow()),
				"cell does not belong to this row");
		final Integer cellPosition = cell.getPosition();
		if (cellPosition == null) {
			throw new AssertionError(
					"cell has been assigned to a row but has now position set");
		}
		return cellPosition;
	}

	/**
	 * Get the cells that make up this row.
	 * <p>
	 * Generally, prefer calling {@code iterator()} to access the cells.
	 * 
	 * @return the cells that make up this row
	 */
	protected abstract List<C> getCells();

// /**
// * Get the matrix that owns these rows.
// * <p>
// * Will be {@code null} if and only if this row is not part of a matrix.
// * Will never be {@code null} for persistent objects.
// *
// * @return the {@code CharacterStateMatrix} of which this is a row
// */
// @Nullable
// public abstract Matrix<Row<C>> getMatrix();

	/**
	 * Get the number of cells this row has.
	 * 
	 * @return the number of cells this row has
	 */
	public int getCellsSize() {
		return getCells().size();
	}

	protected abstract IMatrix getMatrix();

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
	public abstract List<C> setCells(final List<? extends C> cells);

	/**
	 * Reset the pPOD version info of this row and that of its matrix.
	 * 
	 * @return this {@code CharacterStateRow}
	 */
	@Override
	public Row<C> setInNeedOfNewPPodVersionInfo() {
		final IPPodVersioned matrix = getMatrix();
		if (matrix != null) {
			matrix.setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

}
