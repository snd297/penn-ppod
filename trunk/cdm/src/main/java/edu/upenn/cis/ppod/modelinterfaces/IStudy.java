package edu.upenn.cis.ppod.modelinterfaces;

import java.util.Set;

import edu.upenn.cis.ppod.services.ppodentity.IOTUSetCentricEntities;

/**
 * A collection of work - inspired by a Mesquite project - sets of OTU sets and,
 * through the OTU sets, matrices and tree sets.
 * 
 * @author Sam Donnelly
 */
public interface IStudy extends ILabeled, IOTUSetCentricEntities, IUUPPodEntity {

	IOTUSet addOTUSet(final IOTUSet otuSet);

	Set<IOTUSet> getOTUSets();

	/**
	 * Remove an OTU set from this Study.
	 * 
	 * @param otuSet to be removed
	 * 
	 * @return this
	 */
	IStudy removeOTUSet(final IOTUSet otuSet);

	/**
	 * Set the label.
	 * 
	 * @param label the label to set
	 * 
	 * @return this
	 */
	IStudy setLabel(final String label);

}