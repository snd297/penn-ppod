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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.ICell;
import edu.upenn.cis.ppod.imodel.IMatrix;
import edu.upenn.cis.ppod.imodel.IRow;

/**
 * A cell.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Cell<E, R extends IRow<?, ?>>
		extends PPodEntity
		implements ICell<E, R> {

	static final String TYPE_COLUMN = "TYPE";

	@Column(name = "POSITION", nullable = false)
	@CheckForNull
	private Integer position;

	@Column(name = TYPE_COLUMN, nullable = false)
	@Enumerated(EnumType.ORDINAL)
	@CheckForNull
	private Type type;

	Cell() {}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	protected void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		checkNotNull(parent);

		@SuppressWarnings("unchecked")
		final R row = (R) parent;
		setParent(row);
	}

	@Override
	protected boolean beforeMarshal(@CheckForNull final Marshaller marshaller) {
		checkState(getType() != null, "can't marshal a cell with no type");
		return true;
	}

	/**
	 * Will be {@code null} if this is cell is not {@link Type.SINGLE}.
	 */
	@CheckForNull
	abstract E getElement();

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException if the type has not been set for this cell,
	 *             i.e. if {@link #getType() == null}
	 */
	public Set<E> getElements() {
		checkState(getType() != null,
				"type has yet to be assigned for this cell");

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

	@CheckForNull
	abstract Set<E> getElementsModifiable();

	/**
	 * Used for serialization.
	 * <p>
	 * This method assumes that {@link #getType()} is non-null. So, for
	 * instance, it assumes that the unmarshaller will have set the type before
	 * it calls this method.
	 * 
	 * @throws IllegalStateException if {@code getType() == null}
	 */
	@CheckForNull
	Set<E> getElementsXml() {
		if (getType() == null) {
			throw new IllegalStateException("getType == null");
		}
		switch (getType()) {
			case UNASSIGNED:
			case SINGLE:
			case INAPPLICABLE:
				return null;
			case POLYMORPHIC:
			case UNCERTAIN:
				if (getElementsModifiable() == null) {
					initElements();
				}
				return getElementsModifiable();
			default:
				throw new AssertionError("unknown type: " + getType());
		}
	}

	@Nullable
	public abstract R getParent();

	/**
	 * Package-private for testing.
	 */
	@Nullable
	Integer getPosition() {
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
	@XmlAttribute
	@Nullable
	public Type getType() {
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

	/** {@inheritDoc} */
	public void setInapplicable() {
		setInapplicableOrUnassigned(Type.INAPPLICABLE);
	}

	void setInapplicableOrUnassigned(final Type type) {
		checkNotNull(type);
		checkArgument(
				type == Type.INAPPLICABLE
						|| type == Type.UNASSIGNED,
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
			final IMatrix<?> matrix = row.getParent();
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
	 * <p>
	 * This implementation calls {@link #initElements()}.
	 * 
	 * @param states to be added
	 * 
	 * @return this
	 */
	void setPolymorphicOrUncertain(
			final Type type,
			final Set<? extends E> elements) {
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
	 * {@inheritDoc}
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
	void setType(final Type type) {
		checkNotNull(type);
		this.type = type;
		return;
	}

	/** {@inheritDoc} */
	public void setUnassigned() {
		setInapplicableOrUnassigned(Type.UNASSIGNED);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throw IllegalArgumentException if {@code uncertainStates.size() < 2}
	 */
	public void setUncertainElements(
			final Set<? extends E> uncertainElements) {
		checkNotNull(uncertainElements);
		checkArgument(uncertainElements.size() > 1,
				"uncertain elements must be > 1");
		setPolymorphicOrUncertain(Type.UNCERTAIN, uncertainElements);
	}

}
