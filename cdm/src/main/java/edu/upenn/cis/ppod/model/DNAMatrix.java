package edu.upenn.cis.ppod.model;

import java.util.Collections;
import java.util.Iterator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.inject.Inject;

/**
 * A {@link MolecularMatrix} composed of {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNAMatrix.TABLE)
public class DNAMatrix extends MolecularMatrix<DNARow> {
	public final static String TABLE = "DNA_MATRIX_2";

	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	private final OTUsToDNARows otusToRows;

	@Inject
	protected DNAMatrix(final OTUsToDNARows otusToRows) {
		this.otusToRows = otusToRows;
		this.otusToRows.setMatrix(this);
	}

	@Override
	protected OTUsToDNARows getOTUsToRows() {
		return otusToRows;
	}

	public Iterator<DNARow> iterator() {
		return Collections
				.unmodifiableList(otusToRows.getValuesInOTUSetOrder())
				.iterator();
	}

}
