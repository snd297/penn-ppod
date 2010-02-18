package edu.upenn.cis.ppod.saveorupdate;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.INewPPodVersionInfo;

/**
 * Makes {@link ISaveOrUpdateMatrix}s.
 */
public interface ISaveOrUpdateMatrixFactory {
	ISaveOrUpdateMatrix create(IMergeAttachment mergeAttachment,
			IDAO<Object, Long> dao, INewPPodVersionInfo newPPodVersionInfo);
}