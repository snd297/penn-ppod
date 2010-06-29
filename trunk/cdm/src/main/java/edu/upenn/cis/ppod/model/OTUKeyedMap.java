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
import javax.persistence.MappedSuperclass;

import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapValue;
import edu.upenn.cis.ppod.modelinterfaces.IVersionedWithOTUSet;
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
@MappedSuperclass
public abstract class OTUKeyedMap<V extends IOTUKeyedMapValue>
		extends PersistentObject {

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visit(this);
		for (final V value : getOTUsToValues().values()) {
			if (value != null) {
				value.accept(visitor);
			}
		}
	}

	/**
	 * Take care of work that can only be done after {@link XmlIDREF}s have been
	 * resolved.
	 */
	public void afterUnmarshal() {
		for (final OTUSomethingPair<V> otuValuePair : getOTUValuePairs()) {
			put(otuValuePair.getFirst(), otuValuePair.getSecond());
		}
		getOTUValuePairs().clear();
	}

	public OTUKeyedMap<V> clear() {
		if (getOTUsToValues().size() == 0) {
			return this;
		}
		getOTUsToValues().clear();
		setInNeedOfNewVersion();
		return this;
	}

	@Nullable
	public V get(final OTU otu) {
		checkNotNull(otu);
		checkArgument(getOTUsToValues().keySet().contains(otu),
				"otu is not in the keys");
		return getOTUsToValues().get(otu);
	}

	/**
	 * Get the {@code OTU}-keyed values.
	 * 
	 * @return the {@code OTU}-keyed values
	 */
	protected abstract Map<OTU, V> getOTUsToValues();

	protected abstract Set<OTUSomethingPair<V>> getOTUValuePairs();

	/**
	 * Get the object set that owns this map. This will only be {@code null}
	 * when first created until the parent is assigned in the implementing
	 * class. This will never be {@code null} for a persistent object.
	 */
	@Nullable
	protected abstract IVersionedWithOTUSet getParent();

	public List<V> getValuesInOTUSetOrder() {
		if (getParent() == null) {
			throw new IllegalStateException(
					"parent is not set, so there is no OTU ordering to use");
		}
		final OTUSet parentOTUSet = getParent().getOTUSet();
		final List<V> valuesInOTUOrder = newArrayListWithCapacity(getOTUsToValues()
				.values().size());
		for (final OTU otu : parentOTUSet.getOTUs()) {
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
	 * This method calls {@code getParent().setInNeedOfNewVersionInfo()} if this
	 * method changes anything
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
	 * @throws IllegalStateException if {@link #getParent() == null}
	 * @throws IllegalStateException if {@code getParent().getOTUSet() == null}
	 * @throws IllegalArgumentException if {@code otu} does not belong to
	 *             {@code parent.getOTUSet()}
	 * @throws IllegalArgumentException if this already a value {@code .equals}
	 *             to {@code newT}
	 */
	@CheckForNull
	public abstract V put(final OTU key, final V value);

	/**
	 * Associates {@code value} with {@code key} in this map. If the map
	 * previously contained a mapping for {@code key}, the original value is
	 * replaced by the specified value.
	 * <p>
	 * This method calls {@code getParent().setInNeedOfNewVersionInfo()} if this
	 * method changes anything
	 * 
	 * @param key key
	 * @param newValue new value for {@code key}
	 * @param parent the owning object
	 * 
	 * @return the previous value associated with <tt>otu</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>otu</tt>
	 * 
	 * @throws IllegalStateException if {@link #getParent() == null}
	 * @throws IllegalStateException if {@code getParent().getOTUSet() == null}
	 * @throws IllegalArgumentException if {@code otu} does not belong to
	 *             {@code parent.getOTUSet()}
	 * @throws IllegalArgumentException if there's already a value
	 *             {@code .equals} to {@code value}
	 */
	@CheckForNull
	protected V putHelper(final OTU key, final V value) {
		checkNotNull(key);
		checkNotNull(value);
		checkState(getParent() != null, "no parent has been assigned");
		checkState(getParent().getOTUSet() != null,
				"parent.getOTUSet() == null");
		checkArgument(
				contains(
						getParent()
								.getOTUSet()
								.getOTUs(),
								key),
				"otu does not belong to the parent's OTUSet");

		if (null != getOTUsToValues().get(key)
				&& getOTUsToValues().get(key).equals(value)) {
			return getOTUsToValues().get(key);
		}
		checkArgument(!getOTUsToValues().containsValue(value),
				"already has a value .equals() to newT: " + value);
		getParent().setInNeedOfNewVersion();
		final V originalValue = getOTUsToValues().put(key, value);

		// If we are replacing an OTU's sequence, we need to sever the previous
		// sequence's sequence->sequenceSet pointer.
		if (originalValue != null && !originalValue.equals(value)) {
			originalValue.unsetOTUKeyedMap();
		}
		return originalValue;
	}

	protected abstract OTUKeyedMap<V> setInNeedOfNewVersion();

	/**
	 * Set the keys of this {@code OTUKeyedMap} to the OTU's in
	 * {@code getParent()}'s OTU set.
	 * <p>
	 * Any newly introduced keys will map to {@code null} values.
	 * 
	 * @return this
	 */
	@CheckForNull
	protected OTUKeyedMap<V> setOTUs() {
		final IVersionedWithOTUSet parent = getParent();

		final Set<OTU> otusToBeRemoved = newHashSet();
		for (final OTU otu : getOTUsToValues().keySet()) {
			if (parent.getOTUSet() != null
					&& contains(parent
									.getOTUSet()
									.getOTUs(), otu)) {
				// it stays
			} else {
				otusToBeRemoved.add(otu);
				parent.isInNeedOfNewVersion();
			}
		}

		for (final OTU otuToBeRemoved : otusToBeRemoved) {
			final V value = get(otuToBeRemoved);
			if (value != null) {
				value.unsetOTUKeyedMap();
			}
		}

		getOTUsToValues()
				.keySet()
				.removeAll(otusToBeRemoved);

		if (getParent().getOTUSet() != null) {
			for (final OTU otu : parent.getOTUSet().getOTUs()) {
				if (getOTUsToValues().containsKey(otu)) {

				} else {
					getOTUsToValues().put(otu, null);
					parent.setInNeedOfNewVersion();
				}
			}
		}
		return this;
	}
}
