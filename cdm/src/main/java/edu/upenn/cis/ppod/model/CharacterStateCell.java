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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A cell in a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = CharacterStateCell.TABLE)
public class CharacterStateCell extends Cell<CharacterState> {

	/**
	 * The name of the table.
	 */
	public static final String TABLE = "CHARACTER_STATE_CELL";

	/**
	 * Conventionally used as the names of foreign keys that point at the
	 * {@code CharacterStateCell} table.
	 */
	public static final String JOIN_COLUMN = TABLE + "_ID";

	private static final Comparator<CharacterState> STATE_COMPARATOR = new CharacterState.CharacterStateComparator();

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = CharacterState.ID_COLUMN)
	@CheckForNull
	private CharacterState element;

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@CheckForNull
	@ManyToMany
	@Sort(type = SortType.COMPARATOR, comparator = CharacterState.CharacterStateComparator.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = CharacterState.ID_COLUMN))
	private SortedSet<CharacterState> elements = null;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = CharacterStateRow.JOIN_COLUMN)
	@CheckForNull
	private CharacterStateRow row;

	/** No-arg constructor for (at least) Hibernate. */
	CharacterStateCell() {}

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
		row = (CharacterStateRow) parent;
	}

	private void checkRowMatrixCharacter() {

		final CharacterStateRow row = getRow();

		checkState(row != null && getPosition() != null,
				"this cell has not been assigned a row");

		final Integer position = getPosition();

		final CharacterStateMatrix matrix = row.getMatrix();

		checkState(matrix != null,
				"this cell's row has not had a matrix assigned");

		checkState(matrix.getCharactersModifiable().size() >= position,
				"this cell's column hasn't been assigned a character");

		checkState(null != matrix.getCharactersModifiable().get(position),
				"this cell's column hasn't been assigned a character");

	}

	@CheckForNull
	@XmlAttribute(name = "stateDocId")
	@XmlIDREF
	@Override
	protected CharacterState getElement() {
		return element;
	}

	@CheckForNull
	@Override
	protected Set<CharacterState> getElementsModifiable() {
		return elements;
	}

	@CheckForNull
	@XmlElement(name = "stateDocId")
	@XmlIDREF
	@Override
	protected Set<CharacterState> getElementsXml() {
		return super.getElementsXml();
	}

	/**
	 * Getter. This will return {@code null} until the cell is added to a row.
	 * 
	 * @return the {@code CharacterStateRow} to which this {@code
	 *         CharacterStateCell} belongs
	 */
	@Nullable
	@Override
	public CharacterStateRow getRow() {
		return row;
	}

	@Override
	protected void initElements() {
		elements = newTreeSet(STATE_COMPARATOR);
	}

	@Override
	protected Cell<CharacterState> setElement(
			@CheckForNull final CharacterState element) {
		this.element = element;
		return this;
	}

	@Override
	protected Cell<CharacterState> setElements(
			@CheckForNull final Set<CharacterState> elements) {
		if (equal(elements, this.elements)) {

		} else {
			if (elements == null) {
				this.elements = null;
			} else {
				if (this.elements == null) {
					initElements();
				} else {
					this.elements.clear();
				}
				this.elements.addAll(elements);
			}
		}
		return this;
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
	protected CharacterStateCell setPolymorphicOrUncertain(
			final Type type,
			final Set<CharacterState> elements) {
		checkNotNull(type);
		checkNotNull(elements);

		checkArgument(
				type == Type.POLYMORPHIC
						|| type == Type.UNCERTAIN,
				" type is " + type + " but must be POLYMORPHIC OR UNCERTAIN");

		checkArgument(
				elements.size() > 1,
				"POLYMORPIC AND UNCERTAIN must have greater than 1 element but elements has "
						+ elements.size());

		Set<CharacterState> newElements;

		checkRowMatrixCharacter();

		// So FindBugs knows we got it
		final Integer position = getPosition();

		checkState(
					position != null,
					"this cell has not been assigned a row: it's position attribute is null");

		final Character character =
					getRow().getMatrix().getCharacters().get(position);

		newElements = newHashSet();

		for (final CharacterState sourceElement : elements) {
			newElements
						.add(character.getState(sourceElement.getStateNumber()));
		}

		if (getType() != null
				&& getType().equals(type)
				&& newElements.equals(this.elements)) {
			return this;
		}

		setElement(null);
		setElements(newElements);
		setType(type);
		setInNeedOfNewVersion();
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
	protected CharacterStateCell setRow(
			@CheckForNull final CharacterStateRow row) {
		this.row = row;
		return this;
	}

	@Override
	public Cell<CharacterState> setSingleElement(final CharacterState element) {

		checkNotNull(element);

		// So FindBugs knows we got it
		final Integer position = getPosition();

		checkState(
					position != null,
					"this cell has not been assigned a row: it's position attribute is null");

		final Character character =
					getRow().getMatrix().getCharacters().get(position);

		final CharacterState newElement =
				character.getState(element.getStateNumber());

		checkState(newElement != null,
				"cell's character has no state for element "
						+ element.getLabel() + ", state number "
						+ element.getStateNumber());

		if (newElement.equals(getElement())) {
			if (getType() != Type.SINGLE) {
				throw new AssertionError(
						"element is set, but this cell is not a SINGLE");
			}
			return this;
		}

		setElement(newElement);
		setElements(null);
		setType(Type.SINGLE);
		setInNeedOfNewVersion();
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
				this.elements).append(TAB).append(")");

		return retValue.toString();
	}

	@Override
	public CharacterStateCell unsetRow() {
		row = null;
		return this;
	}

}
