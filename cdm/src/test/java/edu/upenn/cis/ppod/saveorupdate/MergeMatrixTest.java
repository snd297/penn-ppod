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

import static com.google.common.collect.Maps.newHashMap;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Map;
import java.util.UUID;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.PPodAssert;
import edu.upenn.cis.ppod.util.MatrixProvider;

/**
 * Tests of {@link IMergeCharacterStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class MergeMatrixTest {

	@Inject
	private IMergeCharacterStateMatrix.IFactory saveOrUpdateMatrixFactory;

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private TestSaveOrUpdateAttachment saveOrUpdateAttachment;

	@Test(dataProvider = MatrixProvider.SMALL_SIMPLE_MATRIX_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void save(final CharacterStateMatrix sourceMatrix) {
		final IMergeCharacterStateMatrix mergeCharacterStateMatrix = saveOrUpdateMatrixFactory
				.create(saveOrUpdateAttachment);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = (CharacterStateMatrix) matrixProvider
				.get().setPPodId();
		fakeDbOTUSet.addMatrix(targetMatrix);
		final CharacterStateMatrix dbMatrix = mergeCharacterStateMatrix.merge(
				targetMatrix, sourceMatrix, fakeOTUsByIncomingOTU);
		assertNotNull(dbMatrix.getPPodId());
		// Check to make sure it's a UUID
		try {
			UUID.fromString(dbMatrix.getPPodId());
		} catch (final IllegalArgumentException e) {
			assertFalse(true, "dbMatrix.getPPodId() is not a UUID: " + e);
		}

		PPodAssert.assertEqualsCharacterStateMatrices(dbMatrix, sourceMatrix);
	}
}
