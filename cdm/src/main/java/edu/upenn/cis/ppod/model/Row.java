package edu.upenn.cis.ppod.model;

import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;

import edu.upenn.cis.ppod.modelinterfaces.IRow;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A row of cells.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Row<C extends Cell<?>> extends PPodEntity implements
		Iterable<C>, IRow {

	Row() {}

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

	/**
	 * Set the cells of this row.
	 * 
	 * @param newCells the cells.
	 * 
	 * @return any cells which were removed as a result of this operation
	 * 
	 * @throws IllegalStateException if {@code this.getParent() == null}
	 * @throws IllegalStateException if the owning matrix does not have the same
	 *             number of columns as {@code newCells.size()}
	 */
	public abstract List<C> setCells(
			final List<? extends C> cells);

	protected abstract Row<C> unsetOTUsToRows();

}
