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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * {@code IPair} implementation.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Pair<T, U> {

	public static <T, U> Pair<T, U> of(final T first, final U second) {
		return new Pair<T, U>(first, second);
	}

	private T first;

	private U second;

	/**
	 * For JAXB.
	 */
	protected Pair() {}

	public Pair(final T first, final U second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public U getSecond() {
		return second;
	}

	protected void setFirst(final T first) {
		this.first = first;
	}

	protected void setSecond(final U second) {
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

		final StringBuilder retValue = new StringBuilder();

		retValue.append("Pair(").append(super.toString()).append(TAB).append(
				"first=").append(this.first).append(TAB).append("second=")
				.append(this.second).append(TAB).append(")");

		return retValue.toString();
	}
}
