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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.modelinterfaces.IMolecularCell;
import edu.upenn.cis.ppod.modelinterfaces.IRow;

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
	public Boolean isLowerCase() {
		return lowerCase;
	}

	@Override
	void setInapplicableOrUnassigned(final Type type) {
		super.setInapplicableOrUnassigned(type);
		setLowerCase(null);
	}

	/**
	 * Protected for JAXB.
	 */
	protected void setLowerCase(
			@CheckForNull final Boolean upperCase) {
		this.lowerCase = upperCase;
	}

	public IMolecularCell<E, R> setPolymorphicElements(
			final Set<? extends E> elements, final Boolean lowerCase) {
		checkNotNull(lowerCase);
		super.setPolymorphicOrUncertain(Type.POLYMORPHIC, elements);
		if (lowerCase.equals(isLowerCase())) {
			return this;
		}
		setLowerCase(lowerCase);
		setInNeedOfNewVersion();
		return this;
	}

	/** {@inheritDoc} */
	public IMolecularCell<E, R> setSingleElement(
			final E element,
			final Boolean lowerCase) {
		checkNotNull(element);
		checkNotNull(lowerCase);

		// == is safe since we know E is an Enum
		if (element == getElement()
				&& lowerCase.equals(isLowerCase())) {
			if (getType() != Type.SINGLE) {
				throw new AssertionError(
						"element is set, but this cell is not a SINGLE");
			}
			return this;
		}
		setType(Type.SINGLE);
		setElements(null);
		setElement(element);
		setLowerCase(lowerCase);
		setInNeedOfNewVersion();
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public MolecularCell<E, R> setUncertainElements(
			final Set<? extends E> elements) {
		super.setUncertainElements(elements);
		setLowerCase(null);
		return this;
	}
}
