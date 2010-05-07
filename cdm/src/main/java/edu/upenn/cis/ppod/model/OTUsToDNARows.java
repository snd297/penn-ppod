package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * Maps {@link OTU}s to {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = "OTUS_TO_DNA_ROWS")
public class OTUsToDNARows extends OTUKeyedMap<DNARow> {

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKeyJoinColumn(name = OTU.JOIN_COLUMN)
	final Map<OTU, DNARow> otusToRows = newHashMap();

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "otusToRows")
	@CheckForNull
	private DNAMatrix2 matrix;

	@Override
	protected Set<OTUSomethingPair<DNARow>> getOTUValuePairs() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected Map<OTU, DNARow> getOTUsToValues() {
		return otusToRows;
	}

	@Override
	protected DNAMatrix2 getParent() {
		return matrix;
	}

	@Override
	protected OTUKeyedMap<DNARow> setInNeedOfNewPPodVersionInfo() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	protected OTUsToDNARows setMatrix(final DNAMatrix2 matrix) {
		this.matrix = matrix;
		return this;
	}

}
