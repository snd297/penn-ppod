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

import java.util.Collections;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.modelinterfaces.IOTU;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link DNASequence}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequenceSet.TABLE)
public class DNASequenceSet extends SequenceSet<DNASequence> {

	/**
	 * The name of the entity's table.
	 */
	public final static String TABLE = "DNA_SEQUENCE_SET";

	/**
	 * Used for foreign keys that point at this table.
	 */
	public final static String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	/**
	 * The sequences.
	 */
	@Embedded
	@CheckForNull
	private DNASequences sequences;

	DNASequenceSet() {}

	@Inject
	DNASequenceSet(final DNASequences otusToDNASequences) {
		this.sequences = otusToDNASequences;
		this.sequences.setParent(this);
	}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDNASequenceSet(this);
		super.accept(visitor);
	}

	@Override
	public DNASequenceSet clearSequences() {
		getOTUKeyedSequences().clear();
		return this;
	}

	@XmlElement(name = "sequences")
	@Override
	@Nullable
	protected DNASequences getOTUKeyedSequences() {
		return sequences;
	}

	@Override
	public DNASequence getSequence(final IOTU otu) {
		checkNotNull(otu);
		return getOTUKeyedSequences().get(otu);
	}

	@Override
	public Map<IOTU, DNASequence> getSequences() {
		return Collections.unmodifiableMap(
				getOTUKeyedSequences()
						.getValues());
	}

	@Override
	@CheckForNull
	public DNASequence putSequence(
			final IOTU otu,
			final DNASequence sequence) {
		checkNotNull(otu);
		checkNotNull(sequence);
		checkArgument(sequence.getSequence() != null,
						"sequence.getSequence() == null");
		checkSequenceLength(sequence);
		return getOTUKeyedSequences().put(otu, sequence);
	}

	/** Protected For JAXB. */
	protected void setOTUKeyedSequences(
			final DNASequences sequences) {
		checkNotNull(sequences);
		this.sequences = sequences;
	}

}
