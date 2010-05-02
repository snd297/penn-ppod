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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
@Table(name = Cell.TABLE)
public class CategoricalCell extends Cell<CategoricalState> implements
		Iterable<CategoricalState> {

	private static final Comparator<CategoricalState> STATE_COMPARATOR = new CategoricalState.CharacterStateComparator();

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIRST_" + CategoricalState.ID_COLUMN)
	@CheckForNull
	private CategoricalState firstState = null;

	/**
	 * The {@code CharacterStateRow} to which this {@code CategoricalCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = CharacterStateRow.ID_COLUMN)
	@CheckForNull
	private CharacterStateRow row;

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@ManyToMany
	@Sort(type = SortType.COMPARATOR, comparator = CategoricalState.CharacterStateComparator.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = CategoricalState.ID_COLUMN))
	@CheckForNull
	private SortedSet<CategoricalState> states = null;

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@Transient
	@CheckForNull
	private Set<CategoricalState> xmlStates = null;

	/**
	 * Tells us that this {@code CategoricalCell} has been unmarshalled and
	 * still needs to have {@code states} populated with {@code xmlStates}.
	 */
	@Transient
	private boolean xmlStatesNeedsToBePutIntoStates = false;

	/** No-arg constructor for (at least) Hibernate. */
	CategoricalCell() {}

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
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
	public boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {

		// Let's not marshal it if it's in a bad state
		checkState(getType() != null, "can't marshal a cell without a type");

		getXmlStates().addAll(getStates());
		return super.beforeMarshal(marshaller);

	}

	private void checkIncomingState(final CharacterState characterState) {

		checkNotNull(characterState);

		final CharacterStateRow row = getRow();

		checkState(row != null && getPosition() != null,
				"this cell has not been assigned a row");

		final Integer position = getPosition();

		final CharacterStateMatrix matrix = row.getMatrix();

		checkState(matrix != null,
				"this cell's row has not had a matrix assigned");

		checkState(matrix.getColumnsSize() >= position,
				"this cell's column hasn't been assigned a character");

// checkState(null != matrix.getCharacters().get(position),
// "this cell's column hasn't been assigned a character");
//
// final AbstractCharacter thisCellsCharacter = matrix.getCharacters().get(
// position);
//
// checkArgument(state.getCharacter() != null,
// "state " + state.getLabel()
// + " hasn't been assigned to a AbstractCharacter");
//
// checkArgument(state.getCharacter().equals(thisCellsCharacter),
// "state is from the wrong AbstractCharacter. We want "
// + matrix.getCharacters().get(position)
// .getLabel() + " but got "
// + state.getCharacter().getLabel());

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
	@Override
	protected CategoricalState getFirstState() {
		return firstState;
	}

	/**
	 * Getter. This will return {@code null} until the cell is added to a row.
	 * 
	 * @return the {@code CharacterStateRow} to which this {@code
	 *         CategoricalCell} belongs
	 */
	@Nullable
	public CharacterStateRow getRow() {
		return row;
	}

	@CheckForNull
	@Override
	protected Set<CategoricalState> getStates() {
		return states;
	}

	/**
	 * The states for marhsalling
	 * 
	 * @return the states for marhsalling
	 */
	@XmlElement(name = "stateDocId")
	@XmlIDREF
	@Override
	protected Set<CategoricalState> getXmlStates() {
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
	public Iterator<CategoricalState> iterator() {
		return Collections.unmodifiableSet(getStates()).iterator();
	}

	/**
	 * Set this cell's type to {@link Type#INAPPLICABLE} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public CategoricalCell setInapplicable() {

		@SuppressWarnings("unchecked")
		final Set<CategoricalState> emptyStates = Collections.EMPTY_SET;
		setTypeAndStates(Type.INAPPLICABLE, emptyStates);
		return this;
	}

	@Override
	public CategoricalCell setInNeedOfNewPPodVersionInfo() {
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
	public CategoricalCell setPolymorphicStates(
			final Set<? extends CategoricalState> polymorphicStates) {
		checkNotNull(polymorphicStates);
		checkArgument(polymorphicStates.size() > 1,
				"polymorphic states must be > 1");
		setTypeAndStates(Type.POLYMORPHIC, polymorphicStates);
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
	 * @return this {@code CategoricalCell}
	 */
	protected CategoricalCell setRow(
			@CheckForNull final CharacterStateRow row) {
		this.row = row;
		return this;
	}

	/**
	 * Set the cell to have type {@link Type#SINGLE} and the given states.
	 * 
	 * @param state state to assign to this cell
	 * 
	 * @return this
	 */
	public CategoricalCell setSingleState(final CategoricalState state) {
		checkNotNull(state);
		setTypeAndStates(Type.SINGLE, newHashSet(state));
		return this;
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
	@Override
	protected CategoricalCell setTypeAndStates(final Type type,
			final Set<? extends CategoricalState> states) {
		checkNotNull(type);
		checkNotNull(states);

		if (this.states == null) {
			this.states = newTreeSet(STATE_COMPARATOR);
		}

		// So FindBugs knows that we got it when it wasn't null
		final Set<CategoricalState> thisStates = this.states;

		if (getType() != null && getType().equals(type)
				&& states.equals(getStates())) {
			return this;
		}

		for (final CharacterState characterState : states) {
			checkIncomingState(characterState);
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
	 */
	CategoricalCell setTypeAndXmlStates(final Cell.Type type,
			final Set<? extends CategoricalState> xmlStates) {
		checkNotNull(type);
		checkNotNull(xmlStates);
		setType(type);
		this.xmlStates = newHashSet(xmlStates);
		return this;
	}

	/**
	 * Set this cell's type to {@link Type#UNASSIGNED} to {@code
	 * Collections.EMPTY_SET}.
	 * 
	 * @return this
	 */
	public CategoricalCell setUnassigned() {
		final Set<CategoricalState> emptyStates = Collections.emptySet();
		setTypeAndStates(Cell.Type.UNASSIGNED, emptyStates);
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
	public CategoricalCell setUncertainStates(
			final Set<? extends CategoricalState> uncertainStates) {
		checkNotNull(uncertainStates);
		checkArgument(uncertainStates.size() > 1,
				"uncertain states must be > 1");
		setTypeAndStates(Cell.Type.UNCERTAIN, uncertainStates);
		return this;
	}

	/**
	 * Created for testing purposes.
	 */
	CategoricalCell setXmlStatesNeedsToBePutIntoStates(
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

		retValue.append("CategoricalCell(").append(super.toString()).append(
				TAB).append("version=").append(TAB).append("states=").append(
				this.states).append(TAB).append(")");

		return retValue.toString();
	}

	@Override
	protected Cell<CategoricalState> unsetXmlStates() {
		this.xmlStates = null;
		return this;
	}
}
