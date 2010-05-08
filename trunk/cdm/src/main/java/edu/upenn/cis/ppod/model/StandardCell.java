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
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A cell in a {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StandardCell.TABLE)
public class StandardCell extends Cell<CharacterState> {

	/**
	 * The name of the table.
	 */
	public static final String TABLE = "STANDARD_CELL";

	/**
	 * Conventionally used as the names of foreign keys that point at the
	 * {@code CharacterStateCell} table.
	 */
	public static final String JOIN_COLUMN = TABLE + "_ID";

	private static final Comparator<CharacterState> STATE_COMPARATOR = new CharacterState.CharacterStateComparator();

	/**
	 * The heart of the cell: the states.
	 * <p>
	 * Will be {@code null} when first created, but is generally not-null.
	 */
	@ManyToMany
	@Sort(type = SortType.COMPARATOR, comparator = CharacterState.CharacterStateComparator.class)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = CharacterState.ID_COLUMN))
	@CheckForNull
	private SortedSet<CharacterState> elements = null;

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
	private CharacterState firstElement;

	/**
	 * The {@code CharacterStateRow} to which this {@code CharacterStateCell}
	 * belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = StandardRow.JOIN_COLUMN)
	@CheckForNull
	private StandardRow row;

	/**
	 * Used for serialization so we don't have to hit {@code states} directly
	 * and thereby cause unwanted database hits.
	 */
	@Transient
	@CheckForNull
	private Set<CharacterState> xmlElements = null;

	/** No-arg constructor for (at least) Hibernate. */
	StandardCell() {}

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
		row = (StandardRow) parent; // don't setRow call because it'll
		// reset the ppod version info
	}

	private void checkIncomingState(final CharacterState state) {

		checkNotNull(state);

		final StandardRow row = getRow();

		checkState(row != null && getPosition() != null,
				"this cell has not been assigned a row");

		final Integer position = getPosition();

		final StandardMatrix matrix = row.getMatrix();

		checkState(matrix != null,
				"this cell's row has not had a matrix assigned");

		checkState(matrix.getCharacters().size() >= position,
				"this cell's column hasn't been assigned a character");

		checkState(null != matrix.getCharacters().get(position),
				"this cell's column hasn't been assigned a character");

		final Character thisCellsCharacter = matrix.getCharacters().get(
				position);

		checkArgument(state.getCharacter() != null,
				"state " + state.getLabel()
						+ " hasn't been assigned to a Character");

		checkArgument(state.getCharacter().equals(thisCellsCharacter),
				"state is from the wrong Character. We want "
						+ matrix.getCharacters().get(position)
								.getLabel() + " but got "
						+ state.getCharacter().getLabel());

	}

	@CheckForNull
	@Override
	protected Set<CharacterState> getElementsRaw() {
		return elements;
	}

	@Override
	protected CharacterState getFirstElement() {
		return firstElement;
	}

	/**
	 * Getter. This will return {@code null} until the cell is added to a row.
	 * 
	 * @return the {@code CharacterStateRow} to which this {@code
	 *         CharacterStateCell} belongs
	 */
	@Nullable
	@Override
	public StandardRow getRow() {
		return row;
	}

	/**
	 * The state set that will be marshalled.
	 * 
	 * @return the states for marhsalling
	 */
	@XmlElement(name = "stateDocId")
	@XmlIDREF
	@Override
	protected Set<CharacterState> getXmlElements() {
		if (xmlElements == null) {
			xmlElements = newHashSet();
		}
		return xmlElements;
	}

	/**
	 * Returns an iterator over this cell's states. Guaranteed to iterator in
	 * {@link CharacterState#getStateNumber()} order.
	 * 
	 * @return an iterator over this cell's states
	 */
	public Iterator<CharacterState> iterator() {
		return Collections.unmodifiableSet(getElements()).iterator();
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
	protected StandardCell setRow(
			@CheckForNull final StandardRow row) {
		this.row = row;
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
	@Override
	protected StandardCell setTypeAndElements(final Type type,
			final Set<? extends CharacterState> states) {
		checkNotNull(type);
		checkNotNull(states);

		if (getElementsRaw() == null) {
			this.elements = newTreeSet(STATE_COMPARATOR);
		}

		// So FindBugs knows that we got it when it wasn't null
		final Set<CharacterState> thisStates = this.elements;

		if (getType() != null && getType().equals(type)
				&& states.equals(getElements())) {
			return this;
		}

		for (final CharacterState state : states) {
			checkIncomingState(state);
		}

		clearElements();

		thisStates.addAll(states);

		if (states.size() > 0) {
			firstElement = get(thisStates, 0);
		}

		setType(type);
		setInNeedOfNewPPodVersionInfo();
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
	protected Cell<CharacterState> unsetFirstElement() {
		this.firstElement = null;
		return this;
	}

	@Override
	protected Cell<CharacterState> unsetXmlElements() {
		this.xmlElements = null;
		return this;
	}

	@Override
	public StandardCell unsetRow() {
		row = null;
		return this;
	}

}
