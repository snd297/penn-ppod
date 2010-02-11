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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Represents A, C, G, and T.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = DNAState.TABLE)
public class DNAState extends MolecularState {

	/**
	 * For assisted injections.
	 */
	public static interface IFactory {

		/**
		 * Create a character state with the label
		 * 
		 * @param nucleotide the nucleotide of the state we are creating
		 * 
		 * @return the new DNA state
		 */
		DNAState create(Nucleotide nucleotide);
	}

	public static enum Nucleotide {

		A, C, G, T;

		public static Nucleotide of(final int stateNumber) {
			// Can't do a switch on Nucleotide.ordinal, so if statements it is
			if (stateNumber == A.ordinal()) {
				return A;
			}
			if (stateNumber == C.ordinal()) {
				return C;
			}
			if (stateNumber == G.ordinal()) {
				return G;
			}
			if (stateNumber == T.ordinal()) {
				return T;
			}
			throw new IllegalArgumentException(
					"stateNumber must be 0, 1, 2, or 3");
		}
	}

	static final String TABLE = "DNA_STATE";

	static final String STATE_COLUMN = "STATE";

	/** For hibernate. */
	DNAState() {}

	@Inject
	DNAState(@Assisted final Nucleotide nucleotide) {
		super.setMolecularStateLabel(nucleotide.toString());
		super.setStateNumber(nucleotide.ordinal());
	}

	@Override
	public String getDocId() {
		// "D" + nucleotide are unique for DNA_STATE (as long as we keep them
		// so),
		// so let's use them instead of UUID's
		// since their smaller and there will be lots of them in the xml: up to
		// hundreds of thousands.

		return "D" + getLabel();
	}

	@Override
	public DNAState setCharacter(final Character character) {
		if (character instanceof DNACharacter) {
			super.setCharacter(character);
		} else {
			throw new IllegalArgumentException(
					"a DNAState's character must be a DNACharacter");
		}
		return this;
	}

}
