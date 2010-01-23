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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNACharacter.TABLE)
public class DNACharacter extends Character {

	public static final String TABLE = "DNA_CHARACTER";

	DNACharacter() {
		super.setLabel("DNA Character");
	}

// @Override
// public DNACharacter setPPodId() {
// throw new UnsupportedOperationException(
// "Can't set a DNACharacter's pPOD ID");
// }

	@Override
	public DNACharacter setLabel(final String label) {
		throw new UnsupportedOperationException(
				"Can't set the label of a DNACharacter");
	}

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
				"can't only add a DNAState to a DNACharacter, you added a "
						+ state.getClass());
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	public String toString() {
		final String TAB = "";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("DNACharacter(").append(super.toString()).append(TAB)
				.append("label=").append(")");

		return retValue.toString();
	}

}
