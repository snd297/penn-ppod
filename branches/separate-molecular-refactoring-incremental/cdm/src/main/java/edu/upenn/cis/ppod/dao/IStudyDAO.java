/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.dao;

import java.util.List;
import java.util.Set;

import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.util.IPair;

/**
 * A {@code Study} DAO.
 * 
 * @author Sam Donnelly
 */
public interface IStudyDAO extends IDAO<Study, Long> {

	/**
	 * Get a (pPOD ID, Study label) pair for every {@link Study} in the
	 * database.
	 * 
	 * @return a set composed of a (pPOD ID, Study label) pair for every
	 *         {@link Study} in the database
	 */
	Set<IPair<String, String>> getPPodIdLabelPairs();

	/**
	 * Retrieve a {@link Study} given its pPOD id. Returns {@code null} if
	 * {@code pPodId == null}.
	 * 
	 * @param pPodId the pPOD id of the {@link Study} we want - {@code null} is
	 *            legal
	 * @return a {@link Study} given its pPOD id or {@code null} if {@code
	 *         pPodId == null}
	 */
	Study getStudyByPPodId(String pPodId);
	Study getStudyByPPodIdEager(String pPodId);

	/**
	 * Get the pPOD version of the study with the given pPOD id.
	 * 
	 * @param pPodId the pPOD id
	 * @return the pPOD version of the study with the given pPOD id
	 */
	Long getPPodVersionByPPodId(String pPodId);


	List<Object[]> getOTUInfosByStudyPPodIdAndMinPPodVersion(String pPodId,
			Long pPodVersion);

}
