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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.util.IPair;

@MappedSuperclass
public abstract class MolecularSequenceSet<S extends MolecularSequence<?>>
		extends UUPPodEntity {

	static final String TABLE = "MOLECULAR_SEQUENCE_SET";

	/**
	 * The position of an {@code OTU} in {@code otuOrdering} signifies its row
	 * number in <code>row</code>. So <code>otuOrdering</code> is a rowNumber->
	 * {@code OTU} lookup.
	 */
	@ManyToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.IndexColumn(name = OTU.TABLE + "_POSITION")
	private final List<OTU> otuOrdering = newArrayList();

	@ManyToOne
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	public S getSequence(final OTU otu) {
		checkNotNull(otu);
		return getOTUsToSeqeuencesModifiable().get(otu);
	}

	@Override
	public void afterUnmarshal() {
		for (final IPair<OTU, S> otuSequencePair : getOTUSequencePairs()) {
			getOTUsToSeqeuencesModifiable().put(otuSequencePair.getFirst(),
					otuSequencePair.getSecond());
		}

		// We're done with this - clear it out
		getOTUSequencePairs().clear();
	}

	/**
	 * Return an unmodifiable view of this matrix's <code>OTUSet</code>
	 * ordering.
	 * 
	 * @return see description
	 */
	public List<OTU> getOTUOrdering() {
		return Collections.unmodifiableList(getOTUOrderingModifiable());
	}

	@XmlElementWrapper(name = "otuOrdering")
	@XmlElement(name = "otuDocId")
	@XmlIDREF
	private List<OTU> getOTUOrderingModifiable() {
		return otuOrdering;
	}

	protected abstract Set<IPair<OTU, S>> getOTUSequencePairs();

	/**
	 * Getter. Will be {@code null} when object is first created.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
	}

	protected abstract Map<OTU, S> getOTUsToSeqeuencesModifiable();

	/**
	 * Get a possibly unmodifiable view of the constituent sequences in {@code
	 * getOTUOrdering()} order.
	 * 
	 * @return the constituent sequences
	 */
	public List<S> getSequences() {
		final ImmutableSet<OTU> otuOrderingAsSet = ImmutableSet
				.copyOf(getOTUOrdering());
		checkState(otuOrderingAsSet.equals(getOTUSet().getOTUs()),
				"otu ordering is not in sync with this sequence's OTUSet");

		final List<S> rows = newArrayList();
		for (final OTU otu : getOTUOrdering()) {
			rows.add(getOTUsToSeqeuencesModifiable().get(otu));
		}
		return Collections.unmodifiableList(rows);
	}

	/**
	 * Created for JAXB.
	 * 
	 * @return a modifiable reference to this set's sequences
	 */
	@XmlElement(name = "sequence")
	protected abstract Set<S> getSequencesModifiable();

	@CheckForNull
	public abstract S putRow(final OTU otu, final S newSequence);

	/**
	 * Set row at <code>otu</code> to <code>row</code>.
	 * <p>
	 * Assumes {@code newSequence} does not belong to another matrix.
	 * <p>
	 * Assumes {@code newSequence} is not detached.
	 * 
	 * @param otu index of the row we are adding
	 * @param newSequence the row we're adding
	 * 
	 * @return the row that was previously there, or {@code null} if there was
	 *         no row previously there
	 * 
	 * @throw IllegalArgumentException if {@code otu} does not belong to this
	 *        matrix's {@code OTUSet}
	 */
	@CheckForNull
	protected S putRowHelper(final OTU otu, final S newSequence) {
		checkNotNull(otu);
		checkNotNull(newSequence);
		checkArgument(getOTUSet().getOTUs().contains(otu),
				"otu does not belong to this sequence set");

		final S originalSequence = getOTUsToSeqeuencesModifiable().put(otu,
				newSequence);
		if (newSequence.equals(originalSequence)) {

		} else {
			if (originalSequence != null) {
				originalSequence.setSequenceSet(null);
			}

			setInNeedOfNewPPodVersionInfo();
		}
		return originalSequence;
	}

	@Override
	public MolecularSequenceSet<S> setInNeedOfNewPPodVersionInfo() {
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
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
	protected MolecularSequenceSet<S> setOtuSet(@Nullable final OTUSet newOTUSet) {
		otuSet = newOTUSet;
		return this;
	}

	public abstract Set<S> setSequences(final Set<S> newSequences);

	protected Set<S> setSequencesHelper(final Set<S> newSequences) {
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

		setInNeedOfNewPPodVersionInfo();
		return removedSequences;
	}

}
