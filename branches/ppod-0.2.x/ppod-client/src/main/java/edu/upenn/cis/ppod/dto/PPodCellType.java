/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
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
package edu.upenn.cis.ppod.dto;

/**
 * The different types of {@code Cell}: single, polymorphic, uncertain,
 * unassigned, or inapplicable.
 * <p>
 * Because we're storing these in the db as ordinals they will be:
 * <ul>
 * <li>{@code UNASSIGNED -> 0}</li>
 * <li>{@code SINGLE -> 1}</li>
 * <li>{@code POLYMORPHIC -> 2}</li>
 * <li>{@code UNCERTAIN -> 3}</li>
 * <li>{@code INAPPLICABLE -> 4}</li>
 * </ul>
 */
public enum PPodCellType {

	/** Unassigned, usually written as a {@code "?"} in Nexus files. */
	UNASSIGNED,

	/**
	 * The cell has exactly one state.
	 */
	SINGLE,

	/**
	 * The cell is a conjunctions of states: <em>state1</em> and <em>state2</em>
	 * and ... and <em>stateN</em>.
	 */
	POLYMORPHIC,

	/**
	 * The cell is a disjunction of states: <em>state1</em> or <em>state2</em>
	 * or ... or <em>stateN</em>.
	 */
	UNCERTAIN,

	/** Inapplicable, usually written as a {@code "-"} in Nexus files. */
	INAPPLICABLE;

}