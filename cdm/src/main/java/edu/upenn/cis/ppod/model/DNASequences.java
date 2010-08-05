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

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Parent;

import edu.upenn.cis.ppod.modelinterfaces.IOTU;
import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMap;
import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapPlus;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUDNASequencePair;

/**
 * An OTU-keyed map of {@link DNASequence}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Embeddable
@Access(AccessType.PROPERTY)
public class DNASequences
		implements IOTUKeyedMap<DNASequence> {

	private final IOTUKeyedMapPlus<DNASequence, DNASequenceSet, OTUDNASequencePair> sequences =
			new OTUKeyedMapPlus<DNASequence, DNASequenceSet, OTUDNASequencePair>();

	public void accept(final IVisitor visitor) {
		sequences.accept(visitor);
	}

	public void afterUnmarshal() {
		sequences.afterUnmarshal();
	}

	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		// Don't checkNotNull(parent) since it's called by JAXB and we can't
		// control what it does
		sequences.afterUnmarshal((DNASequenceSet) parent);
	}

	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTUSomethingPairs().clear();
		for (final Map.Entry<IOTU, DNASequence> otuToRow : getValues()
				.entrySet()) {
			getOTUSomethingPairs().add(
					OTUDNASequencePair.of(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	public DNASequences clear() {
		sequences.clear();
		return this;
	}

	public DNASequence get(final IOTU key) {
		return sequences.get(key);
	}

	@XmlElement(name = "otuSequencePair")
	@Transient
	public Set<OTUDNASequencePair> getOTUSomethingPairs() {
		return sequences.getOTUSomethingPairs();
	}

	@Parent
	public DNASequenceSet getParent() {
		return sequences.getParent();
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DNASequence.JOIN_COLUMN))
	@MapKeyJoinColumn(name = OTU.JOIN_COLUMN)
	@MapKeyClass(OTU.class)
	public Map<IOTU, DNASequence> getValues() {
		return sequences.getValues();
	}

	public DNASequence put(final IOTU key, final DNASequence value) {
		return sequences.put(key, value);
	}

	public DNASequences setOTUs() {
		sequences.setOTUs();
		return this;
	}

	public DNASequences setParent(final DNASequenceSet parent) {
		sequences.setParent(parent);
		return this;
	}

	public DNASequences setValues(final Map<IOTU, DNASequence> values) {
		sequences.setValues(values);
		return this;
	}
}
