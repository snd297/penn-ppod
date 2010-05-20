package edu.upenn.cis.ppod.modelinterfaces;

import java.util.List;

import javax.annotation.Nonnegative;

import edu.upenn.cis.ppod.model.VersionInfo;

/**
 * @author Sam Donnelly
 */
public interface IMatrix extends ILabeled, IVersionedWithOTUSet {
	IMatrix resetColumnVersion(@Nonnegative final int position);

	List<VersionInfo> getColumnVersionInfos();

}
