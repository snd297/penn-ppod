/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;

/**
 * Project wide utilities.
 * 
 * @author Sam Donnelly
 */
public class UPennCisPPodUtil {

	public static <F, T> Predicate<F> composeEqualTo(
			final T equalToTarget,
			final Function<F, ? extends T> f) {
		return compose(equalTo(equalToTarget), f);
	}

	public static <V extends IChild<P>, P extends IChild<OtuSet>>
			void put(
					final Map<Otu, V> otusToValues,
					final Otu otu,
					final V value,
					final P parent) {
		checkNotNull(otu);
		checkNotNull(value);

		final OtuSet grandParent = parent.getParent();

		checkState(grandParent != null);
		checkState(grandParent.getOtus().contains(otu),
				"grandparent otuset does not contain otu");

		final V oldValue = otusToValues.put(otu, value);

		value.setParent(parent);

		if (value != oldValue && oldValue != null) {
			oldValue.setParent(null);
		}
	}

	public static <C extends IChild<?>> void updateOtus(
			@CheckForNull final OtuSet parent,
			final Map<Otu, C> rows) {

		final Set<Otu> otusToBeRemoved = newHashSet();

		for (final Otu otu : rows.keySet()) {
			if (parent != null && contains(parent.getOtus(), otu)) {
				// it stays
			} else {
				otusToBeRemoved.add(otu);
			}
		}

		for (final Otu otuToBeRemoved : otusToBeRemoved) {
			final C row = rows.get(otuToBeRemoved);
			if (row != null) {
				row.setParent(null);
			}
		}

		rows.keySet().removeAll(otusToBeRemoved);

		if (parent != null) {
			for (final Otu otu : parent.getOtus()) {
				if (rows.containsKey(otu)) {

				} else {
					rows.put(otu, null);
				}
			}
		}
	}

	/**
	 * Prevent inheritance and instantiation.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	private UPennCisPPodUtil() {
		throw new AssertionError();
	}
}
