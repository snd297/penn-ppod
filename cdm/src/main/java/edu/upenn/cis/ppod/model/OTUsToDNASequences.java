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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.util.OTUDNASequencePair;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = OTUsToDNASequences.TABLE)
public class OTUsToDNASequences extends
		OTUKeyedMap<DNASequence> {

	final static String TABLE = "OTUS_TO_DNA_SEQUENCES";

	final static String ID_COLUMN = TABLE + "_"
											+ PersistentObject.ID_COLUMN;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "otusToSequences")
	@CheckForNull
	private DNASequenceSet sequenceSet;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKeyJoinColumn(name = OTU.JOIN_COLUMN)
	private final Map<OTU, DNASequence> sequences = newHashMap();

	/**
	 * For marshalling {@code sequences}. Since a {@code Map}'s key couldn't be
	 * an {@code XmlIDREF} in JAXB - at least not easily.
	 */
	@Transient
	private final Set<OTUDNASequencePair> otuSequencePairs = newHashSet();

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		setSequenceSet((DNASequenceSet) parent);
		for (final OTUSomethingPair<DNASequence> otuSequencePair : getOTUValuePairs()) {
			otuSequencePair.getSecond().setOTUsToSequences(this);
		}
	}

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

	@Nullable
	@Override
	protected DNASequenceSet getParent() {
		return sequenceSet;
	}

	@Override
	public DNASequence put(final OTU otu, final DNASequence sequence) {
		checkNotNull(otu);
		checkNotNull(sequence);
		final DNASequence originalSequence = super.putHelper(otu, sequence);

		sequence.setOTUsToSequences(this);
		return originalSequence;
	}

	protected void setInNeedOfNewPPodVersion() {
		if (sequenceSet != null) {
			sequenceSet.setInNeedOfNewPPodVersionInfo();
		}
	}

	@Override
	protected OTUsToDNASequences setInNeedOfNewPPodVersionInfo() {
		if (sequenceSet != null) {
			sequenceSet.setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	protected OTUsToDNASequences setSequenceSet(final DNASequenceSet sequenceSet) {
		checkNotNull(sequenceSet);
		this.sequenceSet = sequenceSet;
		return this;
	}

}
