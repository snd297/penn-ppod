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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.util.MatrixProvider;

/**
 * Tests of {@link IMergeCharacterStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.BROKEN })
public class MergeCharacterStateMatrixTest {

	@Inject
	private IMergeCharacterStateMatrix.IFactory mergeMatrixFactory;

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<DNACharacter> dnaCharacterProvider;

	@Inject
	private TestMergeAttachment mergeAttachment;

	@Test(dataProvider = MatrixProvider.SMALL_SIMPLE_MATRIX_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void save(final CharacterStateMatrix sourceMatrix) {
		final IMergeCharacterStateMatrix mergeCharacterStateMatrix = mergeMatrixFactory
				.create(mergeAttachment, null);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = (CharacterStateMatrix) matrixProvider
				.get();
		final CharacterStateMatrix dbMatrix = mergeCharacterStateMatrix.merge(
				targetMatrix, sourceMatrix, fakeDbOTUSet,
				fakeOTUsByIncomingOTU, dnaCharacterProvider.get(), false);
		ModelAssert.assertEqualsCharacterStateMatrices(dbMatrix, sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_SIMPLE_MATRIX_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveRows(final CharacterStateMatrix sourceMatrix) {
		final IMergeCharacterStateMatrix mergeCharacterStateMatrix = mergeMatrixFactory
				.create(mergeAttachment, null);
		final OTUSet fakeTargetOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = (CharacterStateMatrix) matrixProvider
				.get();

		mergeCharacterStateMatrix.merge(targetMatrix, sourceMatrix,
				fakeTargetOTUSet, fakeOTUsByIncomingOTU, dnaCharacterProvider
						.get(), false);
		final List<OTU> shuffledSourceOTUs = newArrayList(sourceMatrix
				.getOTUs());
		Collections.shuffle(shuffledSourceOTUs);
		sourceMatrix.setOTUs(shuffledSourceOTUs);

		mergeCharacterStateMatrix.merge(targetMatrix, sourceMatrix,
				fakeTargetOTUSet, fakeOTUsByIncomingOTU, dnaCharacterProvider
						.get(), false);
		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_SIMPLE_MATRIX_PROVIDER, dataProviderClass = MatrixProvider.class, groups = TestGroupDefs.IN_DEVELOPMENT)
	public void moveCharacters(final CharacterStateMatrix sourceMatrix) {
		final IMergeCharacterStateMatrix mergeCharacterStateMatrix = mergeMatrixFactory
				.create(mergeAttachment, null);
		final OTUSet fakeTargetOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = (CharacterStateMatrix) matrixProvider
				.get();

		mergeCharacterStateMatrix.merge(targetMatrix, sourceMatrix,
				fakeTargetOTUSet, fakeOTUsByIncomingOTU, dnaCharacterProvider
						.get(), false);

		// Swap 2 and 0
		sourceMatrix.setCharacter(0, sourceMatrix.setCharacter(2, sourceMatrix
				.getCharacters().get(0)));

		mergeCharacterStateMatrix.merge(targetMatrix, sourceMatrix,
				fakeTargetOTUSet, fakeOTUsByIncomingOTU, dnaCharacterProvider
						.get(), false);

		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}
}
