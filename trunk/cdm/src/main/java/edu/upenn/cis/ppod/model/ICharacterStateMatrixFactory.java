package edu.upenn.cis.ppod.model;

import com.google.inject.ImplementedBy;

/**
 * Produces {@link CharacterStateMatrix}s of the given argument.
 * 
 * @author Sam Donnelly
 */
@ImplementedBy(CharacterStateMatrixFactory.class)
public interface ICharacterStateMatrixFactory {

	/**
	 * Make a new {@code CharacterStateMatrix} with the same class as {@code
	 * matrix}.
	 * 
	 * @param type the type of matrix we want
	 * 
	 * @return the new matrix
	 */
	CharacterStateMatrix create(final CharacterStateMatrix matrix);

}