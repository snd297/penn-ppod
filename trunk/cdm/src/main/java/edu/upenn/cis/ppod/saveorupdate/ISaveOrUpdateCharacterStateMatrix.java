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

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.INewPPodVersionInfo;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.SetPPodVersionInfoVisitor;

/**
 * Merge two matrices.
 * 
 * @see #saveOrUpdate(CharacterStateMatrix, CharacterStateMatrix, OTUSet, Map,
 *      DNACharacter)
 * 
 * @author Sam Donnelly
 */
public interface ISaveOrUpdateCharacterStateMatrix {

	/**
	 * Merge {@code sourceMatrix} onto {@code targetMatrix}.
	 * <p>
	 * If {@code sourceMatrix.getDocId() != null} then this method calls {@code
	 * sourceMatrix.setDocId(sourceMatrix.getDocId())}. In other words, if
	 * {@code targetMatrix}'s doc id is not already set, this method copies it
	 * from {@code sourceMatrix}.
	 * <p>
	 * All rows in {@code sourceMatrix} must be non-null.
	 * 
	 * @param targetMatrix merge into the target matrix
	 * @param sourceMatrix source of the merge
	 * @param newTargetMatrixOTUSet this method will call {@code
	 *            targetMatrix.setOTUSet(newTargetMatrixOTUSet)}
	 * @param mergedOTUsBySourceOTU maps each merged OTU to its source OTU
	 *            counterpart. This parameter is used to set the new OTU
	 *            ordering in {@code targetMatrix} as dictated by {@code
	 *            sourceMatrix}
	 * @param dnaCharacter the {@code DNACharacter} in a persistent state that
	 *            should be used in the target matrix
	 */
	void saveOrUpdate(CharacterStateMatrix targetMatrix,
			CharacterStateMatrix sourceMatrix, OTUSet newTargetMatrixOTUSet,
			Map<OTU, OTU> mergedOTUsBySourceOTU, DNACharacter dnaCharacter);

	/**
	 * Makes {@link ISaveOrUpdateCharacterStateMatrix}s.
	 */
	static interface IFactory {
		ISaveOrUpdateCharacterStateMatrix create(
				IMergeAttachment mergeAttachment,
				IDAO<Object, Long> dao, INewPPodVersionInfo newPPodVersionInfo);
	}
}
