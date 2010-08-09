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

import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.model.StandardCharacter;

/**
 * A {@link Character} DAO.
 * 
 * @author Sam Donnelly
 */
public interface ICharacterDAO extends IDAO<StandardCharacter, Long> {

	/**
	 * Get the persisted {@link Character} with the given pPOD id, or {@code
	 * null} if there is no such {@link Character}.
	 * 
	 * @param pPodID the pPOD ID of the character we want
	 * @return the persisted {@link Character} with the given pPOD id, or
	 *         {@code null} if there is no such {@link Character}
	 */
	IStandardCharacter getCharacterByPPodId(String pPodID);
}
