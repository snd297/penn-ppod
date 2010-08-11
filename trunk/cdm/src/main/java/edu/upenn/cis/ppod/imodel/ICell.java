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

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.Nullable;

public interface ICell<E, R extends IRow<?, ?>> extends IOrderedChild<R> {

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
	public static enum Type {

		/** Unassigned, usually written as a {@code "?"} in Nexus files. */
		UNASSIGNED,

		/**
		 * The cell has exactly one state.
		 */
		SINGLE,

		/**
		 * The cell is a conjunctions of states: <em>state1</em> and
		 * <em>state2</em> and ... and <em>stateN</em>.
		 */
		POLYMORPHIC,

		/**
		 * The cell is a disjunction of states: <em>state1</em> or
		 * <em>state2</em> or ... or <em>stateN</em>.
		 */
		UNCERTAIN,

		/** Inapplicable, usually written as a {@code "-"} in Nexus files. */
		INAPPLICABLE;

	}

	/**
	 * Get the elements contained in this cell.
	 * <p>
	 * Will be the the empty set if type is {@link Type.INAPPLICABLE} or
	 * {@link Type.UNASSIGNED}.
	 * 
	 * @return the elements contained in this cell
	 */
	Set<E> getElements();

	/**
	 * Get the type of this cell.
	 * <p>
	 * This value will be {@code null} for newly created cells until the
	 * elements are set. Once set, this will never be {@code null}.
	 * 
	 * @return the type of this cell
	 */
	@XmlAttribute
	@Nullable
	Type getType();

	/**
	 * Set this cell's type to {@link Type#INAPPLICABLE}, its elements to the
	 * empty set.
	 */
	void setInapplicable();

	/**
	 * Set this cell's type to {@link Type#UNASSIGNED}, its elements to the
	 * empty set.
	 */
	void setUnassigned();

	/**
	 * Set the type to uncertain and this cell's elements to the values
	 * equivalent to {@code elements}.
	 * <p>
	 * {@code elements.size()} must be greater than 2.
	 * <p>
	 * The elements that are actually assigned may not be {@code ==} to the
	 * members of {@code elements}, but will be the equivalent appropriate for
	 * the owning matrix. See {@link IStandardCell} for an example of that
	 * behavior.
	 * 
	 * @param elements the elements
	 */
	void setUncertainElements(
			final Set<? extends E> elements);

}