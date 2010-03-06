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

import static com.google.common.base.Objects.equal;
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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
abstract class OTUKeyedMap<T, P extends PPodEntity> {

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	@ManyToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(name = OTU.ID_COLUMN))
	@org.hibernate.annotations.IndexColumn(name = OTU.TABLE + "_POSITION")
	private final List<OTU> otuOrdering = newArrayList();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	public Map<OTU, T> getItems() {
		return Collections.unmodifiableMap(getItemsModifiable());
	}

	public List<T> getItemsInOTUOrder() {
		final List<T> itemsInOTUOrder = newArrayList();
		for (final OTU otu : getOTUOrdering()) {
			itemsInOTUOrder.add(getItems().get(otu));
		}
		return itemsInOTUOrder;
	}

	/**
	 * Get the {@code OTU}-keyed items.
	 * 
	 * @return the {@code OTU}-keyed items
	 */
	protected abstract Map<OTU, T> getItemsModifiable();

	public List<OTU> getOTUOrdering() {
		return Collections.unmodifiableList(getOTUOrderingModifiable());
	}

	@XmlElement(name = "otuDocId")
	@XmlIDREF
	protected List<OTU> getOTUOrderingModifiable() {
		return otuOrdering;
	}

	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
	}

	public abstract P getParent();

	public T put(final OTU otu, final T newT) {
		checkNotNull(otu);
		checkNotNull(newT);
		checkArgument(getOTUSet().getOTUs().contains(otu),
				"otu does not belong to this matrix");
		final T previousT = getItemsModifiable().put(otu, newT);
		if (newT.equals(previousT)) {

		} else {
			getParent().resetPPodVersionInfo();
		}
		return previousT;
	}

	public OTUKeyedMap<T, P> setOTUOrdering(final List<OTU> newOTUs) {
		checkNotNull(newOTUs);
		final Set<OTU> newOTUsSet = newHashSet(newOTUs);
		checkState(getOTUSet() != null, "no OTUSet has been set");
		checkArgument(newOTUsSet.equals(getOTUSet().getOTUs()),
				"argument newOTUs does not have same OTU's as the assigned OTUSet");
		if (newOTUs.equals(getOTUOrdering())) {
			// They're the same, nothing to do
		} else {
			getOTUOrderingModifiable().clear();
			getOTUOrderingModifiable().addAll(newOTUs);
		}
		return this;
	}

	protected OTUKeyedMap<T, P> setOTUSet(@Nullable final OTUSet otuSet) {
		if (equal(this.otuSet, otuSet)) {
			// still the same
		} else {
			this.otuSet = otuSet;
			getParent().resetPPodVersionInfo();
		}
		return this;
	}

	protected abstract OTUKeyedMap<T, P> setParent(P owner);

}
