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

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import edu.upenn.cis.ppod.imodel.IDependsOnParentOtus;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link ISequence}s.
 * 
 * @author Sam Donnelly
 * 
 * @param <S> the type of {@code Sequence} this set contains
 */
@MappedSuperclass
public abstract class SequenceSet<S extends Sequence<?>>
		extends UuPPodEntity
		implements IDependsOnParentOtus {

	@Nullable
	@Column(name = "LABEL", nullable = false)
	private String label;

	@Nullable
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OtuSet.JOIN_COLUMN, insertable = false,
				updatable = false)
	private OtuSet parent;

	SequenceSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		getOTUKeyedSequences().accept(visitor);
		super.accept(visitor);
	}

	void checkSequenceLength(final S sequence) {
		checkNotNull(sequence);
		checkArgument(
				sequence.getSequence() != null,
				"sequence.getSequence() is null");

		final Integer sequencesLength = getSequenceLengths();
		if (sequencesLength == null) {
			return;
		}

		if (sequence.getSequence().length() != sequencesLength) {
			throw new IllegalArgumentException(
							"sequence.getSequence().length must be "
									+ sequencesLength
									+ ", but it is "
									+ sequence.getSequence().length());
		}
	}

	public String getLabel() {
		return label;
	}

	abstract IOtuKeyedMap<S> getOTUKeyedSequences();

	/** {@inheritDoc} */
	@Nullable
	public OtuSet getParent() {
		return parent;
	}

	/**
	 * Get the sequence indexed by {@code otu}.
	 * 
	 * @param otu index
	 * 
	 * @return the sequence at {@code otu}
	 * 
	 * @throws IllegalArgument Exception if {@code otu} does not belong to this
	 *             sequence's {@code OTUSet}
	 */
	@Nullable
	public abstract S getSequence(final Otu otu);

	/**
	 * Get the length of the sequences in this set, or {@code null} if no
	 * sequences have been added to this set.
	 * 
	 * @return the length of the sequences in this set, or {@code null} if no
	 *         sequences have been added to this set
	 */
	@CheckForNull
	public Integer getSequenceLengths() {
		for (final S sequenceInThisSet : getOTUKeyedSequences()
				.getValues()
				.values()) {
			if (sequenceInThisSet != null) {
				return sequenceInThisSet.getSequence().length();
			}
		}
		return null;
	}

	/**
	 * Get a map which contains the {@code OTU, S} entries of this sequence set.
	 * <p>
	 * The returned value may be an unmodifiable view of this set's values, so
	 * the client should make a copy of the returned value if desired.
	 * 
	 * @return a map which contains the {@code OTU, S} entries of this sequence
	 *         set
	 */
	public abstract Map<Otu, S> getSequences();

	/**
	 * @param otu
	 * @param sequence
	 * 
	 * @throws IllegalArgumentException if {@code sequence.getSequence() ==
	 *             null}
	 * @throws IllegalArgumentException if
	 *             {@code sequence.getSequence().length() != this.getLength()}
	 * @return
	 */
	@CheckForNull
	public abstract S putSequence(final Otu otu, final S sequence);

	@Override
	public void setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	public void setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final OtuSet parent) {
		this.parent = parent;
		updateOtus();
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		checkState(getOTUKeyedSequences() != null,
					"getOTUKeyedSequences() == null, "
							+ "so there are no sequences to operate on");

		getOTUKeyedSequences().updateOtus();
	}
}
