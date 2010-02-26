package edu.upenn.cis.ppod.saveorupdate;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * Makes {@link ISaveOrUpdateMatrix}s.
 */
public interface ISaveOrUpdateMatrixFactory {
	ISaveOrUpdateMatrix create(IMergeAttachments mergeAttachments,
			IDAO<Object, Long> dao, INewPPodVersionInfo newPPodVersionInfo);
}