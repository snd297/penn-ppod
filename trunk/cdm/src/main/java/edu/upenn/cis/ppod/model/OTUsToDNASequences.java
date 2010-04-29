/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
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
	 * when sequences are added to the persistence context. We sometimes don't
	 * want the sequences saved or reattached when the the matrix is.
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKeyJoinColumn(name = OTU.ID_COLUMN)
	private final Map<OTU, DNASequence> sequences = newHashMap();

	/**
	 * For marshalling {@code sequences}. Since a {@code Map}'s key couldn't be
	 * an {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUDNASequencePair> otuSequencePairs = newHashSet();

	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTUSequencePairs().clear();
		for (final Map.Entry<OTU, DNASequence> otuToRow : getOTUsToValues()
				.entrySet()) {
			getOTUSequencePairs().add(
					OTUDNASequencePair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	@XmlElement(name = "otuSequencePair")
	protected Set<OTUDNASequencePair> getOTUSequencePairs() {
		return otuSequencePairs;
	}

	@Override
	protected Map<OTU, DNASequence> getOTUsToValues() {
		return sequences;
	}

	@Override
	protected Set<OTUSomethingPair<DNASequence>> getOTUValuePairs() {
		final Set<OTUSomethingPair<DNASequence>> otuSomethingPairs = newHashSet();
		for (final OTUDNASequencePair otuDNASequencePair : getOTUSequencePairs()) {
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
