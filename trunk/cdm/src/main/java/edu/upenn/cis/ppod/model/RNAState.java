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
 * @author Sam Donnelly
 */
@Entity
@Table(name = RNAState.TABLE)
public class RNAState extends MolecularState {

	public static final String TABLE = "RNA_STATE";

	/**
	 * For assisted injections.
	 */
	public static interface IFactory {

		/**
		 * Create a character state with the label
		 * 
		 * @param nucleotide the nucleoAtide of the state we are creating
		 * 
		 * @return the new DNA state
		 */
		RNAState create(Nucleotide nucleotide);
	}

	public static enum Nucleotide {

		A, C, G, U;

		public static Nucleotide of(final int stateNumber) {
			// Can't do a case on Nucleotide.ordinal(), so if statements it is
			if (stateNumber == A.ordinal()) {
				return A;
			}
			if (stateNumber == C.ordinal()) {
				return C;
			}
			if (stateNumber == G.ordinal()) {
				return G;
			}
			if (stateNumber == U.ordinal()) {
				return U;
			}
			throw new IllegalArgumentException(
					"stateNumber must be 0, 1, 2, or 3");
		}
	}

	@Inject
	RNAState(@Assisted final Nucleotide nucleotide) {
		super.setLabel(nucleotide.toString());
		switch (nucleotide) {
			case A:
				setMolecularStateLabel(Nucleotide.A.toString());
				setStateNumber(Nucleotide.A.ordinal());
				break;
			case C:
				setMolecularStateLabel(Nucleotide.C.toString());
				setStateNumber(Nucleotide.C.ordinal());
				break;
			case G:
				setMolecularStateLabel(Nucleotide.G.toString());
				setStateNumber(Nucleotide.G.ordinal());
				break;
			case U:
				setMolecularStateLabel(Nucleotide.U.toString());
				setStateNumber(Nucleotide.U.ordinal());
				break;
			default:
				throw new AssertionError("unknown Nucleotide");
		}
		// State numbers are unique for DNA, so let's use since it's smaller
		// and there will be lots of them in the xml
		setDocId(getStateNumber().toString());
	}
}
