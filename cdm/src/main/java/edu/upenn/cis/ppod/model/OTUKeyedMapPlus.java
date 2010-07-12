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
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.xml.bind.Unmarshaller;

import edu.upenn.cis.ppod.modelinterfaces.IOTUKeyedMapPlus;
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
public class OTUKeyedMapPlus<V extends IOTUKeyedMapValue<P>, P extends IVersionedWithOTUSet, OP extends OTUSomethingPair<V>>
		implements IOTUKeyedMapPlus<V, P, OP> {

	private P parent;

	private Map<OTU, V> values = newHashMap();

	private final Set<OP> otuSomethingPairs = newHashSet();

	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		for (final V value : getValues().values()) {
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
		for (final OP otuValuePair : getOTUSomethingPairs()) {
			put(otuValuePair.getFirst(), otuValuePair.getSecond());
		}
		getOTUSomethingPairs().clear();
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		checkNotNull(parent);

		@SuppressWarnings("unchecked")
		final P parentAsP = (P) parent;
		setParent(parentAsP);

		for (final OP otuSomethingPair : getOTUSomethingPairs()) {
			otuSomethingPair.getSecond().setParent(getParent());
		}

	}

	public OTUKeyedMapPlus<V, P, OP> clear() {
		if (getValues().size() == 0) {
			return this;
		}
		getValues().clear();
		setInNeedOfNewVersion();
		return this;
	}

	/** {@code inheritDoc} */
	@Nullable
	public V get(final OTU otu) {
		checkNotNull(otu);
		checkArgument(getValues().keySet().contains(otu),
				"otu is not in the keys");
		return getValues().get(otu);
	}

	public Set<OP> getOTUSomethingPairs() {
		return otuSomethingPairs;
	}

	/**
	 * Get the object set that owns this map. This will only be {@code null}
	 * when first created until the parent is assigned in the implementing
	 * class. This will never be {@code null} for a persistent object.
	 */
	@Nullable
	public P getParent() {
		return parent;
	}

	/**
	 * Get the {@code OTU}-keyed values.
	 * 
	 * @return the {@code OTU}-keyed values
	 */
	public Map<OTU, V> getValues() {
		return values;
	}

	@CheckForNull
	public V put(final OTU key, final V value) {
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

		if (null != getValues().get(key)
				&& getValues().get(key).equals(value)) {
			return getValues().get(key);
		}
		checkArgument(!getValues().containsValue(value),
				"already has a value .equals() to newT: " + value);
		final V originalValue = getValues().put(key, value);

		// If we are replacing an OTU's sequence, we need to sever the previous
		// sequence's sequence->sequenceSet pointer.
		if (originalValue != null && !originalValue.equals(value)) {
			originalValue.setParent(null);
		}
		value.setParent(getParent());
		setInNeedOfNewVersion();
		return originalValue;
	}

	private void setInNeedOfNewVersion() {
		if (getParent() != null) {
			getParent().setInNeedOfNewVersion();
		}
	}

	public OTUKeyedMapPlus<V, P, OP> setOTUs() {
		final IVersionedWithOTUSet parent = getParent();

		final Set<OTU> otusToBeRemoved = newHashSet();
		for (final OTU otu : getValues().keySet()) {
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
				value.setParent(null);
			}
		}

		getValues()
				.keySet()
				.removeAll(otusToBeRemoved);

		if (getParent().getOTUSet() != null) {
			for (final OTU otu : parent.getOTUSet().getOTUs()) {
				if (getValues().containsKey(otu)) {

				} else {
					getValues().put(otu, null);
					parent.setInNeedOfNewVersion();
				}
			}
		}
		return this;
	}

	public OTUKeyedMapPlus<V, P, OP> setParent(final P parent) {
		checkNotNull(parent);
		this.parent = parent;
		return this;
	}

	public OTUKeyedMapPlus<V, P, OP> setValues(final Map<OTU, V> values) {
		this.values = values;
		return this;
	}
}
