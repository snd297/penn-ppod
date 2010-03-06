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

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;

@MappedSuperclass
public abstract class MolecularSequenceSet<S extends MolecularSequence> extends
		UUPPodEntity {

	static final String TABLE = "MOLECULAR_SEQUENCE_SET";

	@ManyToOne
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	/**
	 * Get this sequence set's owning OTU set.
	 * 
	 * @return this sequence set's owning OTU set
	 */
	@Nullable
	public OTUSet getOtuSet() {
		return otuSet;
	}

	/**
	 * Get the constituent sequences.
	 * 
	 * @return the constituent sequences
	 */
	public Set<S> getSequences() {
		return Collections.unmodifiableSet(getSequencesModifiable());
	}

	/**
	 * Created for JAXB.
	 * 
	 * @return a modifiable reference to this set's sequences
	 */
	@XmlElement(name = "sequence")
	protected abstract Set<S> getSequencesModifiable();

	@Override
	public MolecularSequenceSet resetPPodVersionInfo() {
		if (getOtuSet() != null) {
			getOtuSet().resetPPodVersionInfo();
		}
		super.resetPPodVersionInfo();
		return this;
	}

	/**
	 * Intentionally package-private and meant to be called in {@link OTUSet}.
	 * <p>
	 * A {@code null} value for {@code newOTUSet} indicates we're severing the
	 * relationship.
	 * 
	 * @param newOTUSet the OTU set that will own this sequence set
	 * 
	 * @return this sequence set
	 */
	protected MolecularSequenceSet setOtuSet(@Nullable final OTUSet newOTUSet) {
		otuSet = newOTUSet;
		return this;
	}

	public Set<S> setSequences(final Set<S> newSequences) {
		checkNotNull(newSequences);

		if (newSequences.equals(getSequences())) {
			return Collections.emptySet();
		}

		final Set<S> removedSequences = newHashSet(getSequences());
		removedSequences.removeAll(newSequences);
		for (final S removedSequence : removedSequences) {
			removedSequence.setSequenceSet(null);
		}

		getSequencesModifiable().clear();
		getSequencesModifiable().addAll(newSequences);
		for (final S sequence : getSequences()) {
			sequence.setSequenceSet(this);
		}
		resetPPodVersionInfo();
		return removedSequences;
	}

}
