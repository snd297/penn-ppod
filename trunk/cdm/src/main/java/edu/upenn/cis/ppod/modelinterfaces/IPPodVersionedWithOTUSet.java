package edu.upenn.cis.ppod.modelinterfaces;

import edu.upenn.cis.ppod.model.OTUSet;

/**
 * {@code IPPodVersioned}s that contain an {@code OTUSet}.
 * <p>
 * This is an artificial interface cooked up for {@link OTUKeyedBimap}.
 * 
 * @author Sam Donnelly
 */
public interface IPPodVersionedWithOTUSet extends IPPodVersioned {
	/**
	 * Get the {@code OTUSet} associated with this object.
	 * 
	 * @return the {@code OTUSet} associated with this object
	 */
	OTUSet getOTUSet();
}
