package edu.upenn.cis.ppod.modelinterfaces;

import edu.upenn.cis.ppod.model.OTUSet;

/**
 * @author Sam Donnelly
 * 
 */
public interface IWithOTUSet extends IPPodVersioned {
	OTUSet getOTUSet();
}
