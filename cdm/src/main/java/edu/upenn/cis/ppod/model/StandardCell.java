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
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStandardState;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A cell in a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardCell.TABLE)
public class StandardCell
		extends Cell<IStandardState, IStandardRow>
		implements IStandardCell {

	public static class Adapter extends XmlAdapter<StandardCell, IStandardCell> {

		@Override
		public StandardCell marshal(final IStandardCell cell) {
			return (StandardCell) cell;
		}

		@Override
		public IStandardCell unmarshal(final StandardCell cell) {
			return cell;
		}
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
	 * To handle the most-common case of a single state.
	 * <p>
	 * Will be {@code null} if type is not {@link Type.SINGLE}.
	 */
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = StandardState.class)
	@JoinColumn(name = StandardState.JOIN_COLUMN)
	@Nullable
	private IStandardState element;

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@ManyToMany(targetEntity = StandardState.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(
			name = StandardState.JOIN_COLUMN))
	@Nullable
	private Set<IStandardState> elements;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false,
			targetEntity = StandardRow.class)
	@JoinColumn(name = StandardRow.JOIN_COLUMN)
	@CheckForNull
	private IStandardRow parent;

	@Transient
	private Set<StandardState> elementsXml;

	/** No-arg constructor for (at least) Hibernate. */
	StandardCell() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitStandardCell(this);
	}

	protected boolean afterMarshal(@Nullable final Marshaller marshaller) {
		elementsXml = null;
		return true;
	}

	public void afterUnmarshal() {
		if (getType() == Type.POLYMORPHIC || getType() == Type.UNCERTAIN) {
			initElements();
			for (final IStandardState elementXml : elementsXml) {
				getElementsModifiable().add(elementXml);
			}
		}
		elementsXml = null;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@Override
	protected void afterUnmarshal(final Unmarshaller u, final Object parent) {
		this.parent = (IStandardRow) parent;
	}

	@Override
	protected boolean beforeMarshal(@Nullable final Marshaller marshaller) {
		if (getType() == Type.POLYMORPHIC || getType() == Type.UNCERTAIN) {
			this.elementsXml = newHashSet();
			for (final IStandardState element : elements) {
				// Load it if it's a proxy ;-)
				element.getStateNumber();
				this.elementsXml.add((StandardState) element);
			}
		}
		return true;
	}

	@Override
	protected void beforeUnmarshal(final Unmarshaller u, final Object parent) {
		elementsXml = newHashSet();
	}

	private void checkRowMatrixCharacter() {

		final IStandardRow row = getParent();

		checkState(row != null && getPosition() != null,
				"this cell has not been assigned a row");

		final Integer position = getPosition();

		final IStandardMatrix matrix = row.getParent();

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

	@XmlAttribute(name = "stateDocId")
	@XmlIDREF
	@Override
	protected IStandardState getElement() {
		return element;
	}

	@Override
	Set<IStandardState> getElementsModifiable() {
		return elements;
	}

	/**
	 * We needed to create this instead of serializing a Set<IStandardState}
	 * because the interfaces didn't work with
	 * 
	 * @XmlIdREF
	 */
	@XmlElement(name = "stateDocId")
	@XmlIDREF
	protected Set<StandardState> getElementsXml() {
		return elementsXml;
	}

	/**
	 * Getter. This will return {@code null} until the cell is added to a row.
	 * 
	 * @return the {@code CharacterStateRow} to which this
	 *         {@code CharacterStateCell} belongs
	 */
	@Override
	public IStandardRow getParent() {
		return parent;
	}

	private Set<IStandardState> getStates(final Set<Integer> stateNumbers) {
		final Set<IStandardState> states = newHashSet();
		final IStandardCharacter character = getParent().getParent()
				.getCharacters().get(getPosition());
		for (final Integer stateNumber : stateNumbers) {
			final IStandardState state = character.getState(stateNumber);
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
			@Nullable final IStandardState element) {
		this.element = element;
	}

	@Override
	void setElements(
			@Nullable final Set<IStandardState> elements) {
		this.elements = elements;
	}

	/** {@inheritDoc} */
	public void setParent(final IStandardRow parent) {
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
			final Set<? extends IStandardState> elements) {
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

		checkRowMatrixCharacter();

		super.setPolymorphicOrUncertain(type, elements);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throw IllegalArgumentException if {@code elements.size() < 2}
	 */
	public void setPolymorphicWithStateNos(
			final Set<Integer> stateNumbers) {
		checkNotNull(stateNumbers);
		checkArgument(
				stateNumbers.size() > 1,
				"polymorphic states must be > 1");
		setPolymorphicOrUncertain(Type.POLYMORPHIC, getStates(stateNumbers));
	}

	/** {@inheritDoc} */
	public void setSingleWithStateNo(final Integer stateNumber) {

		checkNotNull(stateNumber);

		checkState(
					getPosition() != null,
					"this cell has not been assigned a row: it's position attribute is null");

		final IStandardCharacter character =
				getParent()
						.getParent()
						.getCharacters()
						.get(getPosition());

		checkState(character != null,
				"no character has been assigned for column " + getPosition());

		final IStandardState state = character.getState(stateNumber);

		checkArgument(
				state != null,
				"This matrix doesn't have a state number "
						+ stateNumber + " for character ["
						+ character.getLabel()
						+ "]");

		if (state.equals(getElement())) {
			if (getType() != Type.SINGLE) {
				throw new AssertionError(
						"element is set, but this cell is not a SINGLE");
			}
		} else {
			setElement(state);
			setElements(null);
			setType(Type.SINGLE);
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setUncertainWithStateNos(
			final Set<Integer> stateNumbers) {
		checkNotNull(stateNumbers);
		checkArgument(
				stateNumbers.size() > 1,
				"polymorphic states must be > 1");
		setPolymorphicOrUncertain(Type.UNCERTAIN, getStates(stateNumbers));
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
