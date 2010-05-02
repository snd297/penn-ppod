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
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * @author Sam Donnelly
 */
@Embeddable
public class CharacterStates {

	/**
	 * The different types of {@code CategoricalCell}: single, polymorphic,
	 * uncertain, unassigned, or inapplicable.
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

	private static final Comparator<CategoricalState> STATE_COMPARATOR = new CategoricalState.CharacterStateComparator();

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIRST_" + CategoricalState.ID_COLUMN)
	@Nullable
	private CategoricalState firstState = null;

	/**
	 * The heart of the cell: the states.
	 */
	@ManyToMany
	@Sort(type = SortType.COMPARATOR, comparator = CategoricalState.CharacterStateComparator.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = CategoricalState.ID_COLUMN))
	@Nullable
	private SortedSet<CategoricalState> states = null;

	private boolean xmlStatesNeedsToBePutIntoStates;

	@Nullable
	private Type type;

	/**
	 * Return an unmodifiable set which contains this cell's states.
	 * <p>
	 * The returned set may or may not be a view of this cell's states.
	 * 
	 * @return an unmodifiable set which contains this cell's states
	 * 
	 * @throws IllegalStateException if the type of this cell has not been
	 *             assigned
	 */
	public Set<CategoricalState> getStates() {
		checkState(getType() != null,
				"type has yet to be assigned for this cell");

		// One may reasonably ask why we don't just do the
		// AfterUnmarshalVisitor's work here. Answer: We don't want to encourage
		// bad habits.
		checkState(
				!xmlStatesNeedsToBePutIntoStates,
				"xmlStateNeedsToBePutIntoStates == true, has the afterUnmarshal visitor been dispatched?");
		switch (getType()) {
			// Don't hit states unless we have too
			case INAPPLICABLE:
			case UNASSIGNED:
				return Collections.emptySet();
			case SINGLE:
				return Collections.unmodifiableSet(newHashSet(firstState));
			case POLYMORPHIC:
			case UNCERTAIN:

				// We have to hit states, which we want to avoid as much as
				// possible since it will trigger a database hit, which in the
				// aggregate
				// is expensive since there're are so many cells.
				return Collections.unmodifiableSet(states);
			default:
				throw new AssertionError("Unknown CharacterState.Type: " + type);
		}

	}

	/**
	 * Get the type of this cell.
	 * 
	 * @return the {@code CategoricalCell.Type}
	 */
	@XmlAttribute
	@Nullable
	public Type getType() {
		return type;
	}

	/**
	 * Add a set of {@code CharacterState}s to this {@code CategoricalCell}.
	 * <p>
	 * Assumes that none of {@code states} is in a Hibernate-detached state.
	 * <p>
	 * This object makes its own copy of {@code states}.
	 * 
	 * @param states to be added. Each must not be in a detached state.
	 * 
	 * @return {@code state}
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	@SuppressWarnings("unused")
	private CharacterStates setStates(final Set<CategoricalState> states) {
		checkNotNull(states);

		if (this.states == null) {
			this.states = newTreeSet(STATE_COMPARATOR);
		}

		if (getStates().equals(states)) {
			return this;
		}

		states.clear();

		this.states.addAll(states);

		if (states.size() > 0) {
			firstState = get(this.states, 0);
		}
		return this;
	}
}
