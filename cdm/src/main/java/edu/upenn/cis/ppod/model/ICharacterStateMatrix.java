package edu.upenn.cis.ppod.model;

import java.util.Collections;
import java.util.List;

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

	/**
	 * Get an unmodifiable view of the {@code PPodVersionInfo}s for each for the
	 * columns of the matrix.
	 * <p>
	 * This value is {@code equals()} to the max pPOD version info in a column.
	 * 
	 * @return an unmodifiable view of the columns' {@code PPodVersionInfo}s
	 */
	public List<PPodVersionInfo> getColumnPPodVersionInfos();

}