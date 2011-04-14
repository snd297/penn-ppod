/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
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
package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * 'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
 * 'S', 'T', 'V', 'W', 'Y', '*', '1', '2', '3', '4'};
 * 
 * @author Sam Donnelly
 * 
 */
public enum PPodProtein {

	/** Alanine. */
	A,

	/** Cysteine. */
	C,

	/** Aspartic Acid. */
	D,

	/** Glutamic Acid. */
	E,

	/** Phenylalanine. */
	F,

	/** Glycine */
	G,

	/** Histidine */
	H,

	/** Isoleucine */
	I,

	/** Lysine */
	K,

	/** Leucine */
	L,

	/** Methionine */
	M,

	/** Asparagine */
	N,

	/** Proline */
	P,

	/** Glutamine */
	Q,

	/** Arginine */
	R,

	/** Serine */
	S,

	/** Threonine */
	T,

	/** Valine */
	V,

	/** Tryptophan */
	W,

	/** Any amino acid */
	X,

	/** Tyrosine */
	Y,

	STOP("*"),

	ONE("1"),

	TWO("2"),
	THREE("3"),
	FOUR("4");

	@CheckForNull
	private final String strValue;

	private PPodProtein() {
		strValue = null;
	}

	private PPodProtein(final String strValue) {
		checkNotNull(strValue);
		this.strValue = strValue;
	}

	@Override
	public String toString() {
		if (strValue == null) {
			return name();
		}
		return strValue;
	}

}
