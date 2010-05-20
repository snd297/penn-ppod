package edu.upenn.cis.ppod.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A {@link MolecularMatrix} composed of {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNAMatrix.TABLE)
public class DNAMatrix extends MolecularMatrix<DNARow> {
	public final static String TABLE = "DNA_MATRIX";

	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	private DNARows rows;

	DNAMatrix() {}

	@Inject
	protected DNAMatrix(final DNARows rows) {
		this.rows = rows;
		this.rows.setMatrix(this);
	}

	@Override
	public void accept(final IVisitor visitor) {
		super.accept(visitor);
		visitor.visit(this);
	}

	/**
	 * Created for JAXB.
	 */
	@XmlElement(name = "rows")
	@Override
	protected DNARows getOTUKeyedRows() {
		return rows;
	}

	/**
	 * Created for JAXB.
	 */
	protected DNAMatrix setOTUKeyedRows(final DNARows rows) {
		this.rows = rows;
		return this;
	}

}
