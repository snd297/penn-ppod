package edu.upenn.cis.ppod.imodel;

import edu.upenn.cis.ppod.model.OtuSet;

public interface IOtuSetChild extends IChild<OtuSet> {

	/**
	 * Signal the child that the otus may have changed - added or deleted.
	 */
	void updateOtus();
}
