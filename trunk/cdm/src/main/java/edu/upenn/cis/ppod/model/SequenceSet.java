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
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import edu.upenn.cis.ppod.modelinterfaces.IVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link Sequence}s.
 * 
 * @author Sam Donnelly
 * 
 * @param <S> the type of {@code Sequence} this set contains
 */
@MappedSuperclass
public abstract class SequenceSet<S extends Sequence>
		extends UUPPodEntityWithXmlId
		implements IVersionedWithOTUSet {

	@Column(name = "LABEL", nullable = false)
	private String label;

	@CheckForNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OTUSet.JOIN_COLUMN)
	private OTUSet otuSet;

	protected SequenceSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		getOTUKeyedSequences().accept(visitor);
		super.accept(visitor);
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@OverridingMethodsMustInvokeSuper
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		setOTUSet((OTUSet) parent);
	}

	protected void checkSequenceLength(final S sequence) {
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

	/**
	 * Remove all sequences from this set. Null's out the
	 * {@code Sequence->SequenceSet} relationship and sets
	 * {@code getSequenceLengths()} to {@code null}.
	 * 
	 * @return this
	 */
	public abstract SequenceSet<S> clearSequences();

	@XmlAttribute(name = "label")
	public String getLabel() {
		return label;
	}

	protected abstract OTUKeyedMap<S> getOTUKeyedSequences();

	/**
	 * Getter. Will be {@code null} when the sequence set is not connected to an
	 * OTU set.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
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
	public abstract S getSequence(final OTU otu);

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
				.getOTUsToValues()
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
	public abstract Map<OTU, S> getSequences();

	/**
	 * 
	 * 
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
	public abstract S putSequence(final OTU otu, final S sequence);

	@Override
	public SequenceSet<S> setInNeedOfNewVersion() {
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
		return this;
	}

	public SequenceSet<S> setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	/**
	 * Set the owning {@code OTUSet}.
	 * <p>
	 * A {@code null} value for {@code newOTUSet} indicates we're severing the
	 * relationship.
	 * <p>
	 * Intentionally package-private.
	 * 
	 * @param otuSet the OTU set that will own this sequence set
	 * 
	 * @return this sequence set
	 */
	SequenceSet<S> setOTUSet(
			@CheckForNull final OTUSet otuSet) {
		this.otuSet = otuSet;
		setOTUs();
		return this;
	}

	protected SequenceSet<S> setOTUs() {
		checkState(getOTUKeyedSequences() != null,
					"getOTUKeyedSequences() == null, "
							+ "so there are no sequences to operate on");

		getOTUKeyedSequences().setOTUs();
		return this;
	}

}
