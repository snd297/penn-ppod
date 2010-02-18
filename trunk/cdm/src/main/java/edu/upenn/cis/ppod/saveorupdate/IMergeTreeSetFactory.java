package edu.upenn.cis.ppod.saveorupdate;

import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 * 
 */
public interface IMergeTreeSetFactory {
	IMergeTreeSet create(INewPPodVersionInfo newPPodVersionInfo);
}
