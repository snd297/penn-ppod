package edu.upenn.cis.ppod.model;

/**
 * The DNA nucleotides.
 * 
 * @author Sam Donnelly
 */
public enum DNANucleotide {

	/** Adenine. */
	A,

	/** Cytosine. */
	C,

	/** Guanine. */
	G,

	/** Thymine. */
	T;

	/**
	 * Get the {@code Nucleotide} with {@code Nucleotide.getOrdinal() ==
	 * stateNumber}.
	 * 
	 * @param stateNumber the state number of the {@code Nucleotide} we want
	 * 
	 * @return the {@code Nucleotide} with {@code Nucleotide.getOrdinal() ==
	 *         stateNumber}
	 */
	public static DNANucleotide of(final int stateNumber) {
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
