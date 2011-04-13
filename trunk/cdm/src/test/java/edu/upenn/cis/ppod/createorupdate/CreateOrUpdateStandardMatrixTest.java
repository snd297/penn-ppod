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

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.PPodOtu;
import edu.upenn.cis.ppod.PPodOtuSet;
import edu.upenn.cis.ppod.PPodStandardCell;
import edu.upenn.cis.ppod.PPodStandardCharacter;
import edu.upenn.cis.ppod.PPodStandardMatrix;
import edu.upenn.cis.ppod.PPodStandardRow;
import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.IStandardRowDAO;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
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
	public void create(final PPodOtuSet sourceOtuSet) {

		final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				new CreateOrUpdateStandardMatrix(
						mock(IStandardRowDAO.class));

		final PPodStandardMatrix sourceMatrix = getOnlyElement(sourceOtuSet
				.getStandardMatrices());

		final OtuSet targetOtuSet = new OtuSet();
		final StandardMatrix targetMatrix = new StandardMatrix();
		targetOtuSet.addStandardMatrix(targetMatrix);

		new MergeOtuSets().mergeOtuSets(targetOtuSet,
				sourceOtuSet);

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix, sourceMatrix);

		ModelAssert.assertEqualsStandardMatrices(
				targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
			dataProviderClass = PPodEntityProvider.class)
	public void moveRows(final PPodOtuSet sourceOtuSet) {

		final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				new CreateOrUpdateStandardMatrix(
						mock(IStandardRowDAO.class));

		final OtuSet targetOtuSet = new OtuSet();
		final StandardMatrix targetMatrix = new StandardMatrix();
		targetOtuSet.addStandardMatrix(targetMatrix);

		new MergeOtuSets().mergeOtuSets(targetOtuSet,
				sourceOtuSet);

		final PPodStandardMatrix sourceMatrix =
				getOnlyElement(sourceOtuSet.getStandardMatrices());

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix,
						getOnlyElement(sourceOtuSet.getStandardMatrices()));

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getCharacters().size(); i++) {
			sourceMatrix.getCharacters().get(i).setPPodId(
					targetMatrix.getCharacters().get(i).getPPodId());
		}

		final List<PPodOtu> shuffledSourceOTUs =
				newArrayList(sourceOtuSet.getOtus());
		shuffledSourceOTUs.set(0,
				sourceOtuSet.getOtus()
						.get(shuffledSourceOTUs.size() / 2));
		shuffledSourceOTUs.set(
				shuffledSourceOTUs.size() / 2,
				sourceOtuSet.getOtus().get(0));

		sourceOtuSet.setOtus(shuffledSourceOTUs);

		createOrUpdateStandardMatrix
				.createOrUpdateMatrix(targetMatrix, sourceMatrix);

		ModelAssert.assertEqualsStandardMatrices(
				targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = PPodEntityProvider.STANDARD_MATRICES_PROVIDER,
			dataProviderClass = PPodEntityProvider.class)
	public void moveCharacters(final PPodOtuSet sourceOtuSet) {

		final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				new CreateOrUpdateStandardMatrix(
						mock(IStandardRowDAO.class));

		final OtuSet targetOtuSet = new OtuSet();
		final StandardMatrix targetMatrix = new StandardMatrix();
		targetOtuSet.addStandardMatrix(targetMatrix);

		new MergeOtuSets().mergeOtuSets(targetOtuSet,
				sourceOtuSet);

		final PPodStandardMatrix sourceMatrix =
				getOnlyElement(sourceOtuSet.getStandardMatrices());

		createOrUpdateStandardMatrix.createOrUpdateMatrix(
				targetMatrix,
				sourceMatrix);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getCharacters().size(); i++) {
			sourceMatrix.getCharacters().get(i).setPPodId(
					targetMatrix.getCharacters().get(i).getPPodId());
		}

		// Swap 2 and 0
		final List<PPodStandardCharacter> newSourceMatrixCharacters =
				newArrayList(sourceMatrix.getCharacters());

		newSourceMatrixCharacters.set(0,
				sourceMatrix.getCharacters()
						.get(2));
		newSourceMatrixCharacters.set(2,
				sourceMatrix.getCharacters().get(0));
		sourceMatrix.setCharacters(newSourceMatrixCharacters);

		for (int ir = 0; ir < sourceMatrix.getRows().size(); ir++) {
			final PPodStandardRow sourceRow =
					sourceMatrix.getRows().get(ir);

			final List<PPodStandardCell> newSourceCells =
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
	public void removeColumn(final PPodOtuSet sourceOtuSet) {

		final CreateOrUpdateStandardMatrix createOrUpdateStandardMatrix =
				new CreateOrUpdateStandardMatrix(
						mock(IStandardRowDAO.class));

		final OtuSet targetOtuSet = new OtuSet();
		final StandardMatrix targetMatrix = new StandardMatrix();
		targetOtuSet.addStandardMatrix(targetMatrix);

		new MergeOtuSets().mergeOtuSets(targetOtuSet,
				sourceOtuSet);

		final PPodStandardMatrix sourceMatrix =
				getOnlyElement(sourceOtuSet.getStandardMatrices());

		createOrUpdateStandardMatrix.createOrUpdateMatrix(
				targetMatrix,
				sourceMatrix);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getCharacters().size(); i++) {
			sourceMatrix.getCharacters()
					.get(i)
					.setPPodId(targetMatrix
							.getCharacters()
							.get(i)
							.getPPodId());
		}

		final List<PPodStandardCharacter> newSourceCharacters =
				newArrayList(sourceMatrix.getCharacters());
		newSourceCharacters.remove(
				sourceMatrix
						.getCharacters().size() / 2);
		sourceMatrix.setCharacters(newSourceCharacters);

		final List<StandardCell> removedSourceCells = newArrayList();

		for (int ir = 0; ir < sourceMatrix.getRows().size(); ir++) {
			final PPodStandardRow sourceRow = sourceMatrix.getRows()
					.get(ir);
			final List<PPodStandardCell> newSourceCells =
					newArrayList(sourceRow.getCells());
			newSourceCells.remove(
					sourceRow.getCells()
							.size() / 2);

			sourceRow.setCells(newSourceCells);
		}

		for (final Otu targetOTU : targetMatrix.getParent().getOtus()) {
			final StandardRow targetRow = targetMatrix.getRows()
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
