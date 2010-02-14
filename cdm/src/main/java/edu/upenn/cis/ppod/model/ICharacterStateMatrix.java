package edu.upenn.cis.ppod.model;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Sam Donnelly
 *
 */
public interface ICharacterStateMatrix {

	/**
	 * We use these to figure out what kind of matrix we have after
	 * unmarshalling.
	 */
	@XmlType(name = "CharacterStateMatrixType")
	public static enum Type {
		/** A {@link DNAStateMatrix}. */
		DNA,
	
		/** An {@link RNAStateMatrix}. */
		RNA,
	
		/** A standard {@link CharacterStateMatrix}. */
		STANDARD;
	}

}