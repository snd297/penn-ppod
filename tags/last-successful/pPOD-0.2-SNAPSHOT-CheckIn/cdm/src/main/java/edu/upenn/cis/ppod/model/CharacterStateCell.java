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
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;
import static edu.upenn.cis.ppod.util.UPennCisPPodUtil.nullSafeEquals;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * A cell in a {@link CharacterStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = CharacterStateCell.TABLE)
public final class CharacterStateCell extends PPodEntity {

	/**
	 * The different types of {@code CharacterStateCell}: single, polymorphic,
	 * uncertain, unassigned, or inapplicable.
	 */
	public static enum Type {

		/**
		 * The cell has exactly one state.
		 */
		SINGLE,

		/**
		 * The cell is a conjunctions of states: state1 and state2 and ... and
		 * stateN.
		 */
		POLYMORPHIC,

		/**
		 * The cell is a disjunction of states: state1 or state2 or ... or
		 * stateN.
		 */
		UNCERTAIN,

		/** Unassigned, usually written as a {@code "?"} in Nexus files. */
		UNASSIGNED,

		/** Inapplicable, usually written as a {@code "-"} in Nexus files. */
		INAPPLICABLE;

		/**
		 * From a string, return the {@code Type} that has {@code
		 * toString().equals(s)}.
		 * 
		 * @param s see description
		 * @return see description
		 */
		public static Type of(final String s) {
			if (Type.SINGLE.toString().equals(s)) {
				return Type.SINGLE;
			} else if (Type.UNASSIGNED.toString().equals(s)) {
				return Type.UNASSIGNED;
			} else if (Type.INAPPLICABLE.toString().equals(s)) {
				return Type.INAPPLICABLE;
			} else if (Type.POLYMORPHIC.toString().equals(s)) {
				return Type.POLYMORPHIC;
			} else if (Type.UNCERTAIN.toString().equals(s)) {
				return Type.UNCERTAIN;
			}
			throw new IllegalArgumentException("Unknown s: [" + s + "]");
		}
	}

	static final String TABLE = "CHARACTER_STATE_CELL";

	static final String ID_COLUMN = TABLE + "_ID";

	static final String TYPE_COLUMN = "TYPE";

	/**
	 * {@code CharacterStateCell}-{@link CharacterState} join table.
	 * Intentionally package-private.
	 */
	static final String CELL_CHARACTER_STATE_JOIN_TABLE = TABLE + "_"
			+ CharacterState.TABLE;

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "FIRST_" + CharacterState.ID_COLUMN)
	private CharacterState firstState = null;

	/**
	 * The heart of the cell: the states.
	 */
	@ManyToMany
	@Sort(type = SortType.COMPARATOR, comparator = CharacterState.CharacterStateComparator.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinTable(name = CELL_CHARACTER_STATE_JOIN_TABLE, joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = CharacterState.ID_COLUMN))
	private SortedSet<CharacterState> states = null;

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@Transient
	private Set<CharacterState> xmlStates = null;

	/**
	 * Does this cell have a single state?, multiple states?, is it unassigned?,
	 * or inapplicable?
	 * 
	 * @see Type
	 */
	@XmlAttribute
	@Column(name = TYPE_COLUMN)
	@Enumerated(EnumType.STRING)
	private CharacterStateCell.Type type;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = CharacterStateRow.ID_COLUMN, insertable = false, updatable = false, nullable = false)
	private CharacterStateRow row;

	/**
	 * Tells us that this {@code CharacterStateCell} has been unmarshalled and
	 * still needs to have {@code states} populated with {@code xmlStates}.
	 */
	@Transient
	private boolean xmlStatesNeedsToBePutIntoStates = false;

	private static final Comparator<CharacterState> STATE_COMPARATOR = new CharacterState.CharacterStateComparator();

	/** No-arg constructor for (at least) Hibernate. */
	CharacterStateCell() {}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		row = (CharacterStateRow) parent; // don't call setRow because it'll
		// reset the ppod version info
		xmlStatesNeedsToBePutIntoStates = true;
	}

	@Override
	public boolean beforeMarshal(final Marshaller marshaller) {
		super.beforeMarshal(marshaller);
		getXmlStates().addAll(getStates());
		return true;
	}

	private void checkIncomingState(final CharacterState state) {
		if (row == null) {
			throw new IllegalStateException(
					"This cell has not been added to a row yet.");
		}
		if (state.getCharacter() == null) {
			throw new IllegalArgumentException(
					"for state + '"
							+ state.getLabel()
							+ "' getCharacter() == null but CharacterStateCell needs it to be associated with a character");
		}
		if (!state.getCharacter().equals(
				getRow().getMatrix().getCharacter(
						getRow().getCellIdx().get(this)))) {
			throw new IllegalArgumentException(
					"state is from the wrong Character. We want "
							+ getRow().getMatrix().getCharacter(
									getRow().getCellIdx().get(this)).getLabel()
							+ " but got " + state.getCharacter().getLabel());
		}
	}

	/**
	 * Do {@code checkNotNull(...)}s and make sure that {@code type} and {@code
	 * states.size()} are compatible.
	 * 
	 * @param type the type
	 * @param states the states
	 */
	private void checkTypeAndStates(final Type type,
			final Set<CharacterState> states) {
		checkNotNull(type);
		checkNotNull(states);
		switch (type) {

			case INAPPLICABLE:
				if (states.size() > 0) {
					throw new IllegalArgumentException(
							"type INAPPLICABLE needs empty states arg");
				}
				break;
			case UNASSIGNED:
				if (states.size() > 0) {
					throw new IllegalArgumentException(
							"type UNASSIGNED needs empty states arg");
				}
				break;
			case SINGLE:
				if (states.size() != 1) {
					throw new IllegalArgumentException(
							"type SINGLE needs == 1 states arg");
				}
				break;
			case POLYMORPHIC:
				if (states.size() < 2) {
					throw new IllegalArgumentException(
							"type POLYMORPHIC needs > 1 states arg");
				}
				break;
			case UNCERTAIN:
				if (states.size() < 2) {
					throw new IllegalArgumentException(
							"type UNCERTAIN needs > 1 states arg");
				}
				break;
			default:
				throw new AssertionError("Unknown CharacterState.Type: " + type);
		}
	}

	/**
	 * Clear all {@link CharacterState} info.
	 */
	private void clearStates() {
		if (firstState == null) {
			// Should be all clear, but let's check for programming errors
			if (states != null && states.size() != 0) {
				throw new AssertionError(
						"programming error: firstate == null and states.size() != 0");
			}
		} else {
			firstState = null;
			if (states != null) {
				states.clear();
			}
			resetPPodVersionInfo();
		}
	}

	/**
	 * Getter.
	 * 
	 * @return the {@code CharacterStateRow} to which this {@code
	 *         CharacterStateCell} belongs
	 */
	public CharacterStateRow getRow() {
		return row;
	}

	/**
	 * Retrieve the {@code CharacterState} with the given state number, or
	 * {@code null} if there is no such {@code CharacterState}
	 * 
	 * @param stateNumber the state value
	 * @return Retrieve the {@code CharacterState} with the given state number,
	 *         or {@code null} if there is no such {@code CharacterState}
	 */
	public CharacterState getStateByNumber(final Integer stateNumber) {
		return findIf(getStates(), compose(equalTo(stateNumber),
				CharacterState.getStateNumber));
	}

	/**
	 * Return an unmodifiable set which contains this cell's states.
	 * <p>
	 * The returned set may or may not be a view of this cell's states.
	 * 
	 * @return an unmodifiable copy of this cell's states
	 */
	public Set<CharacterState> getStates() {
		if (xmlStatesNeedsToBePutIntoStates) {
			checkTypeAndStates(this.type, getXmlStates());
			setStates(getXmlStates());
			xmlStates = null;
			xmlStatesNeedsToBePutIntoStates = false;
		}
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
	 * @return the {@code CharacterStateCell.Type}
	 */
	public CharacterStateCell.Type getType() {
		return type;
	}

	@XmlElement(name = "stateDocId")
	@XmlIDREF
	private Set<CharacterState> getXmlStates() {
		if (xmlStates == null) {
			xmlStates = newHashSet();
		}
		return xmlStates;
	}

	/**
	 * Created for testing purposes.
	 */
	boolean getXmlStatesNeedsToBePutIntoStates() {
		return xmlStatesNeedsToBePutIntoStates;
	}

	/**
	 * {@code null} out {@code pPodVersionInfo}.
	 * 
	 * @return this {@code CharacterStateCell}
	 */
	@Override
	protected CharacterStateCell resetPPodVersionInfo() {
		if (getPPodVersionInfo() == null) {} else {
			if (row != null) {
				row.resetPPodVersionInfo();
				row.getMatrix().resetColumnPPodVersion(
						row.getCellIdx().get(this));
			}
			super.resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Setter. Intentionally package-private.
	 * 
	 * @param row value. nullable.
	 * 
	 * @return this {@code CharacterStateCell}
	 */
	CharacterStateCell setRow(final CharacterStateRow row) {
		if (nullSafeEquals(this.row, row)) {

		} else {
			this.row = row;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Add a {@code CharacterState} to this {@code CharacterStateCell}.
	 * <p>
	 * Assumes that none of {@code states} is in a Hibernate-detached state.
	 * 
	 * @param state to be added. Must be in a persistent state.
	 * 
	 * @return {@code state}
	 */
	private CharacterStateCell setStates(final Set<CharacterState> states) {
		checkNotNull(states);

		if (this.states == null) {
			this.states = newTreeSet(STATE_COMPARATOR);
		}

		if (states.equals(this.states)) {
			return this;
		}

		for (final CharacterState state : states) {
			checkIncomingState(state);
		}

		clearStates();

		this.states.addAll(states);

		if (states.size() > 0) {
			firstState = get(this.states, 0);
		}
		resetPPodVersionInfo();
		return this;
	}

	/**
	 * Setter.
	 * 
	 * @param type the value
	 * 
	 * @return this {@link CharacterStateCell}
	 */
	private CharacterStateCell setType(final CharacterStateCell.Type type) {
		checkNotNull(type);
		if (type.equals(this.type)) {} else {
			this.type = type;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set both the {@code Type} and states of this cell.
	 * <p>
	 * {@code states} will be copied so subsequent actions on it will not affect
	 * this cell.
	 * 
	 * @param type the type
	 * @param states the states
	 * 
	 * @return this cell
	 * 
	 * @throws IllegalStateException if this cell is not a member of a row
	 * @throws IllegalStateException if any member of {@code states} is such
	 *             that {@code state.getCharacter() == null}, that is, if any of
	 *             {@code states} isn't associated with a character
	 * @throws IllegalArgumentException if any of {@code states} is such that
	 *             {@code state.getCharacter()} is not the character of this
	 *             cell's column
	 * @throws IllegalArgumentException if {@code type} is {@code Type.SINGLE}
	 *             and {@codes states.size() != 1}
	 * @throws IllegalArgumentException if {@code type} is {@code
	 *             Type.POLYMORPHIC} and {@code states.size() < 2}
	 * @throws IllegalArgumentException if {@code type} is {@code
	 *             Type.UNCERTAIN} and {@code states.size() < 2}
	 * @throws IllegalArgumentException if {@code type} is {@code
	 *             Type.INAPPLICABLE} and {@code states.size() > 0}
	 * @throws IllegalArgumentException if {@code type} is {@code
	 *             Type.UNASSIGNED} and {@code states.size() > 0}
	 */
	public CharacterStateCell setTypeAndStates(final Type type,
			final Set<CharacterState> states) {
		checkNotNull(type);
		checkNotNull(states);
		checkTypeAndStates(type, states);

		setType(type);
		setStates(states);
		return this;
	}

	/**
	 * Created for testing purposes.
	 * 
	 * @param xmlStates should be {@code null} if type is inapplicable or
	 *            unassigned to emulate what happens when a cell is unmarhsalled
	 */
	CharacterStateCell setTypeAndXmlStates(final Type type,
			final Set<CharacterState> xmlStates) {
		checkNotNull(type);
		checkTypeAndStates(type,
				xmlStates == null ? new HashSet<CharacterState>() : xmlStates);
		setType(type);
		this.xmlStates = xmlStates;
		return this;
	}

	/**
	 * Created for testing purposes.
	 */
	CharacterStateCell setXmlStatesNeedsToBePutIntoStates(
			final boolean xmlStatesNeedsToBePutIntoStates) {
		this.xmlStatesNeedsToBePutIntoStates = xmlStatesNeedsToBePutIntoStates;
		return this;
	}

	/**
	 * Constructs a {@code String} with attributes in name=value format.
	 * 
	 * @return a {@code String} representation of this object
	 */
	@Override
	public String toString() {
		final String TAB = " ";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("PhyloCharMatrixCell(").append(super.toString())
				.append(TAB).append("version=").append(TAB).append("states=")
				.append(this.states).append(TAB).append(")");

		return retValue.toString();
	}
}
