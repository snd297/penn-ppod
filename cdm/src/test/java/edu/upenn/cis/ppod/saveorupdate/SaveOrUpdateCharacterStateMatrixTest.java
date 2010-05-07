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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.TestObjectWithLongIdDAO;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.util.ICharacterStateMatrixFactory;
import edu.upenn.cis.ppod.util.MatrixProvider;

/**
 * Tests of {@link ISaveOrUpdateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST }, sequential = true)
public class SaveOrUpdateCharacterStateMatrixTest {

	@Inject
	private ISaveOrUpdateMatrix.IFactory saveOrUpdateMatrixFactory;

	@Inject
	private ICharacterStateMatrixFactory matrixFactory;

	@Inject
	private TestMergeAttachment mergeAttachment;

	@Inject
	private DNACharacter dnaCharacter;

	@Inject
	private INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	private Provider<TestObjectWithLongIdDAO> daoFactory;

	private TestObjectWithLongIdDAO dao;

	@BeforeMethod
	public void beforeMethod() {
		dao = daoFactory.get();
	}

//
// @AfterMethod
// public void afterMethod() {
// Session s = ManagedSessionContext.unbind(HibernateUtil
// .getSessionFactory());
// s.close();
// }

	private static Map<StandardRow, List<StandardCell>> stashCells(
			final StandardMatrix matrix) {
		final Map<StandardRow, List<StandardCell>> rowsToCells = newHashMap();
		for (final StandardRow row : matrix) {
			rowsToCells.put(row, newArrayList(row));
		}
		return rowsToCells;
	}

	private static void putBackCells(final StandardMatrix matrix,
			final Map<StandardRow, List<StandardCell>> rowsToCells) {
		assertEquals(matrix.getRowsSize(), rowsToCells.size());
		for (final StandardRow row : matrix) {
			row.setCells(rowsToCells.get(row));
		}
		rowsToCells.clear();
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void save(final StandardMatrix sourceMatrix) {

		final ISaveOrUpdateMatrix saveOrUpdateMatrix = saveOrUpdateMatrixFactory
				.create(mergeAttachment, dao, newPPodVersionInfo);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

		final StandardMatrix targetMatrix = matrixFactory
				.create(sourceMatrix);

		fakeDbOTUSet.addStandardMatrix(targetMatrix);

		final Map<StandardRow, List<StandardCell>> sourceRowsToCells = stashCells(sourceMatrix);

		saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				dnaCharacter);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells);

		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveRows(final StandardMatrix sourceMatrix) {
		final ISaveOrUpdateMatrix saveOrUpdateMatrix = saveOrUpdateMatrixFactory
				.create(mergeAttachment, dao,
						newPPodVersionInfo);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

		final StandardMatrix targetMatrix = matrixFactory
				.create(sourceMatrix);

		fakeDbOTUSet.addStandardMatrix(targetMatrix);

		final Map<StandardRow, List<StandardCell>> sourceRowsToCells =
				stashCells(sourceMatrix);

		saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				dnaCharacter);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getColumnsSize(); i++) {
			sourceMatrix.getCharacter(i).setPPodId(
					targetMatrix.getCharacter(i).getPPodId());
		}

		final List<OTU> shuffledSourceOTUs = newArrayList(sourceMatrix
				.getOTUSet());
		Collections.shuffle(shuffledSourceOTUs);

		sourceMatrix.getOTUSet().setOTUs(shuffledSourceOTUs);

		for (final StandardRow targetRow : targetMatrix) {
			targetRow.setPPodVersion(1L);
		}
		for (final StandardRow sourceRow : sourceMatrix) {
			sourceRow.setPPodVersion(1L);
		}

		final Map<StandardRow, List<StandardCell>> sourceRowsToCells2 =
				stashCells(sourceMatrix);

		saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				dnaCharacter);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells2);

		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveCharacters(final StandardMatrix sourceMatrix) {
		// It only makes sense to move characters in a standard matrix
		if (sourceMatrix.getClass().equals(StandardMatrix.class)) {
			final ISaveOrUpdateMatrix saveOrUpdateMatrix = saveOrUpdateMatrixFactory
					.create(mergeAttachment, dao, newPPodVersionInfo);
			final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

			final StandardMatrix targetMatrix = matrixFactory
					.create(sourceMatrix);

			fakeDbOTUSet.addStandardMatrix(targetMatrix);

			final Map<StandardRow, List<StandardCell>> sourceRowsToCells = stashCells(sourceMatrix);
			saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
					dnaCharacter);

			putBackCells(targetMatrix, dao.getRowsToCells());
			putBackCells(sourceMatrix, sourceRowsToCells);

			// Simulate passing back in the persisted characters: so we need to
			// assign the proper pPOD ID's.
			for (int i = 0; i < sourceMatrix.getColumnsSize(); i++) {
				sourceMatrix.getCharacter(i).setPPodId(
						targetMatrix.getCharacter(i).getPPodId());
			}

			// Swap 2 and 0
			final List<Character> newSourceMatrixCharacters = newArrayList(sourceMatrix
					.charactersIterator());

			newSourceMatrixCharacters.set(0, sourceMatrix.getCharacter(2));
			newSourceMatrixCharacters.set(2, sourceMatrix.getCharacter(0));
			sourceMatrix.setCharacters(newSourceMatrixCharacters);

			for (final StandardRow sourceRow : sourceMatrix) {
				final List<StandardCell> newSourceCells = newArrayList(sourceRow);

				newSourceCells.set(0, sourceRow.getCell(2));
				newSourceCells.set(2, sourceRow.getCell(0));
				sourceRow.setCells(newSourceCells);
			}

			final Map<StandardRow, List<StandardCell>> sourceRowsToCells2 = stashCells(sourceMatrix);
			saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
					dnaCharacter);
			putBackCells(targetMatrix, dao.getRowsToCells());
			putBackCells(sourceMatrix, sourceRowsToCells2);

			ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
					sourceMatrix);
		}
	}

	/**
	 * Test removing a character from a matrix.
	 */
	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void deleteCharacter(final StandardMatrix sourceMatrix) {
		// It only makes sense to remove characters from a standard matrix
		if (sourceMatrix.getClass().equals(StandardMatrix.class)) {
			final ISaveOrUpdateMatrix saveOrUpdateMatrix = saveOrUpdateMatrixFactory
					.create(mergeAttachment, dao, newPPodVersionInfo);
			final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

			final StandardMatrix targetMatrix = matrixFactory
					.create(sourceMatrix);

			fakeDbOTUSet.addStandardMatrix(targetMatrix);

			final Map<StandardRow, List<StandardCell>> sourceRowsToCells = stashCells(sourceMatrix);
			saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
					dnaCharacter);
			putBackCells(targetMatrix, dao.getRowsToCells());
			putBackCells(sourceMatrix, sourceRowsToCells);

			// Simulate passing back in the persisted characters: so we need to
			// assign the proper pPOD ID's.
			for (int i = 0; i < sourceMatrix.getColumnsSize(); i++) {
				sourceMatrix.getCharacter(i).setPPodId(
						targetMatrix.getCharacter(i).getPPodId());
			}

			// Remove character 2
			final Character shouldBemovedTargetCharacter = targetMatrix
					.getCharacter(2);

			final List<Character> newSourceMatrixCharacters = newArrayList(sourceMatrix
					.charactersIterator());

			newSourceMatrixCharacters.remove(2);

			sourceMatrix.setCharacters(newSourceMatrixCharacters);

			for (final StandardRow sourceRow : sourceMatrix) {
				final List<StandardCell> newSourceCells = newArrayList(sourceRow);
				newSourceCells.remove(2);
				sourceRow.setCells(newSourceCells);
			}

			final Map<StandardRow, List<StandardCell>> sourceRowsToCells2 = stashCells(sourceMatrix);
			saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
					dnaCharacter);

			assertTrue(dao.getDeletedEntities()
					.contains(shouldBemovedTargetCharacter));

			putBackCells(targetMatrix, dao.getRowsToCells());
			putBackCells(sourceMatrix, sourceRowsToCells2);

			ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
					sourceMatrix);

		}
	}
}
