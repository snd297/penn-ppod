package edu.upenn.cis.ppod.modelinterfaces;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Has an {@code accept(IVisitor)} method.
 * 
 * @author Sam Donnelly
 */
public interface IVisitable {

	/**
	 * Accept the visitor
	 * 
	 * @param visitor the visitor
	 */
	void accept(IVisitor visitor);
}
