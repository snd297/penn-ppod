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
 * Tests of {@link ISaveOrUpdateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class SaveOrUpdateMatrixTest {

	@Inject
	private ISaveOrUpdateMatrix.IFactory saveOrUpdateMatrixFactory;

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private TestSaveOrUpdateAttachment saveOrUpdateAttachment;

	@Test(dataProvider = MatrixProvider.SMALL_SIMPLE_MATRIX_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void save(final CharacterStateMatrix incomingMatrix) {
		final ISaveOrUpdateMatrix saveOrUpdateMatrix = saveOrUpdateMatrixFactory
				.create(saveOrUpdateAttachment);
		final OTUSet fakeDbOTUSet = incomingMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU incomingOTU : incomingMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(incomingOTU, incomingOTU);
		}
		final CharacterStateMatrix dbMatrix = saveOrUpdateMatrix.saveOrUpdate(
				incomingMatrix, (CharacterStateMatrix) matrixProvider.get()
						.setPPodId(), fakeDbOTUSet, fakeOTUsByIncomingOTU);
		assertNotNull(dbMatrix.getPPodId());
		// Check to make sure it's a UUID
		try {
			UUID.fromString(dbMatrix.getPPodId());
		} catch (final IllegalArgumentException e) {
			assertFalse(true, "dbMatrix.getPPodId() is not a UUID: " + e);
		}

		PPodAssert.assertEqualsCharacterStateMatrices(dbMatrix, incomingMatrix);
	}
}
