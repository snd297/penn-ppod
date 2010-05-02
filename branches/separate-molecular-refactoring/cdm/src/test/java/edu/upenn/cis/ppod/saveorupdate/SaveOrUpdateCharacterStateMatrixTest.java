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
import edu.upenn.cis.ppod.model.AbstractCharacter;
import edu.upenn.cis.ppod.model.CategoricalCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.util.ICharacterStateMatrixFactory;
import edu.upenn.cis.ppod.util.MatrixProvider;

/**
 * Tests of {@link ISaveOrUpdateCategoricalMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST }, sequential = true)
public class SaveOrUpdateCharacterStateMatrixTest {

	@Inject
	private ISaveOrUpdateCategoricalMatrix.IFactory saveOrUpdateMatrixFactory;

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

	private static Map<CharacterStateRow, List<CategoricalCell>> stashCells(
			final CharacterStateMatrix matrix) {
		final Map<CharacterStateRow, List<CategoricalCell>> rowsToCells = newHashMap();
		for (final CharacterStateRow row : matrix) {
			rowsToCells.put(row, newArrayList(row));
		}
		return rowsToCells;
	}

	private static void putBackCells(final CharacterStateMatrix matrix,
			final Map<CharacterStateRow, List<CategoricalCell>> rowsToCells) {
		assertEquals(matrix.getRowsSize(), rowsToCells.size());
		for (final CharacterStateRow row : matrix) {
			row.setCells(rowsToCells.get(row));
		}
		rowsToCells.clear();
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void save(final CharacterStateMatrix sourceMatrix) {

		final ISaveOrUpdateCategoricalMatrix saveOrUpdateCategoricalMatrix = saveOrUpdateMatrixFactory
				.create(mergeAttachment, dao, newPPodVersionInfo);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

		final CharacterStateMatrix targetMatrix = matrixFactory
				.create(sourceMatrix);

		fakeDbOTUSet.addMatrix(targetMatrix);

		final Map<CharacterStateRow, List<CategoricalCell>> sourceRowsToCells = stashCells(sourceMatrix);

		saveOrUpdateCategoricalMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				dnaCharacter);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells);

		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveRows(final CharacterStateMatrix sourceMatrix) {
		final ISaveOrUpdateCategoricalMatrix saveOrUpdateCategoricalMatrix = saveOrUpdateMatrixFactory
				.create(mergeAttachment, dao,
						newPPodVersionInfo);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

		final CharacterStateMatrix targetMatrix = matrixFactory
				.create(sourceMatrix);

		fakeDbOTUSet.addMatrix(targetMatrix);

		final Map<CharacterStateRow, List<CategoricalCell>> sourceRowsToCells =
				stashCells(sourceMatrix);

		saveOrUpdateCategoricalMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				dnaCharacter);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getCharactersSize(); i++) {
			sourceMatrix.getCharacter(i).setPPodId(
					targetMatrix.getCharacter(i).getPPodId());
		}

		final List<OTU> shuffledSourceOTUs = newArrayList(sourceMatrix
				.getOTUSet());
		Collections.shuffle(shuffledSourceOTUs);

		sourceMatrix.getOTUSet().setOTUs(shuffledSourceOTUs);

		for (final CharacterStateRow targetRow : targetMatrix) {
			targetRow.setPPodVersion(1L);
		}
		for (final CharacterStateRow sourceRow : sourceMatrix) {
			sourceRow.setPPodVersion(1L);
		}

		final Map<CharacterStateRow, List<CategoricalCell>> sourceRowsToCells2 =
				stashCells(sourceMatrix);

		saveOrUpdateCategoricalMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				dnaCharacter);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells2);

		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveCharacters(final CharacterStateMatrix sourceMatrix) {
		// It only makes sense to move characters in a standard matrix
		if (sourceMatrix.getClass().equals(CharacterStateMatrix.class)) {
			final ISaveOrUpdateCategoricalMatrix saveOrUpdateCategoricalMatrix = saveOrUpdateMatrixFactory
					.create(mergeAttachment, dao, newPPodVersionInfo);
			final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

			final CharacterStateMatrix targetMatrix = matrixFactory
					.create(sourceMatrix);

			fakeDbOTUSet.addMatrix(targetMatrix);

			final Map<CharacterStateRow, List<CategoricalCell>> sourceRowsToCells = stashCells(sourceMatrix);
			saveOrUpdateCategoricalMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
					dnaCharacter);

			putBackCells(targetMatrix, dao.getRowsToCells());
			putBackCells(sourceMatrix, sourceRowsToCells);

			// Simulate passing back in the persisted characters: so we need to
			// assign the proper pPOD ID's.
			for (int i = 0; i < sourceMatrix.getCharactersSize(); i++) {
				sourceMatrix.getCharacter(i).setPPodId(
						targetMatrix.getCharacter(i).getPPodId());
			}

			// Swap 2 and 0
			final List<AbstractCharacter> newSourceMatrixCharacters = newArrayList(sourceMatrix
					.getCharactersIterator());

			newSourceMatrixCharacters.set(0, sourceMatrix.getCharacter(2));
			newSourceMatrixCharacters.set(2, sourceMatrix.getCharacter(0));
			sourceMatrix.setCharacters(newSourceMatrixCharacters);

			for (final CharacterStateRow sourceRow : sourceMatrix) {
				final List<CategoricalCell> newSourceCells = newArrayList(sourceRow);

				newSourceCells.set(0, sourceRow.getCell(2));
				newSourceCells.set(2, sourceRow.getCell(0));
				sourceRow.setCells(newSourceCells);
			}

			final Map<CharacterStateRow, List<CategoricalCell>> sourceRowsToCells2 = stashCells(sourceMatrix);
			saveOrUpdateCategoricalMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
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
	public void deleteCharacter(final CharacterStateMatrix sourceMatrix) {
		// It only makes sense to remove characters from a standard matrix
		if (sourceMatrix.getClass().equals(CharacterStateMatrix.class)) {
			final ISaveOrUpdateCategoricalMatrix saveOrUpdateCategoricalMatrix = saveOrUpdateMatrixFactory
					.create(mergeAttachment, dao, newPPodVersionInfo);
			final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

			final CharacterStateMatrix targetMatrix = matrixFactory
					.create(sourceMatrix);

			fakeDbOTUSet.addMatrix(targetMatrix);

			final Map<CharacterStateRow, List<CategoricalCell>> sourceRowsToCells = stashCells(sourceMatrix);
			saveOrUpdateCategoricalMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
					dnaCharacter);
			putBackCells(targetMatrix, dao.getRowsToCells());
			putBackCells(sourceMatrix, sourceRowsToCells);

			// Simulate passing back in the persisted characters: so we need to
			// assign the proper pPOD ID's.
			for (int i = 0; i < sourceMatrix.getCharactersSize(); i++) {
				sourceMatrix.getCharacter(i).setPPodId(
						targetMatrix.getCharacter(i).getPPodId());
			}

			// Remove character 2
			final AbstractCharacter shouldBemovedTargetCharacter = targetMatrix
					.getCharacter(2);

			final List<AbstractCharacter> newSourceMatrixCharacters = newArrayList(sourceMatrix
					.getCharactersIterator());

			newSourceMatrixCharacters.remove(2);

			sourceMatrix.setCharacters(newSourceMatrixCharacters);

			for (final CharacterStateRow sourceRow : sourceMatrix) {
				final List<CategoricalCell> newSourceCells = newArrayList(sourceRow);
				newSourceCells.remove(2);
				sourceRow.setCells(newSourceCells);
			}

			final Map<CharacterStateRow, List<CategoricalCell>> sourceRowsToCells2 = stashCells(sourceMatrix);
			saveOrUpdateCategoricalMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
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
