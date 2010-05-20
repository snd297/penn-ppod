package edu.upenn.cis.ppod.modelinterfaces;

import javax.annotation.Nonnegative;

/**
 * @author Sam Donnelly
 */
public interface IMatrix extends ILabeled, IVersionedWithOTUSet {
	IMatrix resetColumnVersion(@Nonnegative final int position);

	Integer getColumnsSize();

}
