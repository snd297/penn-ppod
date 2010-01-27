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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = DNAState.TABLE)
public final class DNAState extends MolecularState {

	/**
	 * For assisted injections.
	 */
	public static interface IFactory {

		/**
		 * Create a character state with the label
		 * 
		 * @param nucleotide the nucletide of the state we are creating
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

	final static String TABLE = "DNA_STATE";
	static final String ID_COLUMN = "DNA_STATE_ID";

	static final String STATE_COLUMN = "STATE";

	/**
	 * This column should be the same as {@link CharacterState#getLabel()} and
	 * is really only here to prevent duplicate {@code DNAState}s form being
	 * added to the table.
	 */
	@Column(name = "LABEL", nullable = false, unique = true)
	private String label;

	/** For hibernate. */
	DNAState() {}

	@Inject
	DNAState(@Assisted final Nucleotide nucleotide) {
		super.setLabel(nucleotide.toString());
		switch (nucleotide) {
			case A:
				this.label = Nucleotide.A.toString();
				setStateNumber(Nucleotide.A.ordinal());
				break;
			case C:
				this.label = Nucleotide.C.toString();
				setStateNumber(Nucleotide.C.ordinal());
				break;
			case G:
				this.label = Nucleotide.G.toString();
				setStateNumber(Nucleotide.G.ordinal());
				break;
			case T:
				this.label = Nucleotide.T.toString();
				setStateNumber(Nucleotide.T.ordinal());
				break;
			default:
				throw new AssertionError("unknown Nucleotide");
		}
		// State numbers are unique for DNA, so let's use since it's smaller
		// and there will be lots of them in the xml
		setDocId(getStateNumber().toString());
	}

	@Override
	DNAState setCharacter(final Character character) {
		if (character instanceof DNACharacter) {
			super.setCharacter(character);
		} else {
			throw new IllegalArgumentException(
					"a DNAState's character must be a DNACharacter");
		}
		return this;
	}

	/**
	 * This method is not supported for {@code DNAState} since all instances are
	 * immutable.
	 * 
	 * @param label ignored
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public DNAState setLabel(final String label) {
		throw new UnsupportedOperationException(
				"the label of a dna state is fixed");
	}
}
