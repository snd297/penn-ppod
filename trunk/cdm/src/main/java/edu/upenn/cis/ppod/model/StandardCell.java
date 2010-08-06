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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A cell in a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardCell.TABLE)
public class StandardCell extends Cell<StandardState, StandardRow> {

	/**
	 * Set the type to polymorphic with the appropriate states equivalent to
	 * {@code states}.
	 * <p>
	 * Note that the elements that are actually assigned may or may not be the
	 * {@code ==} to the elements passed in, but the cell will be set to
	 * equivalent (not necessarily {@code .equals()}) elements.
	 * 
	 * @param elements the elements
	 * 
	 * @return this
	 * 
	 * @throw IllegalArgumentException if {@code polymorphicStates.size() < 2}
	 */
	public StandardCell setPolymorphicElements(
			final Set<? extends StandardState> elements) {
		checkNotNull(elements);
		checkArgument(elements.size() > 1,
				"polymorphic states must be > 1");
		setPolymorphicOrUncertain(Type.POLYMORPHIC, elements);
		return this;
	}

	/**
	 * The name of the table.
	 */
	public static final String TABLE = "STANDARD_CELL";

	/**
	 * Conventionally used as the names of foreign keys that point at the
	 * {@code CharacterStateCell} table.
	 */
	public static final String JOIN_COLUMN = TABLE + "_ID";

	/**
	 * To handle the most-common case of a single {@code CharacterState}, we
	 * cache {@code states.get(0)}.
	 * <p>
	 * Will be {@code null} if this is a {@link Type#INAPPLICABLE} or
	 * {@link Type#UNASSIGNED}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = StandardState.JOIN_COLUMN)
	@CheckForNull
	private StandardState element;

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@CheckForNull
	@ManyToMany
	@JoinTable(inverseJoinColumns = @JoinColumn(
			name = StandardState.JOIN_COLUMN))
	private Set<StandardState> elements;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = StandardRow.JOIN_COLUMN)
	@CheckForNull
	private StandardRow parent;

	/** No-arg constructor for (at least) Hibernate. */
	StandardCell() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardCell(this);
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	protected void afterUnmarshal(final Unmarshaller u, final Object parent) {
		super.afterUnmarshal(u, parent);
		this.parent = (StandardRow) parent;
	}

	private void checkRowMatrixCharacter() {

		final StandardRow row = getParent();

		checkState(row != null && getPosition() != null,
				"this cell has not been assigned a row");

		final Integer position = getPosition();

		final StandardMatrix matrix = row.getParent();

		checkState(matrix != null,
				"this cell's row has not had a matrix assigned");

		if (matrix.getColumnsSize() <= position) {
			throw new AssertionError(
					"position "
							+ position
							+ " is >= than the number of columns in the owning matrix"
							+ matrix.getColumnsSize());
		}

		checkState(null != matrix.getCharacters().get(position),
				"this cell's column hasn't been assigned a character");

	}

	@CheckForNull
	@XmlAttribute(name = "stateDocId")
	@XmlIDREF
	@Override
	protected StandardState getElement() {
		return element;
	}

	@CheckForNull
	@Override
	Set<StandardState> getElementsModifiable() {
		return elements;
	}

	@CheckForNull
	@XmlElement(name = "stateDocId")
	@XmlIDREF
	@Override
	protected Set<StandardState> getElementsXml() {
		return super.getElementsXml();
	}

	/**
	 * Getter. This will return {@code null} until the cell is added to a row.
	 * 
	 * @return the {@code CharacterStateRow} to which this
	 *         {@code CharacterStateCell} belongs
	 */
	@Nullable
	@Override
	public StandardRow getParent() {
		return parent;
	}

	/** Protected for JAXB. */
	@Override
	protected void setElement(
			@CheckForNull final StandardState element) {
		this.element = element;
	}

	@Override
	void setElements(
			@CheckForNull final Set<StandardState> elements) {
		this.elements = elements;
	}

	@Override
	void setParent(final StandardRow parent) {
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
			final Type type,
			final Set<? extends StandardState> elements) {
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

		Set<StandardState> newElements;

		checkRowMatrixCharacter();

		// So FindBugs knows we got it
		final Integer position = getPosition();

		checkState(
					position != null,
					"this cell has not been assigned a row: it's position attribute is null");

		final StandardCharacter character =
					getParent().getParent().getCharacters().get(position);

		newElements = newHashSet();

		for (final StandardState sourceElement : elements) {
			newElements
						.add(character.getState(sourceElement
								.getStateNumber()));
		}
		super.setPolymorphicOrUncertain(type, newElements);
	}

	public StandardCell setSingleElement(final StandardState element) {

		checkNotNull(element);

		checkState(
					getPosition() != null,
					"this cell has not been assigned a row: it's position attribute is null");

		final StandardCharacter standardCharacter =
					getParent().getParent().getCharacters().get(getPosition());

		final StandardState newElement =
				standardCharacter.getState(element.getStateNumber());

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

		retValue.append("CharacterStateCell(")
				.append(super.toString())
				.append(TAB)
				.append("version=")
				.append(TAB)
				.append("states=")
				.append(this.elements).append(TAB).append(")");

		return retValue.toString();
	}

}
