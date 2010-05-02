/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * {@link CharacterStateRow} test.
 * 
 * @author Sam Donnelly
 * 
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class CharacterStateRowTest {

	@Inject
	private Provider<Matrix> matrixProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<AbstractCharacter> characterProvider;

	@Inject
	private Provider<CategoricalRow> rowProvider;

	@Inject
	private Provider<CategoricalCell> cellProvider;

	private List<OTU> rowIdxs;

	private Matrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = matrixProvider.get();
		matrix.setOTUSet(otuSetProvider.get());
		rowIdxs = newArrayList();
		rowIdxs.add(otuProvider.get().setLabel("OTU-0"));
		matrix.getOTUSet().setOTUs(newArrayList(rowIdxs.get(0)));
		matrix.putRow(rowIdxs.get(0), rowProvider.get());
		matrix.setCharacters(newArrayList(characterProvider.get().setLabel(
				"CHARACTER-0")));
	}

	public void addCellToMatrixWOneCharacter() {
		final CategoricalCell cell = cellProvider.get().setUnassigned();
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cell));

		ModelAssert.assertEqualsCharacterStateCells(cell, matrix.getRow(
				rowIdxs.get(0)).getCell(0));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToRowThatsNotInAMatrix() {
		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matterJust call setUnassigned so that the cell is in
		// a legal state - it shouldn't really matter
		rowProvider.get().setCells(
				Arrays.asList(cellProvider.get()
						.setUnassigned()));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixThatHasNoCharacters() {

		@SuppressWarnings("unchecked")
		final List<AbstractCharacter> emptyList = (List<AbstractCharacter>) Collections.EMPTY_LIST;

		matrix.setCharacters(emptyList);

		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matterJust call setUnassigned so that the cell is in
		// a legal state - it shouldn't really matter
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				Arrays.asList(cellProvider.get()
						.setUnassigned()));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixWTooFewCharacters() {
		final List<CategoricalCell> cells = newArrayList(cellProvider.get()
				.setUnassigned(), cellProvider.get().setUnassigned());
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(cells);
	}
}
