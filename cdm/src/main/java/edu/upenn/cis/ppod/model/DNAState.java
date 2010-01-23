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

@Entity
@Table(name = DNAState.TABLE)
public class DNAState extends CharacterState {

	/**
	 * For assisted injections.
	 */
	public static interface IFactory {

		/**
		 * Create a character state with the nucleotide
		 * 
		 * @param nucleotide the nucleotide
		 * 
		 * @return the new DNA state
		 */
		DNAState create(Nucleotide nucleotide);
	}

	final static String TABLE = "DNA_STATE";

	static final String ID_COLUMN = "DNA_STATE_ID";
	static final String STATE_COLUMN = "STATE";

	public static enum Nucleotide {
		A, C, G, T;
	}

	@Inject
	DNAState(@Assisted final Nucleotide nucleotide) {
		super.setLabel(nucleotide.toString());
		switch (nucleotide) {
			case A:
				setStateNumber(0);
				break;
			case C:
				setStateNumber(1);
				break;
			case G:
				setStateNumber(2);
				break;
			case T:
				setStateNumber(3);
				break;
			default:
				throw new AssertionError("unknown Nucleotide");
		}
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
		throw new UnsupportedOperationException();
	}

}
