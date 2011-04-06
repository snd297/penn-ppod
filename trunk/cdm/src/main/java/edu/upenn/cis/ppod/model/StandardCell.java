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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.imodel.IChild;

/**
 * A cell in a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardCell.TABLE)
public class StandardCell implements IChild<StandardRow> {

	/**
	 * The name of the table.
	 */
	public static final String TABLE = "STANDARD_CELL";

	/**
	 * Conventionally used as the names of foreign keys that point at the
	 * {@code CharacterStateCell} table.
	 */
	public static final String ID_COLUMN = TABLE + "_ID";

	@CheckForNull
	private Long id;

	@CheckForNull
	private Integer version;

	/**
	 * To handle the most-common case of a single state.
	 * <p>
	 * Will be {@code null} if type is not {@link Type#SINGLE}.
	 */
	@Nullable
	private StandardState state;

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} if type is not {@link Type#POLYMORPHIC} or
	 * {@link Type#UNCERTAIN}.
	 */
	private Set<StandardState> states = newHashSet();

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@CheckForNull
	private StandardRow parent;

	/** No-arg constructor for (at least) Hibernate. */
	public StandardCell() {}

	private void checkRowMatrixCharacter() {

		final StandardRow row = getParent();

		checkState(row != null && getPosition() != null,
				"this cell has not been assigned a row");

		final Integer position = getPosition();

		final StandardMatrix matrix = row.getParent();

		checkState(matrix != null,
				"this cell's row has not had a matrix assigned");

		checkState(null != matrix.getCharacters().get(position),
				"this cell's column hasn't been assigned a character");

	}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * Getter. This will return {@code null} until the cell is added to a row.
	 * 
	 * @return the {@code CharacterStateRow} to which this
	 *         {@code CharacterStateCell} belongs
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = StandardRow.ID_COLUMN)
	public StandardRow getParent() {
		return parent;
	}

	@SuppressWarnings("unused")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = StandardState.ID_COLUMN)
	@Nullable
	private StandardState getState() {
		return state;
	}

	@SuppressWarnings("unused")
	@ManyToMany
	@JoinTable(name = StandardCell.TABLE + "_" + StandardState.TABLE,
			joinColumns = @JoinColumn(
					name = ID_COLUMN),
			inverseJoinColumns = @JoinColumn(
					name = StandardState.ID_COLUMN))
	private Set<StandardState> getStates() {
		return states;
	}

	@SuppressWarnings("unused")
	private void setStates(final Set<StandardState> states) {
		this.states = states;
	}

	@Transient
	private Set<StandardState> getStatesByStateNumbers(
			final Set<Integer> stateNumbers) {
		final Set<StandardState> states = newHashSet();
		final StandardCharacter character = getParent().getParent()
				.getCharacters().get(getPosition());
		for (final Integer stateNumber : stateNumbers) {
			final StandardState state = character.getStates().get(stateNumber);
			checkArgument(
					state != null,
					"This matrix doesn't have a state number "
							+ stateNumber + " for character ["
							+ character.getLabel()
							+ "]");
			states.add(state);
		}
		return states;
	}

	/**
	 * @return the version
	 */
	@Version
	@Column(name = "OBJ_VERSION")
	@Nullable
	public Integer getVersion() {
		return version;
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	public void setParent(final StandardRow parent) {
		this.parent = parent;
	}

	/**
	 * Add a set of {@code CharacterState}s to this {@code CharacterStateCell}.
	 * 
	 * @param states to be added. Each must not be in a detached state.
	 * 
	 * @return {@code state}
	 */
	private void setPolymorphicOrUncertain(
			final PPodCellType type,
			final Set<StandardState> states) {
		checkNotNull(type);
		checkNotNull(states);

		checkArgument(
				type == PPodCellType.POLYMORPHIC
						|| type == PPodCellType.UNCERTAIN,
				" type is " + type + " but must be POLYMORPHIC OR UNCERTAIN");

		checkArgument(
				states.size() > 1,
				"POLYMORPIC AND UNCERTAIN must have greater than 1 element but elements has "
						+ states.size());

		checkRowMatrixCharacter();

		this.type = type;
		this.state = null;
		this.states.clear();
		this.states.addAll(states);
	}

	/**
	 * Set the cell's type to {@link Type.POLYMORPHIC} and its states to contain
	 * only the states with the given state numbers. The states are pulled from
	 * this column's character, so it is not legal to call this method if this
	 * cell is not part of a matrix with a character in the column.
	 * 
	 * @param stateNumbers the state numbers of the states we want
	 * 
	 * @throw IllegalArgumentException if {@code elements.size() < 2}
	 */
	public void setPolymorphic(
			final Set<Integer> stateNumbers) {
		checkNotNull(stateNumbers);
		checkArgument(
				stateNumbers.size() > 1,
				"polymorphic states must be > 1");
		setPolymorphicOrUncertain(PPodCellType.POLYMORPHIC,
				getStatesByStateNumbers(stateNumbers));
	}

	/**
	 * Set the cell's type to {@link Type.SINGLE} and its states to contain only
	 * the state with the given state number. The state is pulled from this
	 * column's character, so it is not legal to call this method if this cell
	 * is not part of a matrix with a character in the column.
	 * 
	 * @param stateNumber the state number of the state we want
	 */
	public void setSingle(final Integer stateNumber) {

		checkNotNull(stateNumber);

		if (getType() == PPodCellType.SINGLE
				&& this.state.getStateNumber().equals(stateNumber)) {
			// We're already good, so let's not do anything.
			// Since this is the most common case, it's worth doing: gives
			// us a 50% improvement on larger matrices.
			return;
		}

		checkState(
					getPosition() != null,
					"this cell has not been assigned a row: it's position attribute is null");

		final StandardCharacter character =
				getParent()
						.getParent()
						.getCharacters()
						.get(getPosition());

		checkState(character != null,
				"no character has been assigned for column " + getPosition());

		final StandardState state = character.getStates().get(stateNumber);

		checkArgument(
				state != null,
				"This matrix doesn't have a state number "
						+ stateNumber + " for character ["
						+ character.getLabel()
						+ "]");

		type = PPodCellType.SINGLE;
		this.state = state;
		this.states.clear();
	}

	/**
	 * Set the cell's type to {@link Type.UNCERTAIN} and its states to contain
	 * only the states with the given state numbers. The states are pulled from
	 * this column's character, so it is not legal to call this method if this
	 * cell is not part of a matrix with a character in the column.
	 * 
	 * @param stateNumbers the state numbers of the states we want
	 */
	public void setUncertain(
			final Set<Integer> stateNumbers) {
		checkNotNull(stateNumbers);
		checkArgument(
				stateNumbers.size() > 1,
				"polymorphic states must be > 1");
		setPolymorphicOrUncertain(PPodCellType.UNCERTAIN,
				getStatesByStateNumbers(stateNumbers));
	}

	/**
	 * @param version the version to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}

	public static final String TYPE_COLUMN = "TYPE";

	@CheckForNull
	private Integer position;

	@CheckForNull
	private PPodCellType type;

	/**
	 * Don't modify the returned collection - that's undefined.
	 * 
	 * @throws IllegalStateException if the type has not been set for this cell,
	 *             i.e. if {@link #getType() == null}
	 */
	@Transient
	public Set<StandardState> getStatesSmartly() {
		checkState(type != null,
				"type has yet to be assigned for this cell");

		switch (type) {
			// Don't hit states unless we have to
			case INAPPLICABLE:
			case UNASSIGNED:
				return Collections.emptySet();
			case SINGLE:
				if (state == null) {
					throw new AssertionError(
							"getElement() == null for SINGLE cell!");
				}
				final Set<StandardState> elementInASet = newHashSet();
				elementInASet.add(state);
				return elementInASet;
			case POLYMORPHIC:
			case UNCERTAIN:

				// We have to hit states, which we want to avoid as much as
				// possible since it will trigger a database hit, which in the
				// aggregate
				// is expensive since there're are so many cells.

				if (states == null) {
					throw new AssertionError(
							"elements is null in a POLYMORPHIC or UNCERTAIN cell");
				}

				if (states.size() < 2) {
					throw new AssertionError("type is "
														+ getType()
												+ " and getElements() has "
												+ states.size() + " elements");
				}
				return states;

			default:
				throw new AssertionError("Unknown Cell.Type: " + type);
		}
	}

	/** {@inheritDoc} */
	@Column(name = "POSITION", nullable = false)
	@Nullable
	public Integer getPosition() {
		return position;
	}

	/**
	 * Get the type of this cell.
	 * <p>
	 * This value will be {@code null} for newly created cells until the
	 * elements are set.
	 * <p>
	 * This value will never be {@code null} for a persistent cell.
	 * 
	 * @return the type of this cell
	 */
	@Column(name = TYPE_COLUMN, nullable = false)
	@Enumerated(EnumType.ORDINAL)
	@Nullable
	public PPodCellType getType() {
		return type;
	}

	/**
	 * Set this cell's type to {@link Type#INAPPLICABLE}, its elements to the
	 * empty set.
	 */
	public void setInapplicable() {
		setInapplicableOrUnassigned(PPodCellType.INAPPLICABLE);
	}

	private void setInapplicableOrUnassigned(final PPodCellType type) {
		checkArgument(
				type == PPodCellType.INAPPLICABLE
						|| type == PPodCellType.UNASSIGNED,
				"type was " + type + " but must be INAPPLICABLE or UNASSIGNED");

		if (type == getType()) {
			return;
		}
		setType(type);
		state = null;
		states.clear();
		return;
	}

	/**
	 * Set the position of this child.
	 * <p>
	 * Use a {@code null} when removing a child from its parent
	 * 
	 * @param position the position of this child
	 * 
	 * @throw IllegalArgumentException if
	 *        {@code position !=null && position < 0}
	 */
	void setPosition(@CheckForNull final Integer position) {
		checkArgument(position == null || position >= 0, "position < 0");
		this.position = position;
	}

	private void setType(final PPodCellType type) {
		this.type = type;
	}

	/**
	 * Set this cell's type to {@link Type#UNASSIGNED}, its elements to the
	 * empty set.
	 */
	public void setUnassigned() {
		setInapplicableOrUnassigned(PPodCellType.UNASSIGNED);
	}

}
