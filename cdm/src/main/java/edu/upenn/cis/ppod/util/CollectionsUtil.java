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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

/**
 * For easing work with collections.
 * 
 * @author Sam Donnelly
 */
public class CollectionsUtil {

	/**
	 * Prevent inheritance and instantiation.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	private CollectionsUtil() {
		throw new AssertionError("Can't instantiate a CollectionsUtil");
	}

	/**
	 * Create a new {@code newConcurrentHashMap()}.
	 * 
	 * @param <K> key type
	 * @param <V> value type
	 * 
	 * @return {@code new ConcurrentHashMap<K, V>()}
	 */
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap<K, V>();
	}

	/**
	 * Equivalent to {@code new Vector<T>()}.
	 * 
	 * @param <T> see {@link Vector#Vector()}.
	 * 
	 * @return {@code new Vector<T>()}
	 */
	public static <T> Vector<T> newVector() {
		return new Vector<T>();
	}

	public static <T> List<T> nullFillAndSet(final List<T> coll, final int i,
			@Nullable final T t) {
		checkNotNull(coll);
		checkArgument(i >= 0, "i must be non-negative");
		while (coll.size() < i + 1) {
			coll.add(null);
		}
		coll.set(i, t);
		return coll;
	}

	public static <T> Collection<T> nullFill(final Collection<T> coll,
			final int size) {
		checkNotNull(coll);
		checkArgument(size >= 0, "size must be non-negative");
		while (coll.size() < size) {
			coll.add(null);
		}
		return coll;
	}
}
