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

import java.util.Collections;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequenceSet.TABLE)
public class DNASequenceSet extends MolecularSequenceSet {

	public final static String TABLE = "DNA_SEQUENCE_SET";

	public final static String ID_COLUMN = "DNA_" + PersistentObject.ID_COLUMN;

	@OneToMany(mappedBy = "sequenceSet")
	private Set<DNASequence> sequences = newHashSet();

	@XmlElement(name = "sequence")
	private Set<DNASequence> getSequencesModifiable() {
		return sequences;
	}

	public DNASequenceSet setSequenceSet(final Set<DNASequence> newSequences) {
		sequences = newSequences;
		return this;
	}

	public Set<DNASequence> setSequences(final Set<DNASequence> newSequences) {
		checkNotNull(newSequences);

		if (newSequences.equals(getSequences())) {
			return Collections.emptySet();
		}

		final Set<DNASequence> removedSequences = newHashSet(getSequencesModifiable());
		removedSequences.removeAll(newSequences);
		for (final DNASequence removedSequence : removedSequences) {
			removedSequence.setSequenceSet(null);
		}

		getSequencesModifiable().clear();
		getSequencesModifiable().addAll(newSequences);
		for (final DNASequence sequence : getSequencesModifiable()) {
			sequence.setSequenceSet(this);
		}
		return removedSequences;
	}

	@Override
	public Set<MolecularSequence> getSequences() {
		final Set<MolecularSequence> molecularSequences = newHashSet();
		for (final MolecularSequence sequence : getSequencesModifiable()) {
			molecularSequences.add(sequence);
		}
		return molecularSequences;
	}

}
