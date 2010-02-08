package edu.upenn.cis.ppod.model;

import edu.upenn.cis.ppod.model.CharacterStateMatrix.Type;

/**
 * @author samd
 * 
 */
public class CharacterStateMatrixFactory implements
		ICharacterStateMatrixFactory {

	public CharacterStateMatrix create(final Type type) {
		switch (type) {
			case STANDARD:
				return new CharacterStateMatrix();
			case DNA:
				return new DNAStateMatrix();
			case RNA:
				return new DNAStateMatrix();
			default:
				throw new AssertionError("unknown type " + type);
		}
	}
}
