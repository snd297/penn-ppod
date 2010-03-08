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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.modelinterfaces.IWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@MappedSuperclass
abstract class OTUKeyedMap<T extends PersistentObject, P extends IWithOTUSet>
		extends PersistentObject {

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	@ManyToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.IndexColumn(name = OTU.TABLE + "_POSITION")
	private final List<OTU> otuOrdering = newArrayList();

	@Override
	public OTUKeyedMap<T, P> accept(final IVisitor visitor) {
		for (final T t : getOTUsToValuesModifiable().values()) {
			t.accept(visitor);
		}
		return this;
	}

	protected abstract Set<OTUSomethingPair<T>> getOTUValuePairsModifiable();

	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		for (final OTUSomethingPair<T> otuValuePair : getOTUValuePairsModifiable()) {
			getOTUsToValuesModifiable().put(otuValuePair.getFirst(),
					otuValuePair.getSecond());
		}
	}

	public List<OTU> getOTUOrdering() {
		return Collections.unmodifiableList(getOTUOrderingModifiable());
	}

	@XmlElementWrapper(name = "otuOrdering")
	@XmlElement(name = "otuDocId")
	@XmlIDREF
	protected List<OTU> getOTUOrderingModifiable() {
		return otuOrdering;
	}

	/**
	 * Get the {@code OTU}-keyed items.
	 * 
	 * @return the {@code OTU}-keyed items
	 */
	protected abstract Map<OTU, T> getOTUsToValuesModifiable();

	public List<T> getValuesInOTUOrder() {
		final List<T> valuesInOTUOrder = newArrayList();
		for (final OTU otu : getOTUOrdering()) {
			valuesInOTUOrder.add(getOTUsToValuesModifiable().get(otu));
		}
		return valuesInOTUOrder;
	}

	public abstract T put(OTU otu, T newT, P parent);

	protected T putHelper(final OTU otu, final T newT, final P parent) {
		checkNotNull(otu);
		checkNotNull(newT);
		checkArgument(parent.getOTUSet().getOTUs().contains(otu),
				"otu does not belong to this matrix");
		final T previousT = getOTUsToValuesModifiable().put(otu, newT);
		if (newT.equals(previousT)) {

		} else {
			parent.setInNeedOfNewPPodVersionInfo();
		}
		return previousT;
	}

	public OTUKeyedMap<T, P> setOTUOrdering(final List<OTU> newOTUs,
			final P parent) {
		checkNotNull(newOTUs);
		final ImmutableSet<OTU> newOTUsSet = ImmutableSet.copyOf(newOTUs);
		checkState(parent.getOTUSet() != null, "no OTUSet has been set");
		checkArgument(newOTUsSet.equals(parent.getOTUSet().getOTUs()),
				"argument newOTUs does not have same OTU's as the assigned OTUSet");
		if (newOTUs.equals(getOTUOrdering())) {
			// They're the same, nothing to do
		} else {
			getOTUOrderingModifiable().clear();
			getOTUOrderingModifiable().addAll(newOTUs);
			parent.setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	protected OTUKeyedMap<T, P> setOTUSet(@Nullable final OTUSet otuSet,
			P parent) {
		for (final OTU otu : getOTUsToValuesModifiable().keySet()) {
			if (!parent.getOTUSet().getOTUs().contains(otu)) {
				getOTUsToValuesModifiable().remove(otu);
			}
		}
		parent.setInNeedOfNewPPodVersionInfo();
		return this;
	}

}
