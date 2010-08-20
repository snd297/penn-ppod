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
package edu.upenn.cis.ppod.imodel;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.Nullable;

public interface IMolecularCell<E extends Enum<?>, R extends IRow<?, ?>>
		extends ICell<E, R> {

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
	Boolean getLowerCase();

	/**
	 * Set the cell to have type {@link Type#POLYMORPHIC}, the given elements,
	 * and the given case.
	 * <p>
	 * {@code elements} must contain at least two elements.
	 * 
	 * @param elements the elements to assign to this cell
	 * @param lowerCase is it lower-case?
	 */
	void setPolymorphicElements(
			final Set<? extends E> elements, final Boolean lowerCase);

	/**
	 * Set the cell to have type {@link Type#SINGLE}, the given element, and the
	 * given case.
	 * 
	 * @param element element to assign to this cell
	 * @param lowerCase is it lower-case?
	 */
	void setSingleElement(
			final E element,
			final Boolean lowerCase);

	/**
	 * Set the type to uncertain and this cell's elements to {@code elements}.
	 * <p>
	 * {@code elements.size()} must be greater than 2.
	 * 
	 * @param elements the elements
	 */
	void setUncertainElements(
			final Set<? extends E> elements);

}