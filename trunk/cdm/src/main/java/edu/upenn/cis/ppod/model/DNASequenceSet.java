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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link DNASequence}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequenceSet.TABLE)
public class DNASequenceSet extends MolecularSequenceSet<DNASequence> {

	public final static String TABLE = "DNA_SEQUENCE_SET";

	public final static String ID_COLUMN = TABLE + "_"
			+ PersistentObject.ID_COLUMN;

	/**
	 * The sequences.
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
	private OTUsToDNASequences otusToSequences;

	DNASequenceSet() {}

	@Inject
	DNASequenceSet(final OTUsToDNASequences otusToDNASequences) {
		this.otusToSequences = otusToDNASequences;
	}

	@Override
	public void accept(final IVisitor visitor) {
		otusToSequences.accept(visitor);
		for (final DNASequence sequence : getOTUsToSequences()
				.getValuesInOTUOrder(getOTUSet())) {
			sequence.accept(visitor);
		}
		super.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public void afterUnmarshal() {
		for (final DNASequence dnaSequence : this) {
			dnaSequence.setSequenceSet(this);
		}
		super.afterUnmarshal();
	}

	@XmlElement(name = "otusToSequences")
	@Override
	protected OTUsToDNASequences getOTUsToSequences() {
		return otusToSequences;
	}

	@Override
	public DNASequence getSequence(final OTU otu) {
		checkNotNull(otu);
		return getOTUsToSequences().get(otu, this);
	}

	@Override
	protected DNASequence putSequenceHelper(final OTU otu,
			final DNASequence newSequence) {
		return getOTUsToSequences().put(otu, newSequence, this);
	}

	@SuppressWarnings("unused")
	private DNASequenceSet setOTUsToSequences(
			final OTUsToDNASequences otusToSequences) {
		this.otusToSequences = otusToSequences;
		return this;
	}

	@Override
	protected MolecularSequenceSet<DNASequence> setOTUsInOTUsToSequences(
			final OTUSet otuSet) {
		checkNotNull(otuSet);
		getOTUsToSequences().setOTUs(otuSet, this);
		return this;
	}

}
