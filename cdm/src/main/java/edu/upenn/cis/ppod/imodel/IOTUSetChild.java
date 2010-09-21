package edu.upenn.cis.ppod.imodel;

public interface IOTUSetChild extends IChild<IOTUSet> {

	/**
	 * Signal the child that the otus may have changed - added or deleted.
	 */
	void updateOTUs();
}
