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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;

/**
 * @author Sam Donnelly
 */
@XmlType(name = "DNACharacter")
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = DNACharacter.TABLE)
public final class DNACharacter extends Character {

	/**
	 * The name of the table.
	 */
	public static final String TABLE = "DNA_CHARACTER";

	/**
	 * This column should be the same as {@link Character#getLabel()} and is
	 * really only here to prevent duplicate {@code DNACharacter}s from being
	 * added to the table. Duplicates are prevented by the {@code nullable =
	 * false, unique = true} combination.
	 */
	@Column(name = "LABEL", nullable = false, unique = true)
	@SuppressWarnings("unused")
	private String label;

	public final static String LABEL = "DNA Character";

	/** For hibernate. */
	DNACharacter() {}

	@Inject
	DNACharacter(final DNAState.IFactory dnaStateFactory) {
		this.label = LABEL;
		super.setLabel(LABEL);
		setDNACharacter();
		addState(dnaStateFactory.create(DNAState.Nucleotide.A));
		addState(dnaStateFactory.create(DNAState.Nucleotide.C));
		addState(dnaStateFactory.create(DNAState.Nucleotide.G));
		addState(dnaStateFactory.create(DNAState.Nucleotide.T));
	}

// @Override
// public DNACharacter setPPodId() {
// throw new UnsupportedOperationException(
// "Can't set a DNACharacter's pPOD ID");
// }

	/**
	 * @throws IllegalArgumentException if {@code state} is not a
	 *             {@link DNAState}
	 */
	@Override
	public DNAState addState(final CharacterState state) {
		if (state instanceof DNAState) {
			super.addState(state);
			return (DNAState) state;
		}
		throw new IllegalArgumentException(
				"can only add a DNAState to a DNACharacter, not a "
						+ state.getClass().getName());
	}

	@Override
	public DNACharacter setLabel(final String label) {
		throw new UnsupportedOperationException(
				"Can't set the label of a DNACharacter");
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = "";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("DNACharacter(").append(super.toString()).append(TAB)
				.append("label=").append(")");

		return retValue.toString();
	}

}
