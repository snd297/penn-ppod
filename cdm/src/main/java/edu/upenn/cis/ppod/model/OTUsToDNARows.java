package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

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
import javax.persistence.Transient;

import edu.upenn.cis.ppod.util.OTUDNARowPair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * Maps {@link OTU}s to {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = "OTUS_TO_DNA_ROWS")
public class OTUsToDNARows extends OTUKeyedMap<DNARow> {

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "otusToRows")
	@CheckForNull
	private DNAMatrix matrix;

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUDNARowPair> otuRowPairs = newHashSet();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKeyJoinColumn(name = OTU.JOIN_COLUMN)
	final Map<OTU, DNARow> rows = newHashMap();

	@Override
	protected Map<OTU, DNARow> getOTUsToValues() {
		return rows;
	}

	@Override
	protected Set<OTUSomethingPair<DNARow>> getOTUValuePairs() {
		final Set<OTUSomethingPair<DNARow>> otuValuePairs = newHashSet();
		for (final OTUDNARowPair otuRowPair : otuRowPairs) {
			otuValuePairs.add(otuRowPair);
		}
		return otuValuePairs;
	}

	@Override
	protected DNAMatrix getParent() {
		return matrix;
	}

	@Override
	protected OTUsToDNARows setInNeedOfNewPPodVersionInfo() {
		if (matrix != null) {
			matrix.setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	protected OTUsToDNARows setMatrix(final DNAMatrix matrix) {
		this.matrix = matrix;
		return this;
	}

	@Override
	public DNARow put(final OTU otu, final DNARow row) {
		checkNotNull(otu);
		checkNotNull(row);
		row.setOTUsToRows(this);
		return super.putHelper(otu, row);
	}

}
