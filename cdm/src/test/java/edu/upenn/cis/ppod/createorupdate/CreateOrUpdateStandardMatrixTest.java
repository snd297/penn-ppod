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

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Tests of {@link ICreateOrUpdateCharacterStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.BROKEN })
public class CreateOrUpdateStandardMatrixTest {

	// @Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
	// dataProviderClass = PPodEntityProvider.class)
	// public void create(final PPodStandardMatrix sourceMatrix) {
	//
	// final VersionInfo versionInfo = mock(VersionInfo.class);
	// final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
	// when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);
	//
	// final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
	// new CreateOrUpdateStandardMatrix(
	// mock(IObjectWithLongIdDAO.class),
	// newVersionInfo);
	//
	// final OtuSet fakeDbOTUSet = sourceMatrix.getParent();
	//
	// final StandardMatrix targetMatrix = new StandardMatrix();
	//
	// fakeDbOTUSet.addStandardMatrix(targetMatrix);
	//
	// createOrUpdateStandardMatrix
	// .createOrUpdateMatrix(targetMatrix, sourceMatrix);
	//
	// ModelAssert.assertEqualsStandardMatrices(
	// targetMatrix,
	// sourceMatrix);
	// }
	//
	// @Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
	// dataProviderClass = PPodEntityProvider.class)
	// public void moveRows(final StandardMatrix sourceMatrix) {
	//
	// final VersionInfo versionInfo = mock(VersionInfo.class);
	// final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
	// when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);
	//
	// final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
	// new CreateOrUpdateStandardMatrix(
	// mock(IObjectWithLongIdDAO.class),
	// newVersionInfo);
	//
	// final OtuSet fakeDbOTUSet = sourceMatrix.getParent();
	//
	// final StandardMatrix targetMatrix =
	// new StandardMatrix();
	//
	// fakeDbOTUSet.addStandardMatrix(targetMatrix);
	//
	// createOrUpdateStandardMatrix
	// .createOrUpdateMatrix(targetMatrix, sourceMatrix);
	//
	// // Simulate passing back in the persisted characters: so we need to
	// // assign the proper pPOD ID's.
	// for (int i = 0; i < sourceMatrix.getColumnVersionInfos().size(); i++) {
	// sourceMatrix.getCharacters().get(i).setPPodId(
	// targetMatrix.getCharacters().get(i).getPPodId());
	// }
	//
	// final List<Otu> shuffledSourceOTUs =
	// newArrayList(sourceMatrix.getParent().getOtus());
	// shuffledSourceOTUs.set(0,
	// sourceMatrix.getParent()
	// .getOtus()
	// .get(shuffledSourceOTUs.size() / 2));
	// shuffledSourceOTUs.set(
	// shuffledSourceOTUs.size() / 2,
	// sourceMatrix.getParent()
	// .getOtus()
	// .get(0));
	//
	// sourceMatrix.getParent().setOtus(shuffledSourceOTUs);
	//
	// createOrUpdateStandardMatrix
	// .createOrUpdateMatrix(targetMatrix, sourceMatrix);
	//
	// ModelAssert.assertEqualsStandardMatrices(
	// targetMatrix,
	// sourceMatrix);
	// }
	//
	// @Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
	// dataProviderClass = PPodEntityProvider.class)
	// public void moveCharacters(final StandardMatrix sourceMatrix) {
	//
	// final VersionInfo versionInfo = mock(VersionInfo.class);
	// final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
	// when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);
	//
	// final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
	// new CreateOrUpdateStandardMatrix(
	// mock(IObjectWithLongIdDAO.class),
	// newVersionInfo);
	//
	// final OtuSet fakeDbOTUSet = sourceMatrix.getParent();
	//
	// final StandardMatrix targetMatrix = new StandardMatrix();
	//
	// fakeDbOTUSet.addStandardMatrix(targetMatrix);
	//
	// createOrUpdateStandardMatrix.createOrUpdateMatrix(
	// targetMatrix,
	// sourceMatrix);
	//
	// // Simulate passing back in the persisted characters: so we need to
	// // assign the proper pPOD ID's.
	// for (int i = 0; i < sourceMatrix.getColumnVersionInfos().size(); i++) {
	// sourceMatrix.getCharacters().get(i).setPPodId(
	// targetMatrix.getCharacters().get(i).getPPodId());
	// }
	//
	// // Swap 2 and 0
	// final List<StandardCharacter> newSourceMatrixCharacters =
	// newArrayList(sourceMatrix.getCharacters());
	//
	// newSourceMatrixCharacters.set(0,
	// sourceMatrix.getCharacters()
	// .get(2));
	// newSourceMatrixCharacters.set(2,
	// sourceMatrix.getCharacters().get(0));
	// sourceMatrix.setCharacters(newSourceMatrixCharacters);
	//
	// for (final Otu sourceOTU : sourceMatrix.getParent().getOtus()) {
	// final StandardRow sourceRow =
	// sourceMatrix.getRows().get(sourceOTU);
	//
	// final List<StandardCell> newSourceCells =
	// newArrayList(sourceRow.getCells());
	//
	// newSourceCells.set(0, sourceRow.getCells().get(2));
	// newSourceCells.set(2, sourceRow.getCells().get(0));
	// sourceRow.setCells(newSourceCells);
	// }
	//
	// createOrUpdateStandardMatrix.createOrUpdateMatrix(
	// targetMatrix,
	// sourceMatrix);
	//
	// ModelAssert.assertEqualsStandardMatrices(
	// targetMatrix, sourceMatrix);
	// }
	//
	// @Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
	// dataProviderClass = PPodEntityProvider.class)
	// public void removeColumn(final StandardMatrix sourceMatrix) {
	//
	// final VersionInfo versionInfo = mock(VersionInfo.class);
	// final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
	// when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);
	//
	// final ICreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
	// new CreateOrUpdateStandardMatrix(
	// mock(IObjectWithLongIdDAO.class),
	// newVersionInfo);
	//
	// final OtuSet fakeDbOTUSet = sourceMatrix.getParent();
	//
	// final StandardMatrix targetMatrix =
	// new StandardMatrix();
	//
	// fakeDbOTUSet.addStandardMatrix(targetMatrix);
	//
	// createOrUpdateStandardMatrix
	// .createOrUpdateMatrix(targetMatrix, sourceMatrix);
	//
	// // Simulate passing back in the persisted characters: so we need to
	// // assign the proper pPOD ID's.
	// for (int i = 0; i < sourceMatrix.getColumnVersionInfos().size(); i++) {
	// sourceMatrix.getCharacters()
	// .get(i)
	// .setPPodId(targetMatrix
	// .getCharacters()
	// .get(i)
	// .getPPodId());
	// }
	//
	// final List<StandardCharacter> newSourceCharacters =
	// newArrayList(sourceMatrix.getCharacters());
	// newSourceCharacters.remove(
	// sourceMatrix
	// .getCharacters().size() / 2);
	// sourceMatrix.setCharacters(newSourceCharacters);
	//
	// final List<StandardCell> removedSourceCells = newArrayList();
	//
	// for (final Otu sourceOTU : sourceMatrix.getParent().getOtus()) {
	// final StandardRow sourceRow = sourceMatrix.getRows()
	// .get(sourceOTU);
	// final List<StandardCell> newSourceCells =
	// newArrayList(sourceRow.getCells());
	// newSourceCells.remove(
	// sourceRow.getCells()
	// .size() / 2);
	//
	// sourceRow.setCells(newSourceCells);
	// }
	//
	// for (final Otu targetOTU : targetMatrix.getParent().getOtus()) {
	// final StandardRow targetRow = targetMatrix.getRows()
	// .get(targetOTU);
	// // It will be the _last_ cell in the row that is deleted by the dao
	// removedSourceCells
	// .add(targetRow
	// .getCells()
	// .get(targetRow
	// .getCells()
	// .size() - 1));
	// }
	//
	// createOrUpdateStandardMatrix.createOrUpdateMatrix(
	// targetMatrix,
	// sourceMatrix);
	//
	// ModelAssert.assertEqualsStandardMatrices(targetMatrix, sourceMatrix);
	// }
}
