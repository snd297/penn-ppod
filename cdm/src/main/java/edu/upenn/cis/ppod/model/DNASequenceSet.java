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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import edu.upenn.cis.ppod.util.IPair;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNASequenceSet.TABLE)
public class DNASequenceSet extends MolecularSequenceSet<DNASequence> {

	public final static String TABLE = "DNA_SEQUENCE_SET";

	public final static String ID_COLUMN = TABLE + "_"
			+ PersistentObject.ID_COLUMN;

	/**
	 * The rows of the matrix. We don't do save_update cascades since we want to
	 * control when rows are added to the persistence context. We sometimes
	 * don't want the rows saved or reattached when the the matrix is.
	 */
	@org.hibernate.annotations.CollectionOfElements
	@JoinTable(name = OTU.TABLE + "_" + CharacterStateRow.TABLE, joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = CharacterStateRow.ID_COLUMN))
	@org.hibernate.annotations.MapKeyManyToMany(joinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private final Map<OTU, DNASequence> otusToRows = newHashMap();

	@OneToMany(mappedBy = "sequenceSet")
	private final Set<DNASequence> sequences = newHashSet();

	@Override
	protected Set<DNASequence> getSequencesModifiable() {
		return sequences;
	}

	@Override
	protected Set<IPair<OTU, DNASequence>> getOTUSequencePairsModifiable() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected Map<OTU, DNASequence> getOTUsToSeqeuencesModifiable() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Set<DNASequence> setSequences(final Set<DNASequence> dnaSequences) {
		checkNotNull(dnaSequences);
		final Set<DNASequence> removedSequences = super
				.setSequencesHelper(dnaSequences);
		for (final DNASequence dnaSequence : getSequences()) {
			dnaSequence.setSequenceSet(this);
		}
		return removedSequences;
	}
}
