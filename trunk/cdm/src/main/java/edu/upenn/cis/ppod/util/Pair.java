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

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * {@code IPair} implementation.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Pair<T, U> implements IPair<T, U> {

	public static class Factory implements IFactory {
		public <T, U> Pair<T, U> create(final T first, final U second) {
			return new Pair<T, U>(first, second);
		}
	}

	private T first;

	private U second;

	protected Pair() {}

	@Inject
	protected Pair(@Assisted final T first, @Assisted final U second) {
		this.first = first;
		this.second = second;
	}

	@Nullable
	public T getFirst() {
		return first;
	}

	@Nullable
	public U getSecond() {
		return second;
	}

	public Pair<T, U> setFirst(final T first) {
		this.first = first;
		return this;
	}

	public void setSecond(final U second) {
		this.second = second;
	}

	/**
	 * Constructs a <code>String</code> with attributes in name=value format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = " ";

		StringBuilder retValue = new StringBuilder();

		retValue.append("Pair(").append(super.toString()).append(TAB).append(
				"first=").append(this.first).append(TAB).append("second=")
				.append(this.second).append(TAB).append(")");

		return retValue.toString();
	}

	public static <T, U> Pair<T, U> of(final IPair<T, U> orderedPair) {
		return new Pair<T, U>(orderedPair.getFirst(), orderedPair.getSecond());
	}

	public static <T, U> Pair<T, U> of(final T first, final U second) {
		return new Pair<T, U>(first, second);
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
