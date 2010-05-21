package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.modelinterfaces.IMatrix;

/**
 * A row of {@link DNACell}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNARow.TABLE)
public class DNARow extends Row<DNACell> {

	public static final String TABLE = "DNA_ROW";

	public static final String JOIN_COLUMN = TABLE + "_"
												+ PersistentObject.ID_COLUMN;
	/**
	 * The {@code CharacterStateCell}s that make up the row.
	 * <p>
	 * We don't cascade {@code SAVE_UPDATE} since there are so many cells and it
	 * slows things down quite a bit - at least for saves (haven't looked at
	 * update yet).
	 * <p>
	 * There is evidence that {@code DELETE_ORPHAN} slows things down so we're
	 * not including that either.
	 * <p>
	 * Remove is here so that the cells are deleted when owning row is.
	 */
	@OneToMany(mappedBy = "row", cascade = CascadeType.REMOVE)
	@OrderBy("position")
	private final List<DNACell> cells = newArrayList();

	/**
	 * This is the parent of the row. It lies in between this and the matrix.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = DNARows.JOIN_COLUMN)
	@CheckForNull
	private DNARows rows;

	DNARow() {}

	@Override
	public List<DNACell> getCells() {
		return Collections.unmodifiableList(cells);
	}

	@XmlElement(name = "cell")
	@Override
	protected List<DNACell> getCellsModifiable() {
		return cells;
	}

	public IMatrix getMatrix() {
		if (rows == null) {
			return null;
		}
		return rows.getParent();
	}

	@Override
	public List<DNACell> setCells(final List<? extends DNACell> cells) {
		final List<DNACell> clearedCells = super.setCellsHelper(cells);

		for (final DNACell cell : getCells()) {
			cell.setRow(this);
		}
		return clearedCells;
	}

	public DNARow setRows(final DNARows otusToRows) {
		this.rows = otusToRows;
		return this;
	}

	public Row<DNACell> unsetOTUKeyedMap() {
		rows = null;
		return this;
	}

}
