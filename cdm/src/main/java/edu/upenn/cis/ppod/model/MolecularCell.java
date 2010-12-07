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
import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IMolecularCell;
import edu.upenn.cis.ppod.imodel.IRow;

@MappedSuperclass
public abstract class MolecularCell<E extends Enum<?>, R extends IRow<?, ?>>
		extends Cell<E, R>
		implements IMolecularCell<E, R> {

	@Column(name = "LOWER_CASE", nullable = true)
	@CheckForNull
	private Boolean lowerCase;

	MolecularCell() {}

	@Nullable
	@XmlAttribute(name = "upperCase")
	public Boolean getLowerCase() {
		return lowerCase;
	}

	@Override
	void setInapplicableOrUnassigned(final Type type) {
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

	/** {@inheritDoc} */
	public void setPolymorphicElements(
			final Set<? extends E> elements, final Boolean lowerCase) {
		checkNotNull(lowerCase);
		super.setPolymorphicOrUncertain(Type.POLYMORPHIC, elements);
		if (lowerCase.equals(getLowerCase())) {

		} else {
			setLowerCase(lowerCase);
			setInNeedOfNewVersion();
		}
	}

	/** {@inheritDoc} */
	public void setSingleElement(
			final E element,
			final Boolean lowerCase) {
		checkNotNull(element);
		checkNotNull(lowerCase);

		// == is safe since we know E is an Enum
		if (element == getElement()
				&& lowerCase.equals(getLowerCase())) {
			if (getType() != Type.SINGLE) {
				throw new AssertionError(
						"element is set, but this cell is not a SINGLE");
			}
		} else {
			setType(Type.SINGLE);
			setElements(null);
			setElement(element);
			setLowerCase(lowerCase);
			setInNeedOfNewVersion();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throw IllegalArgumentException if {@code uncertainStates.size() < 2}
	 */
	@Override
	public void setUncertainElements(
			final Set<? extends E> uncertainElements) {
		checkNotNull(uncertainElements);
		checkArgument(
				uncertainElements.size() > 1,
				"uncertain elements must be > 1");
		setPolymorphicOrUncertain(
				Type.UNCERTAIN,
				uncertainElements);
		setLowerCase(null);
	}
}
