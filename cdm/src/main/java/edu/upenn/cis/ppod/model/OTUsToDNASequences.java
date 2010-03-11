package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.OTUDNASequencePair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = "OTUS_TO_DNA_SEQUENCES")
public class OTUsToDNASequences extends
		OTUsToMolecularSequences<DNASequence, DNASequenceSet> {

	/**
	 * The sequences. We don't do save_update cascades since we want to control
	 * when otusToSequences are added to the persistence context. We sometimes
	 * don't want the otusToSequences saved or reattached when the the matrix
	 * is.
	 */
	@org.hibernate.annotations.CollectionOfElements
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private final Map<OTU, DNASequence> otusToSequences = newHashMap();

	/**
	 * For marshalling {@code otusToSequences}. Since a {@code Map}'s key
	 * couldn't be an {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUDNASequencePair> otuSequencePairs = newHashSet();

	public boolean beforeMarshal(@Nullable final Marshaller marshaller) {
		getOTUSequencePairsModifiable().clear();
		for (final Map.Entry<OTU, DNASequence> otuToRow : getOTUsToValuesModifiable()
				.entrySet()) {
			getOTUSequencePairsModifiable().add(
					OTUDNASequencePair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	@XmlElement(name = "otuSequencePair")
	private Set<OTUDNASequencePair> getOTUSequencePairsModifiable() {
		return otuSequencePairs;
	}

	@Override
	protected Map<OTU, DNASequence> getOTUsToValuesModifiable() {
		return otusToSequences;
	}

	@Override
	protected Set<OTUSomethingPair<DNASequence>> getOTUValuePairs() {
		final Set<OTUSomethingPair<DNASequence>> otuSomethingPairs = newHashSet();
		for (final OTUDNASequencePair otuDNASequencePair : getOTUSequencePairsModifiable()) {
			otuSomethingPairs.add(otuDNASequencePair);
		}
		return otuSomethingPairs;
	}

	@Override
	public DNASequence put(final OTU otu, final DNASequence newSequence,
			final DNASequenceSet parent) {
		final DNASequence originalSequence = super.putHelper(otu, newSequence,
				parent);
		newSequence.setSequenceSet(parent);
		return originalSequence;
	}
}
