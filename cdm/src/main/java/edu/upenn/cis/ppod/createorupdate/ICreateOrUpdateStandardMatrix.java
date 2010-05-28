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
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;

/**
 * Copy the state of
 * 
 * @see #saveOrUpdate(CharacterStateMatrix, CharacterStateMatrix, OTUSet, Map,
 *      DNACharacter)
 * 
 * @author Sam Donnelly
 */
@ImplementedBy(CreateOrUpdateStandardMatrix.class)
public interface ICreateOrUpdateStandardMatrix {

	/**
	 * Copy the state of {@code sourceMatrix} onto the persistent matrix {@code
	 * dbMatrix} and flush those changes to the database.
	 * <p>
	 * If {@code sourceMatrix.getDocId() != null} then this method calls {@code
	 * sourceMatrix.setDocId(sourceMatrix.getDocId())}. In other words, if
	 * {@code targetMatrix}'s doc id is not already set, this method copies it
	 * from {@code sourceMatrix}.
	 * <p>
	 * All rows in {@code sourceMatrix} must be non-null.
	 * <p>
	 * Implementors are free to call {@code CharacterStateRow.clearCells()} on
	 * both cells and rows in order to free up objects for garbage collection.
	 * So generally it is not safe to reattach {@code dbMatrix}.
	 * 
	 * @param dbMatrix merge into the target matrix
	 * @param sourceMatrix source of the merge
	 * @param mergedOTUsBySourceOTU maps each merged OTU to its source OTU
	 *            counterpart. This parameter is used to set the new OTU
	 *            ordering in {@code targetMatrix} as dictated by {@code
	 *            sourceMatrix}
	 */
	MatrixInfo createOrUpdateMatrix(
			StandardMatrix dbMatrix,
			StandardMatrix sourceMatrix);

	static interface IFactory {
		ICreateOrUpdateStandardMatrix create(
				IMergeAttachments mergeAttachments,
				IDAO<Object, Long> dao,
				INewVersionInfo newVersionInfo);
	}

}
