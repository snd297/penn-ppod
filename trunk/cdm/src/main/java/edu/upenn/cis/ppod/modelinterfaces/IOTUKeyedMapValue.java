package edu.upenn.cis.ppod.modelinterfaces;

/**
 * Indicates a class that is stored in {@link OTUKeyedMap} and so has a
 * relationship back to its {@code OTUKeyedMap} that can be severed.
 * 
 * @author Sam Donnelly
 */
public interface IOTUKeyedMapValue extends IPersistentObject {

	/**
	 * Sever the relationship between this object and its {@code OTUKeyedMap}.
	 * 
	 * @return this
	 */
	IOTUKeyedMapValue unsetOTUKeyedMap();
}
