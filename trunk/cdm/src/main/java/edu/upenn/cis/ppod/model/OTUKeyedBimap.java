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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import edu.upenn.cis.ppod.modelinterfaces.IWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@MappedSuperclass
public abstract class OTUKeyedBimap<T extends PersistentObject, P extends IWithOTUSet>
		extends PersistentObject {

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	@Override
	public OTUKeyedBimap<T, P> accept(final IVisitor visitor) {
		visitor.visit(this);
		for (final T t : getOTUsToValuesModifiable().values()) {
			t.accept(visitor);
		}
		return this;
	}

	public void afterUnmarshal() {
		for (final OTUSomethingPair<T> otuValuePair : getOTUValuePairs()) {
			getOTUsToValuesModifiable().put(otuValuePair.getFirst(),
					otuValuePair.getSecond());
		}
	}

	public T get(final OTU otu, final P parent) {
		checkArgument(parent.getOTUSet().getOTUs().contains(otu),
				"otu does not belong to parent's OTUSet");
		return getOTUsToValuesModifiable().get(otu);
	}

	/**
	 * Get the {@code OTU}-keyed items.
	 * 
	 * @return the {@code OTU}-keyed items
	 */
	protected abstract Map<OTU, T> getOTUsToValuesModifiable();

	protected abstract Set<OTUSomethingPair<T>> getOTUValuePairs();

	public List<T> getValuesInOTUOrder(final OTUSet otuSet) {
		final List<T> valuesInOTUOrder = newArrayList();
		for (final OTU otu : otuSet.getOTUs()) {
			valuesInOTUOrder.add(getOTUsToValuesModifiable().get(otu));
		}
		return valuesInOTUOrder;
	}

	public abstract T put(OTU otu, T newT, P parent);

	protected T putHelper(final OTU otu, final T newT, final P parent) {
		checkNotNull(otu);
		checkNotNull(newT);
		checkArgument(parent.getOTUSet().getOTUs().contains(otu),
				"otu does not belong to the parent's OTUSet");

		if (null != getOTUsToValuesModifiable().get(otu)
				&& getOTUsToValuesModifiable().get(otu).equals(newT)) {
			return getOTUsToValuesModifiable().get(otu);
		}
		checkArgument(!getOTUsToValuesModifiable().containsValue(newT),
				"already has a value .equals() to newT: " + newT);

		parent.setInNeedOfNewPPodVersionInfo();
		return getOTUsToValuesModifiable().put(otu, newT);
	}

	/**
	 * Set the keys of this {@code OTUKeyedBimap}.
	 * <p>
	 * Any new keys will be given {@code null} values.
	 * <p>
	 * Any existing keys will be removed if they are not present in {@code
	 * otuSet.getOTUs()}.
	 * 
	 * @param otuSet must be {@code ==} to {@code parent.getOTUSet()} - only
	 *            included to emphasize that {@code null} is allowed
	 * @param parent owner of this bimap
	 * 
	 * @return this
	 * 
	 * @throw IllegalArgumentException if {@code otuSet != parent.getOTUSet()}
	 */
	protected OTUKeyedBimap<T, P> setOTUs(@Nullable final OTUSet otuSet,
			final P parent) {
		checkArgument(otuSet == parent.getOTUSet(),
				"otuSet does not belong to parent");
		final Set<OTU> otusToBeRemoved = newHashSet();
		for (final OTU otu : getOTUsToValuesModifiable().keySet()) {
			if (parent.getOTUSet() != null
					&& parent.getOTUSet().getOTUs().contains(otu)) {
				// it stays
			} else {
				otusToBeRemoved.add(otu);
				parent.isInNeedOfNewPPodVersionInfo();
			}
		}

		getOTUsToValuesModifiable().keySet().removeAll(otusToBeRemoved);

		if (otuSet != null) {
			for (final OTU otu : parent.getOTUSet().getOTUs()) {
				if (getOTUsToValuesModifiable().containsKey(otu)) {

				} else {
					getOTUsToValuesModifiable().put(otu, null);
				}
			}
		}
		parent.setInNeedOfNewPPodVersionInfo();
		return this;
	}
}
