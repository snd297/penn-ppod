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

import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * {@code IPair} implementation.
 * 
 * @author Sam Donnelly
 */
public class Pair<T, U> implements IPair<T, U> {

	public static class Factory implements IFactory {
		public <T, U> Pair<T, U> create(final T first, final U second) {
			return new Pair<T, U>(first, second);
		}
	}

	private T _1st;

	private U _2nd;

	Pair() {}

	@Inject
	Pair(@Assisted final T first, @Assisted final U second) {
		this._1st = first;
		this._2nd = second;
	}

	public T get1st() {
		return _1st;
	}

	@XmlElement
	public U get2nd() {
		return _2nd;
	}

	/**
	 * Constructs a <code>String</code> with attributes in name=value format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	public String toString() {
		final String TAB = " ";

		StringBuilder retValue = new StringBuilder();

		retValue.append("OrderedPair(").append(super.toString()).append(TAB)
				.append("_1st=").append(this._1st).append(TAB).append("_2nd=")
				.append(this._2nd).append(TAB).append(")");

		return retValue.toString();
	}

	public static <T, U> Pair<T, U> of(final IPair<T, U> orderedPair) {
		return new Pair<T, U>(orderedPair.get1st(), orderedPair.get2nd());
	}

	/**
	 * Turn an {@link IPair} into an {@link Pair}.
	 */
	public static final Function<IPair<String, String>, Pair<String, String>> of = new Function<IPair<String, String>, Pair<String, String>>() {
		public Pair<String, String> apply(final IPair<String, String> from) {
			return Pair.of(from);
		}
	};
}
