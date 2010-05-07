package edu.upenn.cis.ppod.modelinterfaces;

import com.google.common.base.Function;

import edu.upenn.cis.ppod.model.OTUSet;

/**
 * @author Sam Donnelly
 */
public interface IWithOTUSet {

	IWithOTUSet setOTUSet(OTUSet otuSet);

	/**
	 * Get the {@code OTUSet} associated with this object.
	 * 
	 * @return the {@code OTUSet} associated with this object
	 */
	OTUSet getOTUSet();
}
