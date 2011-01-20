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
package edu.upenn.cis.ppod.dto;

import static com.google.common.collect.Sets.immutableEnumSet;

import java.util.Set;

/**
 * The four DNA nucleotides.
 * 
 * @author Sam Donnelly
 */
public enum PPodDnaNucleotide {
	/** Adenine. */
	A,

	/** Cytosine. */
	C,

	/** Guanine. */
	G,

	/** Thymine. */
	T;

	public final static Set<PPodDnaNucleotide> A_G =
			immutableEnumSet(PPodDnaNucleotide.A, PPodDnaNucleotide.G);

	public final static Set<PPodDnaNucleotide> C_T =
			immutableEnumSet(PPodDnaNucleotide.C, PPodDnaNucleotide.T);

	public final static Set<PPodDnaNucleotide> G_C =
			immutableEnumSet(PPodDnaNucleotide.G, PPodDnaNucleotide.C);

	public final static Set<PPodDnaNucleotide> A_T =
			immutableEnumSet(PPodDnaNucleotide.A, PPodDnaNucleotide.T);

	public final static Set<PPodDnaNucleotide> G_T =
			immutableEnumSet(PPodDnaNucleotide.G, PPodDnaNucleotide.T);

	public final static Set<PPodDnaNucleotide> A_C =
			immutableEnumSet(PPodDnaNucleotide.A, PPodDnaNucleotide.C);

	public final static Set<PPodDnaNucleotide> C_G_T =
			immutableEnumSet(PPodDnaNucleotide.C, PPodDnaNucleotide.G,
					PPodDnaNucleotide.T);

	public final static Set<PPodDnaNucleotide> A_G_T =
			immutableEnumSet(PPodDnaNucleotide.A, PPodDnaNucleotide.G,
					PPodDnaNucleotide.T);

	public final static Set<PPodDnaNucleotide> A_C_T =
			immutableEnumSet(PPodDnaNucleotide.A, PPodDnaNucleotide.C,
					PPodDnaNucleotide.T);

	public final static Set<PPodDnaNucleotide> A_C_G =
			immutableEnumSet(PPodDnaNucleotide.A, PPodDnaNucleotide.C,
					PPodDnaNucleotide.G);

}
