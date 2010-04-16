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
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
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
@Entity
@Table(name = CharacterStateCell.TABLE)
public class CharacterStateCell extends PPodEntity implements
		Iterable<CharacterState> {

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

	/** Position in a {@link CharacterStateRow}. */
	@Column(name = "POSITION", nullable = false)
	@CheckForNull
	private Integer position;

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
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIRST_" + CharacterState.ID_COLUMN)
	@CheckForNull
	private CharacterState firstState = null;

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@ManyToMany
	@Sort(type = SortType.COMPARATOR, comparator = CharacterState.CharacterStateComparator.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = CharacterState.ID_COLUMN))
	@CheckForNull
	private SortedSet<CharacterState> states = null;

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@Transient
	@CheckForNull
	private Set<CharacterState> xmlStates = null;

	/**
	 * Does this cell have a single state?, multiple states?, is it unassigned?,
	 * or inapplicable?
	 */
	@Column(name = TYPE_COLUMN, nullable = false)
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private Type type;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@ManyToOne
	@JoinColumn(name = CharacterStateRow.ID_COLUMN)
	@CheckForNull
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
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Take actions after unmarshalling that need to occur after
	 * {@link #afterUnmarshal(Unmarshaller, Object)} is called - specifically,
	 * after {@code @XmlIDRef} elements are resolved
	 */
	@Override
	public void afterUnmarshal() {
		super.afterUnmarshal();
		if (xmlStatesNeedsToBePutIntoStates) {
			xmlStatesNeedsToBePutIntoStates = false;

			// Let's reset the type to make it consistent with states
			final Type xmlType = getType();
			this.type = null;
			setTypeAndStates(xmlType, getXmlStates());
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
		super.afterUnmarshal(u, parent);
		row = (CharacterStateRow) parent; // don't setRow call because it'll
		// reset the ppod version info
		xmlStatesNeedsToBePutIntoStates = true;
	}

	/**
	 * @throws IllegalStateException if the type has not been set
	 */
	@Override
	public boolean beforeMarshal(@Nullable final Marshaller marshaller) {

		// Let's not marshal it if it's in a bad state
		checkState(type != null, "can't marshal a cell without a type");

		getXmlStates().addAll(getStates());
		return super.beforeMarshal(marshaller);

	}

	private void checkIncomingState(final CharacterState state) {

		final CharacterStateRow row = getRow();

		checkState(row != null && getPosition() != null,
				"this cell has not been assigned a row");

		final Integer position = getPosition();

		final CharacterStateMatrix matrix = row.getMatrix();

		checkState(matrix != null,
				"this cell's row has not had a matrix assigned");

		checkState(matrix.getCharacters().size() >= position,
				"this cell's column hasn't been assigned a character");

		checkState(null != matrix.getCharacters().get(position),
				"this cell's column hasn't been assigned a character");

		final Character thisCellsCharacter = matrix.getCharacters().get(
				position);

		checkArgument(state.getCharacter() != null, "state " + state.getLabel()
				+ " hasn't been assigned to a Character");

		checkArgument(state.getCharacter().equals(thisCellsCharacter),
				"state is from the wrong Character. We want "
						+ matrix.getCharacters().get(position)
								.getLabel() + " but got "
						+ state.getCharacter().getLabel());

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
			setInNeedOfNewPPodVersionInfo();
		}
	}

	@CheckForNull
	private Integer getPosition() {
		return position;
	}

	/**
	 * Getter. This will return {@code null} until the cell is added to a row.
	 * 
	 * @return the {@code CharacterStateRow} to which this {@code
	 *         CharacterStateCell} belongs
	 */
	@Nullable
	public CharacterStateRow getRow() {
		return row;
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
	private Set<CharacterState> getStates() {
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
				return newHashSet(firstState);
			case POLYMORPHIC:
			case UNCERTAIN:

				// We have to hit states, which we want to avoid as much as
				// possible since it will trigger a database hit, which in the
				// aggregate
				// is expensive since there're are so many cells.
				if (states == null) {
					return Collections.emptySet();
				}
				return states;

			default:
				throw new AssertionError("Unknown CharacterState.Type: " + type);
		}
	}

	/**
	 * Get the number of states that this cell contains.
	 * 
	 * @return the number of states that this cell contains
	 */
	public int getStatesSize() {
		checkState(getType() != null,
				"type has yet to be assigned for this cell");
		switch (getType()) {
			case INAPPLICABLE:
			case UNASSIGNED:
				return 0;
			case SINGLE:
				return 1;
			case POLYMORPHIC:
			case UNCERTAIN:
				return getStates().size();
			default:
				throw new AssertionError("Unknown CharacterState.Type: " + type);
		}
	}

	/**
	 * Get the type of this cell. {@code null} when this object is constructed.
	 * 
	 * @return the {@code Type}
	 */
	@XmlAttribute
	@Nullable
	public Type getType() {
		return type;
	}

	/**
	 * Set package-private for testing.
	 * 
	 * @return the states for marhsalling
	 */
	@XmlElement(name = "stateDocId")
	@XmlIDREF
	Set<CharacterState> getXmlStates() {
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
	 * Returns an iterator over this cell's states. Guaranteed to iterator in
	 * {@link CharacterState#getStateNumber()} order.
	 * 
	 * @return an iterator over this cell's states
	 */
	public Iterator<CharacterState> iterator() {
		return Collections.unmodifiableSet(getStates()).iterator();
	}

	/**
	 * Set this cell's type to {@link Type#INAPPLICABLE} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public CharacterStateCell setInapplicable() {

		@SuppressWarnings("unchecked")
		final Set<CharacterState> emptyStates = Collections.EMPTY_SET;
		setTypeAndStates(Type.INAPPLICABLE, emptyStates);
		return this;
	}

	@Override
	public CharacterStateCell setInNeedOfNewPPodVersionInfo() {
		final CharacterStateRow row = getRow();
		if (row != null) {
			row.setInNeedOfNewPPodVersionInfo();
			final CharacterStateMatrix matrix = row.getMatrix();
			if (matrix != null) {

				// so FindBugs knows that it's okay
				final Integer position = getPosition();
				checkState(position != null,
						"cell has no position, but is a part of a matrix");
				matrix.resetColumnPPodVersion(position);
			}
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Set the type to polymorphic with the given states.
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
		setTypeAndStates(Type.POLYMORPHIC, polymorphicStates);
		return this;
	}

	/**
	 * Set the position.
	 * 
	 * @param position the position to set, pass in {@code null} if the cell is
	 *            no longer part of a row
	 * 
	 * @return this
	 */
	protected CharacterStateCell setPosition(@Nullable final Integer position) {
		this.position = position;
		return this;
	}

	/**
	 * Set or unset the row to which this cell belongs.
	 * <p>
	 * This value is used for error checking. For example, to make sure that the
	 * states that are assigned to this cell belong to the correct character.
	 * 
	 * @param row value, {@code null} to indicate that the cell is removed from
	 *            the row
	 * 
	 * @return this {@code CharacterStateCell}
	 */
	protected CharacterStateCell setRow(@Nullable final CharacterStateRow row) {
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
		setTypeAndStates(Type.SINGLE, newHashSet(state));
		return this;
	}

	private CharacterStateCell setType(final Type type) {
		checkNotNull(type);
		this.type = type;
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
	private CharacterStateCell setTypeAndStates(final Type type,
			final Set<? extends CharacterState> states) {
		checkNotNull(type);
		checkNotNull(states);

		if (this.states == null) {
			this.states = newTreeSet(STATE_COMPARATOR);
		}

		// So FindBugs knows that we got it when it wasn't null
		final Set<CharacterState> thisStates = this.states;

		if (getType() != null && getType().equals(type)
				&& states.equals(getStates())) {
			return this;
		}

		for (final CharacterState state : states) {
			checkIncomingState(state);
		}

		clearStates();

		thisStates.addAll(states);

		if (states.size() > 0) {
			firstState = get(thisStates, 0);
		}

		setType(type);
		setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Created for testing purposes.
	 * 
	 * @param xmlStates should be {@code null} if type is inapplicable or
	 *            unassigned to emulate what happens when a cell is unmarhsalled
	 */
	CharacterStateCell setTypeAndXmlStates(final CharacterStateCell.Type type,
			final Set<CharacterState> xmlStates) {
		checkNotNull(type);
		checkNotNull(xmlStates);
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
		final Set<CharacterState> emptyStates = Collections.emptySet();
		setTypeAndStates(CharacterStateCell.Type.UNASSIGNED, emptyStates);
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
		setTypeAndStates(CharacterStateCell.Type.UNCERTAIN, uncertainStates);
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
