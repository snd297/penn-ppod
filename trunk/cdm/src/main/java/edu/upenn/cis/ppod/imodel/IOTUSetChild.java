package edu.upenn.cis.ppod.imodel;

public interface IOTUSetChild extends IChild<IOtuSetChangeCase> {

	/**
	 * Signal the child that the otus may have changed - added or deleted.
	 */
	void updateOTUs();
}
