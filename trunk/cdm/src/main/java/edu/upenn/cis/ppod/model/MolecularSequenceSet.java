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

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import edu.upenn.cis.ppod.modelinterfaces.IWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link MolecularSequence}s, all of which are the same length.
 * 
 * @author Sam Donnelly
 * 
 * @param <S> the type of {@code MolecularSequence} this set contains
 */
@MappedSuperclass
public abstract class MolecularSequenceSet<S extends MolecularSequence<?>>
		extends UUPPodEntity implements IWithOTUSet {

	static final String TABLE = "MOLECULAR_SEQUENCE_SET";

	@Column(name = "LABEL", nullable = false)
	private String label;

	@ManyToOne
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	@Override
	public void accept(final IVisitor visitor) {
		getOTUsToSequences().accept(visitor);
		for (final S sequence : getOTUsToSequences().getOTUsToValuesReference()
				.values()) {
			sequence.accept(visitor);
		}
		super.accept(visitor);
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@OverridingMethodsMustInvokeSuper
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		setOTUSet((OTUSet) parent);
	}

	@XmlAttribute(name = "label")
	public String getLabel() {
		return label;
	}

	/**
	 * Getter. Will be {@code null} when object is first created.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
	}

	protected abstract OTUKeyedBimap<S, ?> getOTUsToSequences();

	/**
	 * 
	 * @param otu
	 * @return
	 * 
	 * @throws IllegalArgument Exception if {@code otu} does not belong to this
	 *             sequence's {@code OTUSet}
	 */
	@Nullable
	public abstract S getSequence(final OTU otu);

	/**
	 * Get a copy of the constituent sequences in {@code getOTUOrdering()}
	 * order.
	 * 
	 * @return the constituent sequences
	 */
	public List<S> getSequences() {
		return getOTUsToSequences().getValuesInOTUOrder(getOTUSet());
	}

	/**
	 * @param otu
	 * @param newSequence
	 * @return
	 * 
	 * @throws IllegalArgumentException if this sequence set already contains
	 *             sequences and {@code newSequence.getSequence()} does not have
	 *             the same length as those sequences
	 */
	@CheckForNull
	public S putSequence(final OTU otu, final S sequence) {
		checkNotNull(otu);
		checkNotNull(sequence);
		checkArgument(sequence.getSequence() != null,
				"non-nullable value sequence.getSequence() is null");
		if (getSequences().size() > 0) {
			checkArgument(
					getSequences().get(0).getSequence().length() == sequence
							.getSequence().length(),
					"newSequence has incorrect length. It needs to be " +
							getSequences().get(0).getSequence().length()
							+ ", but it is "
							+ sequence.getSequence().length());
		}
		return putSequenceHelper(otu, sequence);
	}

	protected abstract S putSequenceHelper(final OTU otu, final S newSequence);

	@Override
	public MolecularSequenceSet<S> setInNeedOfNewPPodVersionInfo() {
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	public MolecularSequenceSet<S> setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewPPodVersionInfo();
		}
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
	protected final MolecularSequenceSet<S> setOTUSet(
			@Nullable final OTUSet newOTUSet) {
		otuSet = newOTUSet;
		return this;
	}

}
