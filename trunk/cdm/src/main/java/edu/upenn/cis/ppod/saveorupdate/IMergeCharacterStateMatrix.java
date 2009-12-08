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

/**
 * Save or update a matrix in the pPOD db.
 * 
 * @author Sam Donnelly
 */
public interface IMergeCharacterStateMatrix {

	/**
	 * Merge {@code sourceMatrix} into {@code targetMatrix}.
	 * <p>
	 * If a new matrix is being newly saved, {@code targetMatrix} must have the
	 * pPOD ID set by the client. *
	 * <p>
	 * NOTE: this method doesn't do anything about connecting {@code
	 * targetMatrix} to an {@code OTUSet} and this must have been done before
	 * this method is called.
	 * 
	 * 
	 * @param targetMatrix merge into the target matrix
	 * @param sourceMatrix source of the merge
	 * @param mergedOTUsBySourceOTU maps each merged OTU to its source OTU
	 *            counterpart. This parameter is used to set the new OTU
	 *            ordering in {@code targetMatrix} as dictated by {@code
	 *            sourceMatrix}
	 * 
	 * @return {@code targetMatrix}
	 * 
	 * @throws IllegalArgumentException if {@code dbOTUSet.getPPodId() == null}
	 * @throws IllegalArgumentException if {@targetMatrix.getOTUSet() == null}
	 */
	CharacterStateMatrix merge(CharacterStateMatrix targetMatrix,
			CharacterStateMatrix sourceMatrix,
			Map<OTU, OTU> mergedOTUsBySourceOTU);

	/**
	 * Makes {@link IMergeCharacterStateMatrix}s.
	 */
	static interface IFactory {
		IMergeCharacterStateMatrix create(IMergeAttachment mergeAttachment);
	}
}
