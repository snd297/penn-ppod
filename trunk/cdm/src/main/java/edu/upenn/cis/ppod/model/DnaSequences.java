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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Parent;

import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * An OTU-keyed map of {@link DNASequence}s.
 * 
 * @author Sam Donnelly
 */
@Embeddable
@Access(AccessType.PROPERTY)
public class DnaSequences
		implements IOtuKeyedMap<DnaSequence> {

	private final OtuKeyedMapPlus<DnaSequence, DnaSequenceSet> sequences =
			new OtuKeyedMapPlus<DnaSequence, DnaSequenceSet>();

	public void accept(final IVisitor visitor) {
		sequences.accept(visitor);
	}

	public void clear() {
		sequences.clear();
	}

	public DnaSequence get(final Otu key) {
		return sequences.get(key);
	}

	@Parent
	public DnaSequenceSet getParent() {
		return sequences.getParent();
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DnaSequence.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	public Map<Otu, DnaSequence> getValues() {
		return sequences.getValues();
	}

	public DnaSequence put(final Otu key, final DnaSequence value) {
		return sequences.put(key, value);
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		sequences.updateOtus();
	}

	/** {@inheritDoc} */
	public void setParent(final DnaSequenceSet parent) {
		sequences.setParent(parent);
	}

	public void setValues(final Map<Otu, DnaSequence> values) {
		sequences.setValues(values);
	}
}
