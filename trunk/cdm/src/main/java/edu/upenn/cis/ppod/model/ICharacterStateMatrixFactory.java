package edu.upenn.cis.ppod.model;

/**
 * @author Sam Donnelly
 */
public interface ICharacterStateMatrixFactory {
	CharacterStateMatrix create(final CharacterStateMatrix.Type type);
}
