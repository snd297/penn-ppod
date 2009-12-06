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

/**
 * Project wide utilities.
 * 
 * @author Sam Donnelly
 */
public class UPennCisPPodUtil {

	/**
	 * Prevent inheritance and instantiation.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	private UPennCisPPodUtil() {
		throw new UnsupportedOperationException(
				"Can't instantiate a UPennCisPPodUtil");
	}

	/**
	 * Equivalent to:<br>
	 * If {@code lhs == rhs == null}, return {@code true}.<br>
	 * Else if {@code lhs == null}, return {@code false}. <br>
	 * Else return {@code lhs.equals(rhs)}.
	 * 
	 * @param lhs left-hand side
	 * @param rhs right-hand side
	 * 
	 * @return see description
	 */
	public static boolean nullSafeEquals(final Object lhs, final Object rhs) {
		if ((lhs == null) && (rhs == null)) {
			return true;
		}
		if (lhs == null) {
			return false;
		}
		return lhs.equals(rhs);
	}

}
