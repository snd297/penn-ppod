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
package edu.upenn.cis.ppod.imodel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

/**
 * Utility methods for the {@code imodel} package.
 * 
 * @author Sam Donnelly
 */
public class IModelUtil {

	/**
	 * Orders character states by the natural ordering of
	 * {@link IStandardState#getStateNumber()}.
	 * <p>
	 * Note: this comparator imposes orderings that are inconsistent with
	 * equals. If two states have the same state numbers, this comparator
	 * returns {@code 0} whether they are {@code .equals} or not.
	 * <p>
	 * We use an external comparator for {@code IStandardState} instead of
	 * implementing {@code CharacterStateComparator} because we don't want to
	 * have to worry (as much) about being incompatible with equals. See
	 * {@link Comparable} for information about that.
	 * <p>
	 * Note that the state number of a {@code StandardState} is immutable once
	 * assigned, so since the comparator does not allow {@code null} values for
	 * {@link IStandardState#getStateNumber()} this comparator will be safe to
	 * use in a {@code SortedSet}. See
	 * {@link StandardState#setStateNumber(Integer)} for the why's of the
	 * mutability.
	 */
	public static class StandardStateComparator implements
			java.util.Comparator<IStandardState>, Serializable {

		private static final long serialVersionUID = 1L;

		public int compare(final IStandardState o1, final IStandardState o2) {
			checkNotNull(o1);
			checkArgument(
					o1.getStateNumber() != null,
					"o1.getStateNumber() == null");

			checkNotNull(o2);
			checkArgument(o2.getStateNumber() != null,
					"o2.getStateNumber() == null");

			return o2.getStateNumber() - o1.getStateNumber();
		}
	}

	/** Prevent inheritance and instantiation. */
	private IModelUtil() {
		throw new AssertionError("Can't instantiate an IModelUtil");
	}
}
