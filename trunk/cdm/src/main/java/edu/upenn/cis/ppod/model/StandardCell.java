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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@Access(AccessType.PROPERTY)
public class StandardCell extends Cell<StandardState> {

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
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@CheckForNull
	private StandardRow row;

	/** No-arg constructor for (at least) Hibernate. */
	StandardCell() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
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
		row = (StandardRow) parent;
	}

	private void checkRowMatrixCharacter() {

		final StandardRow row = getRow();

		checkState(row != null && getPosition() != null,
				"this cell has not been assigned a row");

		final Integer position = getPosition();

		final StandardMatrix matrix = row.getMatrix();

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
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = StandardState.JOIN_COLUMN)
	@Override
	protected StandardState getElement() {
		return super.getElement();
	}

	@CheckForNull
	@ManyToMany
	@JoinTable(inverseJoinColumns =
			@JoinColumn(name = StandardState.JOIN_COLUMN))
	@Override
	protected Set<StandardState> getElementsRaw() {
		return super.getElementsRaw();
	}

	@CheckForNull
	@XmlElement(name = "stateDocId")
	@XmlIDREF
	@Transient
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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = StandardRow.JOIN_COLUMN)
	@Override
	public StandardRow getRow() {
		return row;
	}

	/**
	 * For JAXB.
	 */
	@Override
	protected void setElement(final StandardState state) {
		super.setElement(state);
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
	protected void setPolymorphicOrUncertain(
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
					getRow().getMatrix().getCharacters().get(position);

		newElements = newHashSet();

		for (final StandardState sourceElement : elements) {
			newElements
						.add(character.getState(sourceElement
								.getStateNumber()));
		}

		super.setPolymorphicOrUncertain(type, newElements);

		return;
	}

	/**
	 * Set or unset the row to which this cell belongs.
	 * <p>
	 * Intentionally package-private.
	 * 
	 * @param row value, {@code null} to indicate that the cell is removed from
	 *            the row
	 */
	protected void setRow(@CheckForNull final StandardRow row) {
		this.row = row;
	}

	@Override
	public Cell<StandardState> setSingleElement(final StandardState element) {

		checkNotNull(element);

		checkState(
					getPosition() != null,
					"this cell has not been assigned a row: it's position attribute is null");

		final StandardCharacter standardCharacter =
					getRow().getMatrix().getCharacters().get(getPosition());

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

		super.setSingleElement(newElement);

		setInNeedOfNewVersion();
		return this;
	}

	@Override
	public void unsetRow() {
		row = null;
	}
}
