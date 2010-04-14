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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.CheckForNull;
import javax.persistence.Column;
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
	 * This column should be the same as {@link CharacterState#getLabel()} and
	 * is only here to prevent duplicate {@code DNAState}s form being added to
	 * the table.
	 * <p>
	 * We were just calling this column {@code "LABEL"}, but that seemed to
	 * break {@link Character#getLabel()} - it would return {@code null} after
	 * db retrieval. Because {@code CharacterState} has a column called {@code
	 * "LABEL"}?
	 * <p>
	 * This should really be in {@code MolecularState} but a bug in Hiberante <a
	 * href
	 * ="http://opensource.atlassian.com/projects/hibernate/browse/HHH-5060">
	 * HHH=5060</a> prevents that.
	 * 
	 */
	@Column(name = "MOLECULAR_STATE_LABEL", unique = true)
	@SuppressWarnings("unused")
	@CheckForNull
	private String molecularStateLabel;

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

	/**
	 * The four states that a {@code DNAState} can have.
	 */
	public static enum Nucleotide {
		/** Adenine. */
		A,

		/** Cytosine. */
		C,

		/** Guanine. */
		G,

		/** Thymine. */
		T;

		public static boolean hasOneWithAValueOf(final char c) {
			if (c == A.toString().charAt(0)) {
				return true;
			}
			if (c == C.toString().charAt(0)) {
				return true;
			}
			if (c == G.toString().charAt(0)) {
				return true;
			}
			if (c == T.toString().charAt(0)) {
				return true;
			}
			return false;
		}

		/**
		 * Return {@code true} if {@code i} is the ordinal value of {@link #A},
		 * {@link #C}, {@link #G}, or {@link #T}, {@code false} otherwise.
		 * 
		 * @param i see description
		 * 
		 * @return {@code true} if {@code i} is the ordinal value of {@link #A},
		 *         {@link #C}, {@link #G}, or {@link #T}, {@code false}
		 *         otherwise
		 */
		public static boolean hasOneWithAValueOf(final int i) {
			if (A.ordinal() == i || C.ordinal() == i || G.ordinal() == i
					|| T.ordinal() == i) {
				return true;
			}
			return false;
		}

		/**
		 * Return {@code true} if {@code s} is the string value of {@link #A},
		 * {@link #C}, {@link #G}, or {@link #T}, {@code false} otherwise.
		 * 
		 * @param s see description
		 * 
		 * @return Return {@code true} if {@code s} is the string value of
		 *         {@link #A}, {@link #C}, {@link #G}, or {@link #T}, {@code
		 *         false} otherwise.
		 */
		public static boolean hasOneWithAValueOf(final String s) {
			checkNotNull(s);
			if (s.equals(A.toString()) || s.equals(C.toString())
					|| s.equals(G.toString()) || s.equals(T.toString())) {
				return true;
			}
			return false;
		}

		/**
		 * Get the {@code Nucleotide} with {@code Nucleotide.getOrdinal() ==
		 * stateNumber}.
		 * 
		 * @param stateNumber the state number of the {@code Nucleotide} we want
		 * 
		 * @return the {@code Nucleotide} with {@code Nucleotide.getOrdinal() ==
		 *         stateNumber}
		 */
		public static Nucleotide of(final int stateNumber) {
			// Can't do a switch on Nucleotide.ordinal, so if
			// statements it is
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
}
