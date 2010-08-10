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

import org.hibernate.annotations.Target;

import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUKeyedMap;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.ISequenceSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link Sequence}s.
 * 
 * @author Sam Donnelly
 * 
 * @param <S> the type of {@code Sequence} this set contains
 */
/**
 * @author Sam Donnelly
 * 
 * @param <S>
 */
@MappedSuperclass
abstract class SequenceSet<S extends Sequence<?>>
		extends UUPPodEntityWithDocId
		implements ISequenceSet<S> {

	@Column(name = "LABEL", nullable = false)
	private String label;

	@CheckForNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = OTUSet.JOIN_COLUMN)
	@Target(OTUSet.class)
	private IOTUSet parent;

	SequenceSet() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		getOTUKeyedSequences().accept(visitor);
		super.accept(visitor);
	}

	public void afterUnmarshal() {
		getOTUKeyedSequences().afterUnmarshal();
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@OverridingMethodsMustInvokeSuper
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		checkNotNull(parent);
		setParent((OTUSet) parent);
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

	@XmlAttribute(name = "label")
	public String getLabel() {
		return label;
	}

	abstract IOTUKeyedMap<S> getOTUKeyedSequences();

	/** {@inheritDoc} */
	@Nullable
	public IOTUSet getParent() {
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
	public abstract S getSequence(final IOTU otu);

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
	public abstract Map<IOTU, S> getSequences();

	/**
	 * {@inheritDoc}
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
	public abstract S putSequence(final IOTU otu, final S sequence);

	@Override
	public void setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/** {@inheritDoc} */
	public ISequenceSet<S> setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
		return this;
	}

	void setOTUs() {
		checkState(getOTUKeyedSequences() != null,
					"getOTUKeyedSequences() == null, "
							+ "so there are no sequences to operate on");

		getOTUKeyedSequences().setOTUs();
	}

	/** {@inheritDoc} */
	public void setParent(
			@CheckForNull final IOTUSet parent) {
		this.parent = parent;
		setOTUs();
	}

}
