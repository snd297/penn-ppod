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
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OTUSomethingPair;

/**
 * An {@code OTU -> PersistentObject} map with {@code equals}-unique values.
 * <p>
 * Even though this object is guaranteed to have unique values, we don't call it
 * a bidirectional map because it doesn't support an inverse view. It will
 * always be safe however to, for example, call {@link HashBiMap#create(Map)} on
 * {@link #getOTUsToValues()}.
 * 
 * @author Sam Donnelly
 */
public abstract class OTUKeyedMap<V extends PersistentObject>
		extends PersistentObject {

	static final String OTU_IDX_COLUMN = "OTU_IDX";

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
	}

	public void afterUnmarshal() {
		for (final OTUSomethingPair<V> otuValuePair : getOTUValuePairs()) {
			getOTUsToValues().put(otuValuePair.getFirst(),
					otuValuePair.getSecond());
		}
	}

	public OTUKeyedMap<V> clear() {
		if (getOTUsToValues().size() == 0) {
			return this;
		}
		getOTUsToValues().clear();
		setInNeedOfNewPPodVersionInfo();
		return this;
	}

	public V get(final OTU otu) {
		checkArgument(contains(getParent().getOTUSet(), otu),
				"otu does not belong to parent's OTUSet");
		return getOTUsToValues().get(otu);
	}

	/**
	 * Get the {@code OTU}-keyed items.
	 * 
	 * @return the {@code OTU}-keyed items
	 */
	protected abstract Map<OTU, V> getOTUsToValues();

	protected abstract Set<OTUSomethingPair<V>> getOTUValuePairs();

	protected abstract IPPodVersionedWithOTUSet getParent();

	public List<V> getValuesInOTUOrder(final OTUSet otuSet) {
		final List<V> valuesInOTUOrder = newArrayListWithCapacity(getOTUsToValues()
				.values().size());
		for (final OTU otu : otuSet) {
			if (getOTUsToValues().containsKey(otu)) {
				valuesInOTUOrder.add(getOTUsToValues().get(otu));
			}
		}
		return valuesInOTUOrder;
	}

	/**
	 * Associates {@code value} with {@code key} in this map. If the map
	 * previously contained a mapping for {@code key}, the original value is
	 * replaced by the specified value.
	 * <p>
	 * This method calls {@code getParent().setInNeedOfNewPPodVersionInfo()} if
	 * this method changes anything
	 * 
	 * @param key key
	 * @param newValue new value for {@code key}
	 * @param parent the owning object
	 * 
	 * @return the previous value associated with <tt>otu</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>otu</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>.)
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to
	 *             {@code parent.getOTUSet()}
	 * @throws IllegalArgumentException if there's already a value {@code
	 *             .equals} to {@code newT}
	 */
	@CheckForNull
	public abstract V put(OTU key, V value);

	/**
	 * Associates {@code value} with {@code key} in this map. If the map
	 * previously contained a mapping for {@code key}, the old value is replaced
	 * by the specified value.
	 * <p>
	 * This method calls {@code getParent.setInNeedOfNewPPodVersionInfo()} if it
	 * changes this {@code OTUKeyedMap}'s state.
	 * 
	 * @param otu key
	 * @param value value
	 * @param parent the owning object
	 * 
	 * @return the previous value associated with <tt>otu</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>otu</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>.)
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to
	 *             {@code parent.getOTUSet()}
	 * @throws IllegalArgumentException if there's already a value {@code
	 *             .equals} to {@code newT}
	 * @throws IllegalArgumentException if {@code parent.getOTUSet() == null}
	 */
	@CheckForNull
	protected V putHelper(final OTU key, final V value) {
		checkNotNull(key);
		checkNotNull(value);
		checkState(getParent() != null, "no parent has been assigned");
		checkState(getParent().getOTUSet() != null,
				"parent.getOTUSet() == null");
		checkArgument(contains(getParent().getOTUSet(), key),
				"otu does not belong to the parent's OTUSet");

		if (null != getOTUsToValues().get(key)
				&& getOTUsToValues().get(key).equals(value)) {
			return getOTUsToValues().get(key);
		}
		checkArgument(!getOTUsToValues().containsValue(value),
				"already has a value .equals() to newT: " + value);
		getParent().setInNeedOfNewPPodVersionInfo();
		return getOTUsToValues().put(key, value);
	}

	protected abstract OTUKeyedMap<V> setInNeedOfNewPPodVersionInfo();

	/**
	 * Set the keys of this {@code OTUKeyedMap}.
	 * <p>
	 * Any newly introduced keys will map to {@code null} values.
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
	@CheckForNull
	protected OTUKeyedMap<V> setOTUs(@Nullable final OTUSet otuSet) {
		final IPPodVersionedWithOTUSet parent = getParent();

		checkArgument(otuSet == parent.getOTUSet(),
				"otuSet does not belong to parent");

		final Set<OTU> otusToBeRemoved = newHashSet();
		for (final OTU otu : getOTUsToValues().keySet()) {
			if (parent.getOTUSet() != null
					&& contains(parent.getOTUSet(), otu)) {
				// it stays
			} else {
				otusToBeRemoved.add(otu);
				parent.isInNeedOfNewPPodVersionInfo();
			}
		}

		getOTUsToValues().keySet().removeAll(otusToBeRemoved);

		if (otuSet != null) {
			for (final OTU otu : parent.getOTUSet()) {
				if (getOTUsToValues().containsKey(otu)) {

				} else {
					getOTUsToValues().put(otu, null);
					parent.setInNeedOfNewPPodVersionInfo();
				}
			}
		}
		return this;
	}
}
