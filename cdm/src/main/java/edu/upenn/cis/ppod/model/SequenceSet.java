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

import java.util.Iterator;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A set of {@link Sequence}s.
 * 
 * @author Sam Donnelly
 * 
 * @param <S> the type of {@code Sequence} this set contains
 */
@MappedSuperclass
public abstract class SequenceSet<S extends Sequence<?>>
		extends UUPPodEntityWXmlId implements IPPodVersionedWithOTUSet,
		Iterable<S> {

	static final String TABLE = "MOLECULAR_SEQUENCE_SET";

	@Column(name = "LABEL", nullable = false)
	private String label;

	@ManyToOne
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	public abstract SequenceSet<S> clear();

	@Override
	public void accept(final IVisitor visitor) {
		getOTUsToSequences().accept(visitor);
		for (final S sequence : getOTUsToSequences().getOTUsToValues()
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
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
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

	protected abstract OTUKeyedMap<S, ? extends SequenceSet<?>> getOTUsToSequences();

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
	 * Get the number of sequences in this set.
	 * 
	 * @return the number of sequences in this set.
	 */
	public int getSequencesSize() {
		return getOTUsToSequences().getOTUsToValues().size();
	}

	/**
	 * Iterates over the sequences in {@code getOTUSet().getOTUs()} order.
	 */
	public Iterator<S> iterator() {
		return getOTUsToSequences().getValuesInOTUOrder(getOTUSet()).iterator();
	}

	/**
	 * @param otu
	 * @param newSequence
	 * @return
	 */
	@CheckForNull
	public S putSequence(final OTU otu, final S sequence) {
		checkNotNull(otu);
		checkNotNull(sequence);
		return putSequenceHelper(otu, sequence);
	}

	@Nullable
	protected abstract S putSequenceHelper(final OTU otu, final S newSequence);

	@Override
	public SequenceSet<S> setInNeedOfNewPPodVersionInfo() {
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	public SequenceSet<S> setLabel(final String label) {
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
	SequenceSet<S> setOTUSet(
			@CheckForNull final OTUSet newOTUSet) {
		this.otuSet = newOTUSet;
		setOTUsInOTUsToSequences(newOTUSet);
		return this;
	}

	protected abstract SequenceSet<S> setOTUsInOTUsToSequences(
			@Nullable final OTUSet otuSet);

}
