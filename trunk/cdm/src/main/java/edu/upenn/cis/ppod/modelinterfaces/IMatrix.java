package edu.upenn.cis.ppod.modelinterfaces;

import javax.annotation.Nonnegative;

/**
 * @author Sam Donnelly
 */
public interface IMatrix extends IPPodVersionedWithOTUSet {
	IMatrix resetColumnPPodVersion(@Nonnegative final int position);
}
