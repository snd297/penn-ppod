package edu.upenn.cis.ppod.modelinterfaces;

import edu.upenn.cis.ppod.model.PPodVersionInfo;

/**
 * Get the next version available from the pPOD Db.
 * <p>
 * 
 * @see SetPPodVersionInfoVisitor
 * 
 * @author Sam Donnelly
 */
public interface INewPPodVersionInfo {

	/**
	 * Get the next version available from the pPOD Db.
	 * 
	 * @return the next version available from the pPOD Db
	 */
	PPodVersionInfo getNewPPodVersionInfo();


}