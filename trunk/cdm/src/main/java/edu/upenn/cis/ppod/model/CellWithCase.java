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

import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.dto.PPodCellType;

/**
 * A cell in a molecular matrix. The elements are subclasses of {@code Enum} so
 * implementors are free to, for example, put the elements into an
 * {@link java.util.EnumSet} or use {@code ==} when comparing elements.
 * 
 * @author Sam Donnelly
 * 
 * @param <E> see the elements in the cell
 * @param <R> see parent row
 */
@MappedSuperclass
public abstract class CellWithCase<E extends Enum<?>, R extends Row<?, ?>>
		extends Cell<E, R> {

	@Column(name = "LOWER_CASE", nullable = true)
	@CheckForNull
	private Boolean lowerCase;

	CellWithCase() {}

	/**
	 * Are the contained {@link edu.upenn.cis.ppod.model.DNANucleotide}
	 * lower-case?
	 * <p>
	 * Will be {@code null} for {@link Type.INAPPLICABLE},
	 * {@link Type.UNASSIGNED}, and {@link Type.UNCERTAIN} cells.
	 * 
	 * @return {@code true} if the contained
	 *         {@link edu.upenn.cis.ppod.model.DNANucleotide} is lower-case,
	 *         {@code false} otherwise
	 */
	@Nullable
	public Boolean getLowerCase() {
		return lowerCase;
	}

	@Override
	void setInapplicableOrUnassigned(final PPodCellType type) {
		super.setInapplicableOrUnassigned(type);
		setLowerCase(null);
	}

	/**
	 * Protected JAXB.
	 */
	protected void setLowerCase(
			@CheckForNull final Boolean upperCase) {
		this.lowerCase = upperCase;
	}

	/**
	 * Set the cell to have type {@link Type#POLYMORPHIC}, the given elements,
	 * and the given case.
	 * <p>
	 * {@code elements} must contain at least two elements.
	 * 
	 * @param elements the elements to assign to this cell
	 * @param lowerCase is it lower-case?
	 */
	public void setPolymorphic(
			final Set<E> elements, final Boolean lowerCase) {
		checkNotNull(lowerCase);
		super.setPolymorphicOrUncertain(PPodCellType.POLYMORPHIC, elements);
		setLowerCase(lowerCase);
	}

	/**
	 * Set the cell to have type {@link Type#SINGLE}, the given element, and the
	 * given case.
	 * 
	 * @param element element to assign to this cell
	 * @param lowerCase is it lower-case?
	 */
	public void setSingle(
			final E element,
			final Boolean lowerCase) {
		checkNotNull(element);
		checkNotNull(lowerCase);

		setType(PPodCellType.SINGLE);
		setElements(null);
		setElement(element);
		setLowerCase(lowerCase);
	}

	/**
	 * Set the type to uncertain and this cell's elements to {@code elements}.
	 * 
	 * @param elements the elements
	 * 
	 * @throws IllegalArgumentException if {@code uncertainStates.size() > 1}
	 */
	@Override
	public void setUncertain(final Set<E> uncertainElements) {
		checkNotNull(uncertainElements);
		checkArgument(
				uncertainElements.size() > 1,
				"uncertain elements must be > 1");
		setPolymorphicOrUncertain(
				PPodCellType.UNCERTAIN,
				uncertainElements);
		setLowerCase(null);
	}
}
