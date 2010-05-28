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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.OTUDNARowPair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * Maps {@link OTU}s to {@link DNARow}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNARows.TABLE)
public class DNARows extends OTUKeyedMap<DNARow> {

	public static final String TABLE = "DNA_ROWS";

	public static final String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "rows")
	@CheckForNull
	private DNAMatrix matrix;

	/**
	 * For marshalling {@code rows}. Since a {@code Map}'s key couldn't be an
	 * {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUDNARowPair> otuRowPairs = newHashSet();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DNARow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = OTU.JOIN_COLUMN)
	private final Map<OTU, DNARow> rows = newHashMap();

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		setMatrix((DNAMatrix) parent);
		for (final OTUSomethingPair<DNARow> otuRowPair : otuRowPairs) {
			otuRowPair.getSecond().setRows(this);
		}
	}

	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTURowPairs().clear();
		for (final Map.Entry<OTU, DNARow> otuToRow : getOTUsToValues()
				.entrySet()) {
			getOTURowPairs().add(
					OTUDNARowPair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	@XmlElement(name = "otuRowPair")
	protected Set<OTUDNARowPair> getOTURowPairs() {
		return otuRowPairs;
	}

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
	public DNARow put(final OTU otu, final DNARow row) {
		checkNotNull(otu);
		checkNotNull(row);
		row.setRows(this);
		return super.putHelper(otu, row);
	}

	@Override
	protected DNARows setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		return this;
	}

	protected DNARows setMatrix(final DNAMatrix matrix) {
		this.matrix = matrix;
		return this;
	}

}
