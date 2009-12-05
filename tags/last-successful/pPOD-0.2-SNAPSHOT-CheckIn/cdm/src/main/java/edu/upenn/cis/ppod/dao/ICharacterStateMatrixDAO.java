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

import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.util.IPair;

/**
 * A {@link CharacterStateMatrix} DAO.
 * 
 * @author Sam Donnelly
 */
public interface ICharacterStateMatrixDAO extends
		IDAO<CharacterStateMatrix, Long> {

	/**
	 * Get all matrices with that have the given label.
	 * 
	 * @param label the label
	 * @return all matrices with that have the given label.
	 */
	List<CharacterStateMatrix> getByLabel(String label);

	/**
	 * Get a matrix given its pPOD id.
	 * 
	 * @param pPodId the pPOD id
	 * @return a matrix given its pPOD id, or {@code null} if there is no such
	 *         matrix
	 */
	CharacterStateMatrix getByPPodId(String pPodId);

	/**
	 * Get a (pPOD ID, Study label) pair for every {@link CharacterStateMatrix}
	 * in the database.
	 * 
	 * @return a set composed of a (pPOD ID, Study label) pair for every
	 *         {@link CharacterStateMatrix} in the database
	 */
	Set<IPair<String, String>> getPPodIdLabelPairs();

	List<Object[]> getCharacterInfosByMatrixIdAndMinPPodVersion(Long matrixId,
			Long minPPodVersion);

	List<Long> getColumnPPodVersionsByMatrixId(Long matrixId);

	List<Object[]> getRowIdxsIdsVersionsByMatrixIdAndMinPPodVersion(
			Long matrixId, Long minPPodVersion);
}