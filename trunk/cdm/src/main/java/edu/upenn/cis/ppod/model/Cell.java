/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.imodel.IChild;

/**
 * A cell in a matrix
 * 
 * @author Sam Donnelly
 * 
 * @param <E> the elements in the cell
 * @param <R> the parent row type
 */
@MappedSuperclass
abstract class Cell<E, R extends Row<?, ?>>
		extends PPodEntity
		implements IChild<R> {

	static final String TYPE_COLUMN = "TYPE";

	@Column(name = "POSITION", nullable = false)
	@Nullable
	private Integer position;

	@Column(name = TYPE_COLUMN, nullable = false)
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private PPodCellType type;

	protected Cell() {}

	/**
	 * Get the elements contained in this cell.
	 * <p>
	 * Will be {@code null} if this is cell is not {@link Type.SINGLE}.
	 * 
	 * @return the elements contained in this cell
	 */
	@Nullable
	abstract E getElement();

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException if the type has not been set for this cell,
	 *             i.e. if {@link #getType() == null}
	 */
	public Set<E> getElements() {
		// checkState(type != null,
		// "type has yet to be assigned for this cell");

		switch (getType()) {
			// Don't hit states unless we have to
			case INAPPLICABLE:
			case UNASSIGNED:
				return Collections.emptySet();
			case SINGLE:
				if (getElement() == null) {
					throw new AssertionError(
							"getElement() == null for SINGLE cell!");
				}
				final Set<E> elementInASet = newHashSet();
				elementInASet.add(getElement());
				return elementInASet;
			case POLYMORPHIC:
			case UNCERTAIN:

				// We have to hit states, which we want to avoid as much as
				// possible since it will trigger a database hit, which in the
				// aggregate
				// is expensive since there're are so many cells.

				final Set<E> elements = getElementsModifiable();

				if (elements == null) {
					throw new AssertionError(
							"elements is null in a POLYMORPHIC or UNCERTAIN cell");
				}

				if (elements.size() < 2) {
					throw new AssertionError("type is "
														+ getType()
												+ " and getElements() has "
												+ elements.size() + " elements");
				}
				return Collections.unmodifiableSet(elements);

			default:
				throw new AssertionError("Unknown Cell.Type: " + type);
		}
	}

	@Nullable
	abstract Set<E> getElementsModifiable();

	@Nullable
	public abstract R getParent();

	/** {@inheritDoc} */
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
	@Nullable
	public PPodCellType getType() {
		return type;
	}

	/**
	 * This initializes the cells elements to {@code new HashSet<E>()}.
	 * Subclasses may wish to override this for more efficient solutions, for
	 * example using an {@code EnumSet<E>}.
	 */
	void initElements() {
		setElements(new HashSet<E>());
	}

	/**
	 * Does not affect {@link #isInNeedOfNewVersion()}.
	 */
	abstract void setElement(@CheckForNull final E element);

	/**
	 * Does not affect {@link #isInNeedOfNewVersion()}.
	 */
	abstract void setElements(
			@CheckForNull final Set<E> elements);

	/**
	 * Set this cell's type to {@link Type#INAPPLICABLE}, its elements to the
	 * empty set.
	 */
	public void setInapplicable() {
		setInapplicableOrUnassigned(PPodCellType.INAPPLICABLE);
	}

	void setInapplicableOrUnassigned(final PPodCellType type) {
		checkNotNull(type);
		checkArgument(
				type == PPodCellType.INAPPLICABLE
						|| type == PPodCellType.UNASSIGNED,
				"type was " + type + " but must be INAPPLICABLE or UNASSIGNED");

		if (type == getType()) {
			return;
		}
		setType(type);
		setElement(null);
		setElements(null);
		setInNeedOfNewVersion();
		return;
	}

	@Override
	public void setInNeedOfNewVersion() {
		final R row = getParent();
		if (row != null) {
			row.setInNeedOfNewVersion();
			final Matrix<?, ?> matrix = row.getParent();
			if (matrix != null) {
				// so FindBugs knows that it's okay
				final Integer position = getPosition();
				if (getPosition() == null) {
					throw new AssertionError(
							"cell has no position, but is a part of a matrix");
				}
				matrix.setInNeedOfNewColumnVersion(position);
			}
		}
		super.setInNeedOfNewVersion();
	}

	/**
	 * Add a set of {@code E} to this {@code Cell}.
	 * <p>
	 * Assumes that none of {@code elements} is in a detached state.
	 * <p>
	 * This object makes its own copy of {@code states}.
	 * 
	 * @param states to be added
	 * 
	 * @return this
	 */
	void setPolymorphicOrUncertain(
			final PPodCellType type,
			final Set<? extends E> elements) {
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

		if (getType() != null
				&& getType()
						.equals(type)
				&& elements
						.equals(getElementsModifiable())) {
			return;
		}

		setElement(null);

		if (getElementsModifiable() == null) {
			initElements();
		}

		setType(type);

		final Set<E> thisElements = getElementsModifiable();
		if (thisElements == null) {
			throw new AssertionError(
					"initElements() was called but elements is null");
		}
		thisElements.clear();
		thisElements.addAll(elements);

		setInNeedOfNewVersion();
	}

	/**
	 * Set the position of this child.
	 * <p>
	 * Use a {@code null} when removing a child from its parent
	 * <p>
	 * <strong>There is no reason for client code to call this method as the
	 * value will always be set by the parent object. Modifying could cause
	 * unexpected behavior.</strong>
	 * 
	 * @param position the position of this child
	 * 
	 * @throw IllegalArgumentException if
	 *        {@code position !=null && position < 0}
	 */
	public void setPosition(@CheckForNull final Integer position) {
		checkArgument(position == null || position >= 0, "position < 0");
		this.position = position;
	}

	/**
	 * This method has no affect on {@link #isInNeedOfNewVersion()}.
	 * 
	 * @param type the new type
	 */
	@VisibleForTesting
	void setType(final PPodCellType type) {
		checkNotNull(type);
		this.type = type;
		return;
	}

	/**
	 * Set this cell's type to {@link Type#UNASSIGNED}, its elements to the
	 * empty set.
	 */
	public void setUnassigned() {
		setInapplicableOrUnassigned(PPodCellType.UNASSIGNED);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throw IllegalArgumentException if {@code uncertainStates.size() < 2}
	 */
	public void setUncertainElements(
			final Set<? extends E> elements) {
		checkNotNull(elements);
		checkArgument(
				elements.size() > 1,
				"uncertain elements must be > 1");
		setPolymorphicOrUncertain(
				PPodCellType.UNCERTAIN,
				elements);
	}
}
