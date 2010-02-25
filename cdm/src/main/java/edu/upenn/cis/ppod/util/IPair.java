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
 * An ordered pair.
 * 
 * @author Sam Donnelly
 * @param <T> type of the first
 * @param <U> type of the second
 */
public interface IPair<T, U> {

	/**
	 * Get the first entry.
	 * 
	 * @return the first entry
	 */
	T get1st();

	/**
	 * Get the second entry.
	 * 
	 * @return the second entry
	 */
	U get2nd();

	/**
	 * Create an ordered pair.
	 */
	static interface IFactory {
		<T, U> IPair<T, U> create(T t, U u);
	}
}