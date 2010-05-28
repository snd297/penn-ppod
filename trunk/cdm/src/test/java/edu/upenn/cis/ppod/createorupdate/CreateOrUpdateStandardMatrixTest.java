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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.TestObjectWithLongIdDAO;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.util.MatrixProvider;

/**
 * Tests of {@link ICreateOrUpdateCharacterStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST }, sequential = true)
public class CreateOrUpdateStandardMatrixTest {

	@Inject
	private ICreateOrUpdateStandardMatrix.IFactory createOrUpdateMatrixFactory;

	@Inject
	private Provider<StandardMatrix> characterStateMatrixProvider;

	@Inject
	private TestMergeAttachment mergeAttachment;

	@Inject
	private INewVersionInfo newVersionInfo;

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
		for (final OTU otu : matrix.getOTUSet().getOTUs()) {
			final StandardRow row = matrix.getRow(otu);
			rowsToCells.put(row, newArrayList(row.getCells()));
		}
		return rowsToCells;
	}

	private static void putBackCells(final StandardMatrix matrix,
			final Map<StandardRow, List<StandardCell>> rowsToCells) {
		assertEquals(matrix.getRows().size(), rowsToCells.size());
		for (final OTU otu : matrix.getOTUSet().getOTUs()) {
			final StandardRow row = matrix.getRow(otu);
			row.setCells(rowsToCells.get(row));
		}
		rowsToCells.clear();
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void save(final StandardMatrix sourceMatrix) {

		final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				createOrUpdateMatrixFactory.create(
						mergeAttachment,
						dao,
						newVersionInfo);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

		final StandardMatrix targetMatrix =
				characterStateMatrixProvider.get();

		fakeDbOTUSet.addStandardMatrix(targetMatrix);

		final Map<StandardRow, List<StandardCell>> sourceRowsToCells = stashCells(sourceMatrix);

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix, sourceMatrix);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells);

		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveRows(final StandardMatrix sourceMatrix) {
		final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				createOrUpdateMatrixFactory
						.create(mergeAttachment, dao,
								newVersionInfo);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

		final StandardMatrix targetMatrix =
				characterStateMatrixProvider.get();

		fakeDbOTUSet.addStandardMatrix(targetMatrix);

		final Map<StandardRow, List<StandardCell>> sourceRowsToCells =
				stashCells(sourceMatrix);

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix, sourceMatrix);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getColumnVersionInfos().size(); i++) {
			sourceMatrix.getCharacters().get(i).setPPodId(
					targetMatrix.getCharacters().get(i).getPPodId());
		}

		final List<OTU> shuffledSourceOTUs =
				newArrayList(sourceMatrix.getOTUSet().getOTUs());
		Collections.shuffle(shuffledSourceOTUs);

		sourceMatrix.getOTUSet().setOTUs(shuffledSourceOTUs);

		for (final OTU targetOTU : targetMatrix.getOTUSet().getOTUs()) {
			final StandardRow targetRow = targetMatrix.getRow(targetOTU);
			targetRow.setVersion(1L);
		}

		for (final OTU sourceOTU : sourceMatrix.getOTUSet().getOTUs()) {
			final StandardRow sourceRow = sourceMatrix.getRow(sourceOTU);
			sourceRow.setVersion(1L);
		}

		final Map<StandardRow, List<StandardCell>> sourceRowsToCells2 =
				stashCells(sourceMatrix);

		createOrUpdateStandardMatrix.createOrUpdateMatrix(
				targetMatrix,
				sourceMatrix);

		putBackCells(targetMatrix, dao.getRowsToCells());
		putBackCells(sourceMatrix, sourceRowsToCells2);

		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveCharacters(final StandardMatrix sourceMatrix) {
		// It only makes sense to move characters in a standard matrix
		if (sourceMatrix.getClass()
				.equals(StandardMatrix.class)) {
			final ICreateOrUpdateStandardMatrix createOrUpdateMatrix =
					createOrUpdateMatrixFactory.create(
							mergeAttachment,
							dao,
							newVersionInfo);
			final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();

			final StandardMatrix targetMatrix = characterStateMatrixProvider
					.get();

			fakeDbOTUSet.addStandardMatrix(targetMatrix);

			final Map<StandardRow, List<StandardCell>> sourceRowsToCells = stashCells(sourceMatrix);
			createOrUpdateMatrix.createOrUpdateMatrix(
					targetMatrix,
					sourceMatrix);

			putBackCells(targetMatrix, dao.getRowsToCells());
			putBackCells(sourceMatrix, sourceRowsToCells);

			// Simulate passing back in the persisted characters: so we need to
			// assign the proper pPOD ID's.
			for (int i = 0; i < sourceMatrix.getColumnVersionInfos().size(); i++) {
				sourceMatrix.getCharacters().get(i).setPPodId(
						targetMatrix.getCharacters().get(i).getPPodId());
			}

			// Swap 2 and 0
			final List<StandardCharacter> newSourceMatrixCharacters = newArrayList(sourceMatrix
					.getCharacters());

			newSourceMatrixCharacters.set(0,
					sourceMatrix.getCharacters()
							.get(2));
			newSourceMatrixCharacters.set(2,
					sourceMatrix.getCharacters().get(0));
			sourceMatrix.setCharacters(newSourceMatrixCharacters);

			for (final OTU sourceOTU : sourceMatrix.getOTUSet().getOTUs()) {
				final StandardRow sourceRow = sourceMatrix
						.getRow(sourceOTU);

				final List<StandardCell> newSourceCells = newArrayList(sourceRow
						.getCells());

				newSourceCells.set(0, sourceRow.getCells().get(2));
				newSourceCells.set(2, sourceRow.getCells().get(0));
				sourceRow.setCells(newSourceCells);
			}

			final Map<StandardRow, List<StandardCell>> sourceRowsToCells2 = stashCells(sourceMatrix);
			createOrUpdateMatrix.createOrUpdateMatrix(
					targetMatrix,
					sourceMatrix);

			putBackCells(targetMatrix, dao.getRowsToCells());
			putBackCells(sourceMatrix, sourceRowsToCells2);

			ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
					sourceMatrix);
		}
	}

}
