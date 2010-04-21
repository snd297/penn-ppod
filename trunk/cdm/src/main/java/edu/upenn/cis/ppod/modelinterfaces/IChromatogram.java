package edu.upenn.cis.ppod.modelinterfaces;

import edu.upenn.cis.ppod.model.DNASequence;

/**
 * @author Sam Donnelly
 */
public interface IChromatogram {

	/**
	 * Get the chromatogram.
	 * 
	 * @return the chromatogram
	 */
	byte[] getChromatogram();

	/**
	 * Set the chromatogram.
	 * 
	 * @param chromatogram the chromatogram to set
	 * 
	 * @return this
	 */
	IChromatogram setChromatogram(final byte[] chromatogram);

	DNASequence getSequence();

	IChromatogram setSequence(DNASequence sequence);

}