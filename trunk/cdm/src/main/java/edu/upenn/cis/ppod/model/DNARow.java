package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
	public static final String ID_COLUMN = TABLE + "_"
											+ PersistentObject.ID_COLUMN;

	@OneToMany(mappedBy = "row", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("position")
	private final List<DNACell> cells = newArrayList();

	@Override
	protected List<DNACell> getCells() {
		return cells;
	}

	@Override
	protected Row<DNACell> unsetOTUsToRows() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Iterator<DNACell> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DNACell> setCells(
			List<? extends DNACell> cells) {
		this.cells.clear();
		this.cells.addAll(cells);
		return null;
	}

	public IMatrix getMatrix() {
		throw new UnsupportedOperationException();
	}

}
