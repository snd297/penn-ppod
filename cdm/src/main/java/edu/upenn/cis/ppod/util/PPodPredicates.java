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
package edu.upenn.cis.ppod.util;


/**
 * @author Sam Donnelly
 * 
 */
public class PPodPredicates {

	/**
	 * Functionally equivalent to
	 * 
	 * <pre>
	 * compose(Predicates.equalTo(target), func);
	 * </pre>
	 * 
	 * @param <A>
	 * @param <B>
	 * @param target
	 * @param func
	 * @return
	 */
// public static <A, B> Predicate<A> equalTo(final B target,
// final Function<A, ? extends B> func) {
// return compose(Predicates.equalTo(target), func);
// }

	private PPodPredicates() {
		throw new IllegalStateException("Can't instantiate a PPodIterables");
	}
}
