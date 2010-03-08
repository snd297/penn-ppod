package edu.upenn.cis.ppod.modelinterfaces;

import edu.upenn.cis.ppod.model.OTUSet;

/**
 * {@code IPPodVersioned}'s that contain a {@code OTUSet}.
 * 
 * @author Sam Donnelly
 */
public interface IWithOTUSet extends IPPodVersioned {
	OTUSet getOTUSet();

}
