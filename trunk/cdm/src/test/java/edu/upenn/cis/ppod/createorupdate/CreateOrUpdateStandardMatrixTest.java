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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOtuSetChangeCase;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IVersionInfo;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.util.PPodEntityProvider;

/**
 * Tests of {@link ICreateOrUpdateCharacterStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class CreateOrUpdateStandardMatrixTest {

	@Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
			dataProviderClass = PPodEntityProvider.class)
	public void create(final IStandardMatrix sourceMatrix) {

		final IVersionInfo versionInfo = mock(IVersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				new CreateOrUpdateStandardMatrix(
						mock(IObjectWithLongIdDAO.class),
						newVersionInfo);

		final IOtuSetChangeCase fakeDbOTUSet = sourceMatrix.getParent();

		final IStandardMatrix targetMatrix = new StandardMatrix();

		fakeDbOTUSet.addStandardMatrix(targetMatrix);

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix, sourceMatrix);

		ModelAssert.assertEqualsStandardMatrices(
				targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
			dataProviderClass = PPodEntityProvider.class)
	public void moveRows(final IStandardMatrix sourceMatrix) {

		final IVersionInfo versionInfo = mock(IVersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				new CreateOrUpdateStandardMatrix(
						mock(IObjectWithLongIdDAO.class),
						newVersionInfo);

		final IOtuSetChangeCase fakeDbOTUSet = sourceMatrix.getParent();

		final IStandardMatrix targetMatrix =
				new StandardMatrix();

		fakeDbOTUSet.addStandardMatrix(targetMatrix);

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix, sourceMatrix);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getColumnVersionInfos().size(); i++) {
			sourceMatrix.getCharacters().get(i).setPPodId(
					targetMatrix.getCharacters().get(i).getPPodId());
		}

		final List<IOtu> shuffledSourceOTUs =
				newArrayList(sourceMatrix.getParent().getOTUs());
		shuffledSourceOTUs.set(0,
				sourceMatrix.getParent()
						.getOTUs()
						.get(shuffledSourceOTUs.size() / 2));
		shuffledSourceOTUs.set(
				shuffledSourceOTUs.size() / 2,
				sourceMatrix.getParent()
						.getOTUs()
						.get(0));

		sourceMatrix.getParent().setOTUs(shuffledSourceOTUs);

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix, sourceMatrix);

		ModelAssert.assertEqualsStandardMatrices(
				targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
			dataProviderClass = PPodEntityProvider.class)
	public void moveCharacters(final IStandardMatrix sourceMatrix) {

		final IVersionInfo versionInfo = mock(IVersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				new CreateOrUpdateStandardMatrix(
						mock(IObjectWithLongIdDAO.class),
						newVersionInfo);

		final IOtuSetChangeCase fakeDbOTUSet = sourceMatrix.getParent();

		final IStandardMatrix targetMatrix = new StandardMatrix();

		fakeDbOTUSet.addStandardMatrix(targetMatrix);

		createOrUpdateStandardMatrix.createOrUpdateMatrix(
					targetMatrix,
					sourceMatrix);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getColumnVersionInfos().size(); i++) {
			sourceMatrix.getCharacters().get(i).setPPodId(
						targetMatrix.getCharacters().get(i).getPPodId());
		}

		// Swap 2 and 0
		final List<IStandardCharacter> newSourceMatrixCharacters =
				newArrayList(sourceMatrix.getCharacters());

		newSourceMatrixCharacters.set(0,
					sourceMatrix.getCharacters()
							.get(2));
		newSourceMatrixCharacters.set(2,
					sourceMatrix.getCharacters().get(0));
		sourceMatrix.setCharacters(newSourceMatrixCharacters);

		for (final IOtu sourceOTU : sourceMatrix.getParent().getOTUs()) {
			final IStandardRow sourceRow =
					sourceMatrix.getRows().get(sourceOTU);

			final List<IStandardCell> newSourceCells =
					newArrayList(sourceRow.getCells());

			newSourceCells.set(0, sourceRow.getCells().get(2));
			newSourceCells.set(2, sourceRow.getCells().get(0));
			sourceRow.setCells(newSourceCells);
		}

		createOrUpdateStandardMatrix.createOrUpdateMatrix(
					targetMatrix,
					sourceMatrix);

		ModelAssert.assertEqualsStandardMatrices(
				targetMatrix, sourceMatrix);
	}

	@Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
			dataProviderClass = PPodEntityProvider.class)
	public void removeColumn(final IStandardMatrix sourceMatrix) {

		final IVersionInfo versionInfo = mock(IVersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				new CreateOrUpdateStandardMatrix(
						mock(IObjectWithLongIdDAO.class),
						newVersionInfo);

		final IOtuSetChangeCase fakeDbOTUSet = sourceMatrix.getParent();

		final IStandardMatrix targetMatrix =
				new StandardMatrix();

		fakeDbOTUSet.addStandardMatrix(targetMatrix);

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix, sourceMatrix);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getColumnVersionInfos().size(); i++) {
			sourceMatrix.getCharacters()
					.get(i)
					.setPPodId(targetMatrix
							.getCharacters()
							.get(i)
							.getPPodId());
		}

		final List<IStandardCharacter> newSourceCharacters =
				newArrayList(sourceMatrix.getCharacters());
		newSourceCharacters.remove(
				sourceMatrix
						.getCharacters().size() / 2);
		sourceMatrix.setCharacters(newSourceCharacters);

		final List<IStandardCell> removedSourceCells = newArrayList();

		for (final IOtu sourceOTU : sourceMatrix.getParent().getOTUs()) {
			final IStandardRow sourceRow = sourceMatrix.getRows()
					.get(sourceOTU);
			final List<IStandardCell> newSourceCells =
					newArrayList(sourceRow.getCells());
			newSourceCells.remove(
							sourceRow.getCells()
									.size() / 2);

			sourceRow.setCells(newSourceCells);
		}

		for (final IOtu targetOTU : targetMatrix.getParent().getOTUs()) {
			final IStandardRow targetRow = targetMatrix.getRows()
					.get(targetOTU);
			// It will be the _last_ cell in the row that is deleted by the dao
			removedSourceCells
					.add(targetRow
							.getCells()
							.get(targetRow
									.getCells()
									.size() - 1));
		}

		createOrUpdateStandardMatrix.createOrUpdateMatrix(
					targetMatrix,
					sourceMatrix);

		ModelAssert.assertEqualsStandardMatrices(targetMatrix, sourceMatrix);
	}
}
