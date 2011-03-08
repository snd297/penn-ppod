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

import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.PPodCellType;

/**
 * A cell in a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardCell.TABLE)
public class StandardCell extends Cell<StandardState, StandardRow> {

	@Access(AccessType.PROPERTY)
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@CheckForNull
	private Long id;

	@SuppressWarnings("unused")
	@Version
	@Column(name = "OBJ_VERSION")
	@CheckForNull
	private Integer objVersion;

	/**
	 * The name of the table.
	 */
	public static final String TABLE = "STANDARD_CELL";

	/**
	 * Conventionally used as the names of foreign keys that point at the
	 * {@code CharacterStateCell} table.
	 */
	public static final String ID_COLUMN = TABLE + "_ID";

	/**
	 * To handle the most-common case of a single state.
	 * <p>
	 * Will be {@code null} if type is not {@link Type#SINGLE}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = StandardState.ID_COLUMN)
	@Nullable
	private StandardState state;

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} if type is not {@link Type#POLYMORPHIC} or
	 * {@link Type#UNCERTAIN}.
	 */
	@ManyToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(
			name = StandardState.ID_COLUMN))
	@Nullable
	private Set<StandardState> states;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = StandardRow.ID_COLUMN)
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

	@Override
	protected StandardState getElement() {
		return state;
	}

	@Override
	Set<StandardState> getElementsModifiable() {
		return states;
	}

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
	@Override
	public StandardRow getParent() {
		return parent;
	}

	private Set<StandardState> getStates(final Set<Integer> stateNumbers) {
		final Set<StandardState> states = newHashSet();
		final StandardCharacter character = getParent().getParent()
				.getCharacters().get(getPosition());
		for (final Integer stateNumber : stateNumbers) {
			final StandardState state = character.getState(stateNumber);
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

	/** Protected for JAXB. */
	@Override
	protected void setElement(
				final StandardState element) {
		this.state = element;
	}

	@Override
	void setElements(
			@Nullable final Set<StandardState> elements) {
		this.states = elements;
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
	 * <p>
	 * Makes no assumption about the hibernate-state of {@code states} (could be
	 * transient, persistent, detached). Because it looks up the actual state to
	 * hang on to through {@code getRow().getMatrix().getCharacter(
	 * getPosition()).getState(...)}.
	 * 
	 * @param states to be added. Each must not be in a detached state.
	 * 
	 * @return {@code state}
	 */
	@Override
	void setPolymorphicOrUncertain(
			final PPodCellType type,
			final Set<StandardState> elements) {
		checkNotNull(type);
		checkNotNull(elements);

		checkArgument(
				type == PPodCellType.POLYMORPHIC
						|| type == PPodCellType.UNCERTAIN,
				" type is " + type + " but must be POLYMORPHIC OR UNCERTAIN");

		checkArgument(
				elements.size() > 1,
				"POLYMORPIC AND UNCERTAIN must have greater than 1 element but elements has "
						+ elements.size());

		checkRowMatrixCharacter();

		super.setPolymorphicOrUncertain(type, elements);
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
	public void setPolymorphicWithStateNos(
			final Set<Integer> stateNumbers) {
		checkNotNull(stateNumbers);
		checkArgument(
				stateNumbers.size() > 1,
				"polymorphic states must be > 1");
		setPolymorphicOrUncertain(PPodCellType.POLYMORPHIC,
				getStates(stateNumbers));
	}

	/**
	 * Set the cell's type to {@link Type.SINGLE} and its states to contain only
	 * the state with the given state number. The state is pulled from this
	 * column's character, so it is not legal to call this method if this cell
	 * is not part of a matrix with a character in the column.
	 * 
	 * @param stateNumber the state number of the state we want
	 */
	public void setSingleWithStateNo(final Integer stateNumber) {

		checkNotNull(stateNumber);

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

		final StandardState state = character.getState(stateNumber);

		checkArgument(
				state != null,
				"This matrix doesn't have a state number "
						+ stateNumber + " for character ["
						+ character.getLabel()
						+ "]");

		if (state.equals(getElement())) {
			if (getType() != PPodCellType.SINGLE) {
				throw new AssertionError(
						"element is set, but this cell is not a SINGLE");
			}
		} else {
			super.setSingle(state);
		}
	}

	/**
	 * Set the cell's type to {@link Type.UNCERTAIN} and its states to contain
	 * only the states with the given state numbers. The states are pulled from
	 * this column's character, so it is not legal to call this method if this
	 * cell is not part of a matrix with a character in the column.
	 * 
	 * @param stateNumbers the state numbers of the states we want
	 */
	public void setUncertainWithStateNos(
			final Set<Integer> stateNumbers) {
		checkNotNull(stateNumbers);
		checkArgument(
				stateNumbers.size() > 1,
				"polymorphic states must be > 1");
		setPolymorphicOrUncertain(PPodCellType.UNCERTAIN,
				getStates(stateNumbers));
	}
}
