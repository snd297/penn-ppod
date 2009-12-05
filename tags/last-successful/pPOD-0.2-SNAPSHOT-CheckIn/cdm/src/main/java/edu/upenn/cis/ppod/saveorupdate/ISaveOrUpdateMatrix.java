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
package edu.upenn.cis.ppod.saveorupdate;

import java.util.Map;

import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;

/**
 * Save or update a matrix in the pPOD db.
 * 
 * @author Sam Donnelly
 */
public interface ISaveOrUpdateMatrix {

	/**
	 * Save or update {@code incomingMatrix}.
	 * <p>
	 * If a new matrix is being newly saved, {@code dbMatrix} must have the pPOD
	 * ID set by the client.
	 * 
	 * @param incomingMatrix the state of the matrix to be saved or updated
	 * @param dbMatrix the state of the matrix in the database
	 * @param dbOTUSet the transient/persistent OTU set a client has already set
	 *            with the new state to be saved. This method will call {@code
	 *            dbMatrix.setOTUSet(dbOTUSet)}
	 * @param dbOTUsByIncomingOTU maps each incoming OTU to its
	 *            transient/persistent counterpart. This parameter is used to
	 *            set the new OTU ordering in {@code dbMatrix} as dictated by
	 *            {@code incomingMatrix}
	 * 
	 * @return {@code dbMatrix}
	 * 
	 * @throws IllegalArgumentException if {@code dbOTUSet.getPPodId() == null}
	 */
	CharacterStateMatrix saveOrUpdate(CharacterStateMatrix incomingMatrix,
			CharacterStateMatrix dbMatrix, OTUSet dbOTUSet,
			Map<OTU, OTU> dbOTUsByIncomingOTU);

	/**
	 * Makes {@link ISaveOrUpdateMatrix}s.
	 */
	static interface IFactory {
		ISaveOrUpdateMatrix create(
				ISaveOrUpdateAttachment saveOrUpdateAttachment);
	}
}
