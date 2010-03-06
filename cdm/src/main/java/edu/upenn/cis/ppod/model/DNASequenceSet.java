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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequenceSet.TABLE)
public class DNASequenceSet extends MolecularSequenceSet<DNASequence> {

	public final static String TABLE = "DNA_SEQUENCE_SET";

	public final static String ID_COLUMN = TABLE + "_"
			+ PersistentObject.ID_COLUMN;

	@OneToMany(mappedBy = "sequenceSet")
	private final Set<DNASequence> sequences = newHashSet();

	@Override
	protected Set<DNASequence> getSequencesModifiable() {
		return sequences;
	}

	public Set<DNASequence> setSequences(
			final Set<? extends DNASequence> newSequences) {
		checkNotNull(newSequences);
		final Set<DNASequence> removedSequences = super
				.setSequencesHelper(newSequences);
		for (final DNASequence dnaSequence : getSequences()) {
			dnaSequence.setSequenceSet(this);
		}
		return removedSequences;
	}
}
