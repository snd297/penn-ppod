package edu.upenn.cis.ppod.saveorupdate;

import edu.upenn.cis.ppod.model.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 * 
 */
public interface IMergeTreeSetFactory {
	IMergeTreeSet create(INewPPodVersionInfo newPPodVersionInfo);
}
