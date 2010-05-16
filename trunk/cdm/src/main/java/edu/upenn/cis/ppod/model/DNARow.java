package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

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

	@OneToMany(mappedBy = "row", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("position")
	private final List<DNACell> cells = newArrayList();

	/**
	 * This is the parent of the row. It lies in between this and the matrix.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@CheckForNull
	private OTUsToDNARows otusToRows;

	protected DNARow() {}

	@Override
	public List<DNACell> getCells() {
		return Collections.unmodifiableList(cells);
	}

	@Override
	protected List<DNACell> getCellsModifiable() {
		return cells;
	}

	public IMatrix getMatrix() {
		if (otusToRows == null) {
			return null;
		}
		return otusToRows.getParent();
	}

	public Iterator<DNACell> iterator() {
		return Collections.unmodifiableList(cells).iterator();
	}

	@Override
	public List<DNACell> setCells(final List<? extends DNACell> cells) {
		final List<DNACell> clearedCells = super.setCellsHelper(cells);

		for (final DNACell cell : getCells()) {
			cell.setRow(this);
		}
		return clearedCells;
	}

	public DNARow setOTUsToRows(final OTUsToDNARows otusToRows) {
		this.otusToRows = otusToRows;
		return this;
	}

	public Row<DNACell> unsetOTUKeyedMap() {
		otusToRows = null;
		return this;
	}

}
