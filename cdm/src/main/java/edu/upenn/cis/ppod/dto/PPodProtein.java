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
