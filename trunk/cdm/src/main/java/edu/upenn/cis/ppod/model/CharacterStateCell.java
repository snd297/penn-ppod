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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Collections;
import java.util.Comparator;
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

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.upenn.cis.ppod.util.IVisitor;

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
	@JoinColumn(name = "FIRST_" + CharacterState.ID_COLUMN)
	private CharacterState firstState = null;

	/**
	 * The heart of the cell: the states.
	 */
	@ManyToMany
	@Sort(type = SortType.COMPARATOR, comparator = CharacterState.CharacterStateComparator.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = CharacterState.ID_COLUMN))
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
	 */
	@Column(name = TYPE_COLUMN)
	@Enumerated(EnumType.ORDINAL)
	private Type type;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 * <p>
	 * Not persisted. It's up to the client to set after db retrieval this for
	 * pPOD version propagation and state checking.
	 */
	@Transient
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

	@Override
	public CharacterStateCell accept(final IVisitor visitor) {
		visitor.visit(this);
		return this;
	}

	/**
	 * Take actions after unmarshalling that need to occur after
	 * {@link #afterUnmarshal(Unmarshaller, Object)} is called, specifically
	 * after {@code @XmlIDRef} elements are resolved
	 */
	public void afterUnmarshal() {
		if (xmlStatesNeedsToBePutIntoStates) {
			xmlStatesNeedsToBePutIntoStates = false;
			setStates(getXmlStates());
			xmlStates = null;
		}
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		row = (CharacterStateRow) parent; // don't setRow call because it'll
		// reset the ppod version info
		xmlStatesNeedsToBePutIntoStates = true;
		super.afterUnmarshal(u, parent);
	}

	/**
	 * @throws IllegalStateException if the type has not been set
	 */
	@Override
	public boolean beforeMarshal(final Marshaller marshaller) {

		if (type == null) {
			// Let's not marshal it if it's in a bad state
			throw new IllegalStateException(
					"can't marshal a cell without a type");
		}
		getXmlStates().addAll(getStates());
		return super.beforeMarshal(marshaller);

	}

	private void checkIncomingState(final CharacterState state) {
		if (getRow() == null) {
			throw new IllegalStateException(
					"this cell has not been assigned a row");
		}

		if (getRow().getMatrix() == null) {
			throw new IllegalStateException(
					"this cell's row has not had a matrix assigned");
		}

		if (getRow().getMatrix().getCharacters().size() < getRow().getCellIdx()
				.get(this)
				|| null == getRow().getMatrix().getCharacters().get(
						getRow().getCellIdx().get(this))) {
			throw new IllegalStateException(
					"this cell's column hasn't been assigned a character");
		}

		final Character thisCellsCharacter = getRow().getMatrix()
				.getCharacters().get(row.getCellIdx().get(this));

		if (!state.getCharacter().equals(thisCellsCharacter)) {
			throw new IllegalArgumentException(
					"state is from the wrong Character. We want "
							+ getRow().getMatrix().getCharacters().get(
									getRow().getCellIdx().get(this)).getLabel()
							+ " but got " + state.getCharacter().getLabel());
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
						"programming error: firstate == null && states != null && states.size() != 0");
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
	 * @return an unmodifiable set which contains this cell's states
	 * 
	 * @throws IllegalStateException if the type of this cell has not been
	 *             assigned
	 */
	public Set<CharacterState> getStates() {
		checkState(getType() != null,
				"type has yet to be assigned for this cell");

		// One may reasonably ask why we don't just do the visitor's work here.
		// Answer: We don't want to encourage bad habits.
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
	 * @return the {@code CharacterStateCell.Type}
	 */
	@XmlAttribute
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
	 * Set this cell's type to {@link Type#INAPPLICABLE} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public CharacterStateCell setInapplicable() {
		setType(Type.INAPPLICABLE);

		@SuppressWarnings("unchecked")
		final Set<CharacterState> emptyStates = Collections.EMPTY_SET;
		setStates(emptyStates);

		return this;
	}

	/**
	 * Set the type to polymorphic uncertain with the given states.
	 * 
	 * @param polymorphicStates the states
	 * 
	 * @return this
	 * 
	 * @throw IllegalArgumentException if {@code polymorphicStates.size() < 2}
	 */
	public CharacterStateCell setPolymorphicStates(
			final Set<CharacterState> polymorphicStates) {
		checkNotNull(polymorphicStates);
		checkArgument(polymorphicStates.size() > 1,
				"polymorphic states must be > 1");
		setType(Type.POLYMORPHIC);
		setStates(polymorphicStates);
		return this;
	}

	/**
	 * Set the row to which this cell belongs.
	 * <p>
	 * This value not persisted because it's simple to be able to persist the
	 * cells (and there are lots of them) before the row is persisted.
	 * <p>
	 * This value is used for error checking. For example, to make sure that the
	 * states that are assigned to this cell belong to the correct character.
	 * 
	 * @param row value. nullable.
	 * 
	 * @return this {@code CharacterStateCell}
	 */
	public CharacterStateCell setRow(final CharacterStateRow row) {
		this.row = row;
		return this;
	}

	/**
	 * Set the cell to have {@link Type#SINGLE} and {@link #getStates()} {@code
	 * == state}.
	 * 
	 * @param state state to assign to this cell
	 * 
	 * @return this
	 */
	public CharacterStateCell setSingleState(final CharacterState state) {
		checkNotNull(state);
		setType(Type.SINGLE);
		setStates(newHashSet(state));
		return this;
	}

	/**
	 * Add a set of {@code CharacterState}s to this {@code CharacterStateCell}.
	 * <p>
	 * Assumes that none of {@code states} is in a Hibernate-detached state.
	 * <p>
	 * This object makes its own copy of {@code states}.
	 * 
	 * @param states to be added. Each must not be in a detached state.
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
			if (state.getCharacter() == null) {
				final Character thisCellsCharacter = getRow().getMatrix()
						.getCharacters().get(row.getCellIdx().get(this));
				state.setCharacter(thisCellsCharacter);
			}
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
		if (type.equals(this.type)) {

		} else {
			this.type = type;
			resetPPodVersionInfo();
		}
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
		// checkTypeAndStates(type,
		// xmlStates == null ? new HashSet<CharacterState>() : xmlStates);
		setType(type);
		this.xmlStates = xmlStates;
		return this;
	}

	/**
	 * Set this cell's type to {@link Type#UNASSIGNED} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public CharacterStateCell setUnassigned() {
		setType(Type.UNASSIGNED);

		@SuppressWarnings("unchecked")
		final Set<CharacterState> emptyStates = Collections.EMPTY_SET;
		setStates(emptyStates);
		return this;
	}

	/**
	 * Set the type to uncertain with the given states.
	 * 
	 * @param uncertainStates the states
	 * 
	 * @return this
	 * 
	 * @throw IllegalArgumentException if {@code uncertainStates.size() < 2}
	 */
	public CharacterStateCell setUncertainStates(
			final Set<CharacterState> uncertainStates) {
		checkNotNull(uncertainStates);
		checkArgument(uncertainStates.size() > 1,
				"uncertain states must be > 1");
		setType(Type.UNCERTAIN);
		setStates(uncertainStates);
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

		retValue.append("CharacterStateCell(").append(super.toString()).append(
				TAB).append("version=").append(TAB).append("states=").append(
				this.states).append(TAB).append(")");

		return retValue.toString();
	}

}
