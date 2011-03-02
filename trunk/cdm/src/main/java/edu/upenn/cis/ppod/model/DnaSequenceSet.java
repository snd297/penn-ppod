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

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

/**
 * A set of {@link DNASequence}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DnaSequenceSet.TABLE)
public class DnaSequenceSet
		extends SequenceSet<DnaSequence> {

	/**
	 * The name of the entity's table.
	 */
	public final static String TABLE = "DNA_SEQUENCE_SET";

	/**
	 * Used for foreign keys that point at this table.
	 */
	public final static String JOIN_COLUMN =
			TABLE + "_" + PersistentObject.ID_COLUMN;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = DnaSequence.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	private final Map<Otu, DnaSequence> sequences = newHashMap();

	/**
	 * Default constructor.
	 */
	public DnaSequenceSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitDnaSequenceSet(this);
		for (final DnaSequence sequence : sequences.values()) {
			if (sequence != null) {
				sequence.accept(visitor);
			}
		}
		super.accept(visitor);
	}

	/** {@inheritDoc} */
	public void clearSequences() {
		if (sequences.size() > 0) {
			sequences.clear();
			setInNeedOfNewVersion();
		}
	}

	@Override
	public DnaSequence getSequence(final Otu otu) {
		checkNotNull(otu);
		return sequences.get(otu);
	}

	@Override
	public Map<Otu, DnaSequence> getSequences() {
		return Collections.unmodifiableMap(sequences);
	}

	@Override
	@Nullable
	public DnaSequence putSequence(
			final Otu otu,
			final DnaSequence sequence) {
		checkNotNull(otu);
		checkNotNull(sequence);
		final DnaSequence oldSequence = sequences.put(otu, sequence);
		sequence.setParent(this);
		if (sequence != oldSequence || oldSequence == null) {
			setInNeedOfNewVersion();
		}

		if (sequence != oldSequence && oldSequence != null) {
			oldSequence.setParent(null);
		}
		return oldSequence;
	}

	@Override
	public void updateOtus() {
		UPennCisPPodUtil.updateOtus(getParent(), sequences, this);
	}
}
