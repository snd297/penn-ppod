package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.google.common.annotations.Beta;

import edu.upenn.cis.ppod.util.IVisitor;

@Entity
@Table(name = ProteinMatrix.TABLE)
public class ProteinMatrix
		extends Matrix<ProteinRow, ProteinCell> {

	public final static String TABLE = "PROTEIN_MATRIX";

	public final static String JOIN_COLUMN = TABLE + "_ID";

	@Embedded
	private final ProteinRows rows = new ProteinRows(this);

	/**
	 * No-arg constructor.
	 */
	public ProteinMatrix() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitProteinMatrix(this);
		super.accept(visitor);
	}

	@Override
	protected ProteinRows getOtuKeyedRows() {
		return rows;
	}

	/**
	 * Remove the cells the make up the given column number.
	 * 
	 * @param columnNo the column to remove
	 * 
	 * @return the cells in the column
	 */
	@Beta
	public List<DnaCell> removeColumn(final int columnNo) {
		throw new UnsupportedOperationException();
	}
}
