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

import edu.upenn.cis.ppod.imodel.IDNASequence;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOTUKeyedMap;
import edu.upenn.cis.ppod.imodel.IOTUKeyedMapPlus;
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
		implements IOTUKeyedMap<IDNASequence> {

	private final IOTUKeyedMapPlus<IDNASequence, IDNASequenceSet, OTUDNASequencePair> sequences =
			new OTUKeyedMapPlus<IDNASequence, IDNASequenceSet, OTUDNASequencePair>();

	public void accept(final IVisitor visitor) {
		sequences.accept(visitor);
	}

	public void afterUnmarshal() {
		sequences.afterUnmarshal();
	}

	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		// Don't checkNotNull(parent) since it's called by JAXB and we can't
		// control what it does
		sequences.afterUnmarshal((IDNASequenceSet) parent);
	}

	protected boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTUSomethingPairs().clear();
		for (final Map.Entry<IOtu, IDNASequence> otuToRow : getValues()
				.entrySet()) {
			getOTUSomethingPairs().add(
					new OTUDNASequencePair(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	public void clear() {
		sequences.clear();
	}

	public IDNASequence get(final IOtu key) {
		return sequences.get(key);
	}

	@XmlElement(name = "otuSequencePair")
	@Transient
	public Set<OTUDNASequencePair> getOTUSomethingPairs() {
		return sequences.getOTUSomethingPairs();
	}

	@Parent
	public IDNASequenceSet getParent() {
		return sequences.getParent();
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,
			targetEntity = DNASequence.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DNASequence.JOIN_COLUMN))
	@MapKeyJoinColumn(name = OtuChangeCase.JOIN_COLUMN)
	@MapKeyClass(OtuChangeCase.class)
	public Map<IOtu, IDNASequence> getValues() {
		return sequences.getValues();
	}

	public IDNASequence put(final IOtu key, final IDNASequence value) {
		return sequences.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOTUs() {
		sequences.updateOTUs();
	}

	public void setParent(final IDNASequenceSet parent) {
		sequences.setParent(parent);
	}

	public void setValues(final Map<IOtu, IDNASequence> values) {
		sequences.setValues(values);
	}
}
