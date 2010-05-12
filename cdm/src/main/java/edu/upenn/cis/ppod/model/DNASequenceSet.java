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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
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
public class DNASequenceSet extends SequenceSet<DNASequence> {

	public final static String TABLE = "DNA_SEQUENCE_SET";

	public final static String JOIN_COLUMN = TABLE + "_"
												+ PersistentObject.ID_COLUMN;

	/**
	 * The sequences.
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	@CheckForNull
	private OTUsToDNASequences otusToSequences;

	protected DNASequenceSet() {}

	@Inject
	protected DNASequenceSet(final OTUsToDNASequences otusToDNASequences) {
		this.otusToSequences = otusToDNASequences;
		this.otusToSequences.setSequenceSet(this);
	}

	@Override
	public void accept(final IVisitor visitor) {
		getOTUsToSequences().accept(visitor);
		for (final DNASequence sequence : getOTUsToSequences()
				.getValuesInOTUSetOrder()) {
			sequence.accept(visitor);
		}
		super.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public DNASequenceSet clearSequences() {
		getOTUsToSequences().clear();
		return this;
	}

	@XmlElement(name = "otusToSequences")
	@Override
	@Nullable
	protected OTUsToDNASequences getOTUsToSequences() {
		return otusToSequences;
	}

	@Override
	public DNASequence getSequence(final OTU otu) {
		checkNotNull(otu);
		return getOTUsToSequences().get(otu);
	}

	@Override
	@CheckForNull
	public DNASequence putSequence(final OTU otu,
			final DNASequence sequence) {
		checkNotNull(otu);
		checkNotNull(sequence);
		checkArgument(sequence.getSequence() != null,
				"sequence.getSequence() == null");
		checkSequenceSize(sequence);
		return getOTUsToSequences().put(otu, sequence);
	}

	/**
	 * @throws IllegalStateException if {@link #getOTUsToSequences()} {@code ==
	 *             null}.
	 */
	@Override
	protected SequenceSet<DNASequence> setOTUsInOTUsToSequences(
			@Nullable final OTUSet otuSet) {
		checkState(
				getOTUsToSequences() != null,
				"getOTUsToSequences() == null, so there is no otusToSequences to operate on");
		getOTUsToSequences().setOTUs();
		return this;
	}

	/** For JAXB. */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	@SuppressWarnings("unused")
	private DNASequenceSet setOTUsToSequences(
			final OTUsToDNASequences otusToSequences) {
		checkNotNull(otusToSequences);
		this.otusToSequences = otusToSequences;
		return this;
	}

}
