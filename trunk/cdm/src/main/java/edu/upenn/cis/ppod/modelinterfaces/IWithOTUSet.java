package edu.upenn.cis.ppod.modelinterfaces;

import edu.upenn.cis.ppod.model.OTUSet;

public interface IWithOTUSet {

	/**
	 * Get the {@code OTUSet} associated with this object.
	 * 
	 * @return the {@code OTUSet} associated with this object
	 */
	OTUSet getOTUSet();
}
