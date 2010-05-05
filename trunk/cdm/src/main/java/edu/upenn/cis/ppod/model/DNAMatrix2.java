package edu.upenn.cis.ppod.model;

import java.util.Iterator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.inject.Inject;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNAMatrix2.TABLE)
public class DNAMatrix2 extends Matrix<DNARow> {
	public final static String TABLE = "DNA_MATRIX_2";

	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	private OTUsToDNARows otusToRows;

	@Inject
	protected DNAMatrix2(final OTUsToDNARows otusToRows) {
		this.otusToRows = otusToRows;
		this.otusToRows.setMatrix(this);
	}

	public Iterator<DNARow> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected OTUsToRows<DNARow> getOTUsToRows() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
