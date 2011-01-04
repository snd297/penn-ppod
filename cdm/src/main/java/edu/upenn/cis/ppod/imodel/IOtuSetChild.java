package edu.upenn.cis.ppod.imodel;

public interface IOtuSetChild extends IChild<IOtuSet> {

	/**
	 * Signal the child that the otus may have changed - added or deleted.
	 */
	void updateOtus();
}
