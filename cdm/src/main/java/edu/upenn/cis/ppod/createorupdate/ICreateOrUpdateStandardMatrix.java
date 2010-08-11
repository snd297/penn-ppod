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
package edu.upenn.cis.ppod.createorupdate;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStandardState;

/**
 * Copy the state of {@code sourceMatrix} onto the persistent matrix
 * {@code dbMatrix} and flush all changes to the database.
 * 
 * @see #saveOrUpdate(CharacterStateMatrix, CharacterStateMatrix, OTUSet, Map,
 *      DNACharacter)
 * 
 * @author Sam Donnelly
 */
@ImplementedBy(CreateOrUpdateStandardMatrix.class)
public interface ICreateOrUpdateStandardMatrix
		extends
		ICreateOrUpdateMatrix<IStandardMatrix, IStandardRow, IStandardCell, IStandardState> {

	/**
	 * Copy the state of {@code sourceMatrix} onto the persistent matrix
	 * {@code dbMatrix} and flush all changes to the database.
	 * <p>
	 * If {@code sourceMatrix.getDocId() != null} then this method calls
	 * {@code dbMatrix.setDocId(sourceMatrix.getDocId())}. In other words, if
	 * {@code dbMatrix}'s doc id is not already set, this method copies it from
	 * {@code sourceMatrix}.
	 * <p>
	 * All rows in {@code sourceMatrix} must be non-null.
	 * 
	 * @param dbMatrix merge into the target matrix
	 * @param sourceMatrix source of the merge
	 * @param mergedOTUsBySourceOTU maps each merged OTU to its source OTU
	 *            counterpart. This parameter is used to set the new OTU
	 *            ordering in {@code dbMatrix} as dictated by
	 *            {@code sourceMatrix}
	 */
	void createOrUpdateMatrix(
			IStandardMatrix dbMatrix,
			IStandardMatrix sourceMatrix);

	static interface IFactory {
		ICreateOrUpdateStandardMatrix create(
				IMergeAttachments mergeAttachments,
				IDAO<Object, Long> dao,
				INewVersionInfo newVersionInfo);

	}

}
