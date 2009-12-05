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

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Predicate;

/**
 * @author Sam Donnelly
 */
public class PPodIterables {

	/**
	 * Find the first member of {@code iterable} that satisfies {@code
	 * predicate}, or return {@code null} if there is no such element.
	 * 
	 * @param <T>
	 * @param iterable
	 * @param predicate
	 * @return
	 */
	public static <T> T findIf(final Iterable<T> iterable,
			final Predicate<? super T> predicate) {
		for (final Iterator<T> iterator = iterable.iterator(); iterator
				.hasNext();) {
			final T thisItem = iterator.next();
			if (predicate.apply(thisItem)) {
				return thisItem;
			}
		}
		return null;
	}

	public static <T> boolean removeIf(final Collection<T> collection,
			final Predicate<? super T> predicate) {
		final int preSize = collection.size();
		for (final Iterator<T> iterator = collection.iterator(); iterator
				.hasNext();) {
			try {
				if (predicate.apply(iterator.next())) {
					iterator.remove();
				}
			} catch (final UnsupportedOperationException e) {
				throw new IllegalArgumentException(
						"iterable.iterator doesn't support remove", e);
			}
		}
		return preSize == collection.size();
	}

	private PPodIterables() {
		throw new IllegalStateException("Can't instantiate a PPodIterables");
	}
}