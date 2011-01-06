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
import javax.xml.bind.Marshaller;

import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.imodel.IOtuKeyedMapPlus;
import edu.upenn.cis.ppod.util.IVisitor;
import edu.upenn.cis.ppod.util.OtuKeyedPair;

/**
 * An {@code IOtu -> PersistentObject} map with {@code equals}-unique values.
 * <p>
 * Even though this object is guaranteed to have unique values, we don't call it
 * a bidirectional map because it doesn't support an inverse view. It will
 * always be safe however to, for example, call {@link HashBiMap#create(Map)} on
 * {@link #getOtusKeyedPairs()}.
 * 
 * @author Sam Donnelly
 */
public class OtuKeyedMapPlus<V extends IChild<P>, P extends IChild<OtuSet>, OP extends OtuKeyedPair<V>>
		implements IOtuKeyedMapPlus<V, P, OP> {

	private P parent;

	private Map<Otu, V> values = newHashMap();

	private final Set<OP> otuSomethingPairs = newHashSet();

	OtuKeyedMapPlus() {}

	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		for (final V value : getValues().values()) {
			if (value != null) {
				value.accept(visitor);
			}
		}
	}

	public boolean afterMarshal(@CheckForNull final Marshaller marshaller) {
		otuSomethingPairs.clear();
		return true;
	}

	/**
	 * Take care of work that can only be done after {@link XmlIDREF}s have been
	 * resolved.
	 */
	public void afterUnmarshal() {
		for (final OP otuValuePair : getOtuKeyedPairs()) {
			put(otuValuePair.getFirst(), otuValuePair.getSecond());
		}
		getOtuKeyedPairs().clear();
	}

	public void afterUnmarshal(final P parent) {
		checkNotNull(parent);

		setParent(parent);

		for (final OP otuSomethingPair : getOtuKeyedPairs()) {
			otuSomethingPair.getSecond().setParent(getParent());
		}

	}

	/** {@inheritDoc} */
	public void clear() {
		if (getValues().size() == 0) {

		} else {
			getValues().clear();
			setInNeedOfNewVersion();
		}
	}

	/** {@code inheritDoc} */
	@Nullable
	public V get(final Otu otu) {
		checkNotNull(otu);
		checkArgument(getValues().keySet().contains(otu),
				"otu is not in the keys");
		return getValues().get(otu);
	}

	public Set<OP> getOtuKeyedPairs() {
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

	/** {@inheritDoc} */
	public Map<Otu, V> getValues() {
		return values;
	}

	@CheckForNull
	public V put(final Otu key, final V value) {
		checkNotNull(key);
		checkNotNull(value);
		checkState(getParent() != null, "no parent has been assigned");
		checkState(getParent().getParent() != null,
				"parent " + getParent().getClass().getSimpleName()
						+ " has not been added to an OTUSet");
		checkArgument(
				contains(
						getParent()
								.getParent()
								.getOtus(),
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

	/** {@inheritDoc} */
	public void setParent(final P parent) {
		checkNotNull(parent);
		this.parent = parent;
	}

	/** {@inheritDoc} */
	public void setValues(final Map<Otu, V> values) {
		this.values = values;
	}

	/** {@inheritDoc}  */
	public void updateOtus() {
		final IChild<OtuSet> parent = getParent();

		final Set<Otu> otusToBeRemoved = newHashSet();
		for (final Otu otu : getValues().keySet()) {
			if (parent.getParent() != null
					&& contains(parent
									.getParent()
									.getOtus(), otu)) {
				// it stays
			} else {
				otusToBeRemoved.add(otu);
				parent.isInNeedOfNewVersion();
			}
		}

		for (final Otu otuToBeRemoved : otusToBeRemoved) {
			final V value = get(otuToBeRemoved);
			if (value != null) {
				value.setParent(null);
			}
		}

		getValues()
				.keySet()
				.removeAll(otusToBeRemoved);

		if (getParent().getParent() != null) {
			for (final Otu otu : parent.getParent().getOtus()) {
				if (getValues().containsKey(otu)) {

				} else {
					getValues().put(otu, null);
					parent.setInNeedOfNewVersion();
				}
			}
		}
	}
}
