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

import edu.upenn.cis.ppod.imodel.IDnaSequence;
import edu.upenn.cis.ppod.imodel.IDnaSequenceSet;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMapPlus;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OtuDnaSequencePair;

/**
 * An OTU-keyed map of {@link DNASequence}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Embeddable
@Access(AccessType.PROPERTY)
public class DnaSequences
		implements IOtuKeyedMap<IDnaSequence> {

	private final IOtuKeyedMapPlus<IDnaSequence, IDnaSequenceSet, OtuDnaSequencePair> sequences =
			new OtuKeyedMapPlus<IDnaSequence, IDnaSequenceSet, OtuDnaSequencePair>();

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
		sequences.afterUnmarshal((IDnaSequenceSet) parent);
	}

	protected boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		getOTUSomethingPairs().clear();
		for (final Map.Entry<IOtu, IDnaSequence> otuToRow : getValues()
				.entrySet()) {
			getOTUSomethingPairs().add(
					new OtuDnaSequencePair(otuToRow.getKey(), otuToRow
							.getValue()));
		}
		return true;
	}

	public void clear() {
		sequences.clear();
	}

	public IDnaSequence get(final IOtu key) {
		return sequences.get(key);
	}

	@XmlElement(name = "otuSequencePair")
	@Transient
	public Set<OtuDnaSequencePair> getOTUSomethingPairs() {
		return sequences.getOtuKeyedPairs();
	}

	@Parent
	public IDnaSequenceSet getParent() {
		return sequences.getParent();
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,
			targetEntity = DnaSequence.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DnaSequence.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	@MapKeyClass(Otu.class)
	public Map<IOtu, IDnaSequence> getValues() {
		return sequences.getValues();
	}

	public IDnaSequence put(final IOtu key, final IDnaSequence value) {
		return sequences.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		sequences.updateOtus();
	}

	public void setParent(final IDnaSequenceSet parent) {
		sequences.setParent(parent);
	}

	public void setValues(final Map<IOtu, IDnaSequence> values) {
		sequences.setValues(values);
	}
}
