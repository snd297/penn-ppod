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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
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
public class StandardRowTest {

	@Inject
	private Provider<StandardMatrix> matrixProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<StandardCharacter> characterProvider;

	@Inject
	private Provider<StandardRow> rowProvider;

	@Inject
	private Provider<StandardCell> cellProvider;

	private List<OTU> otus;

	private StandardMatrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = matrixProvider.get();
		matrix.setOTUSet(otuSetProvider.get());
		otus = newArrayList();
		otus.add(otuProvider.get().setLabel("OTU-0"));
		matrix.getOTUSet().setOTUs(newArrayList(otus.get(0)));
		matrix.putRow(otus.get(0), rowProvider.get());
		matrix.setCharacters(
				newArrayList(
						characterProvider.get().setLabel("CHARACTER-0")));
	}

	@Test
	public void addCellToMatrixWOneCharacter() {
		final StandardCell cell = (StandardCell) cellProvider.get()
				.setUnassigned();
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCellsPublic(
				Arrays.asList(cell));

		ModelAssert.assertEqualsStandardCells(cell, matrix.getRow(
				otus.get(0)).getCells().get(0));
	}

	@Test
	void setCells() {
		matrix = matrixProvider.get();
		matrix.setOTUSet(otuSetProvider.get());
		otus = newArrayList();
		final OTU otu0 = otuProvider.get().setLabel("OTU-0");
		otus.add(otu0);
		matrix.getOTUSet().setOTUs(newArrayList(otus.get(0)));
		matrix.putRow(otus.get(0), rowProvider.get());

		final StandardRow row = matrix.getRow(otu0);
		matrix.
				setCharacters(ImmutableList.of(
						characterProvider.get().setLabel("CHARACTER-0"),
						characterProvider.get().setLabel("CHARACTER-1"),
						characterProvider.get().setLabel("CHARACTER-2")));

		final List<StandardCell> cells =
				ImmutableList.of(cellProvider.get(), cellProvider.get(),
						cellProvider.get());
		row.setCellsPublic(cells);
		assertEquals(row.getCells(), cells);

		assertSame(cells.get(0).getRow(), row);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToRowThatsNotInAMatrix() {
		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matterJust call setUnassigned so that the cell is in
		// a legal state - it shouldn't really matter
		rowProvider.get().setCellsPublic(
				Arrays.asList((StandardCell) cellProvider.get()
						.setUnassigned()));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixThatHasNoCharacters() {

		@SuppressWarnings("unchecked")
		final List<StandardCharacter> emptyList = (List<StandardCharacter>) Collections.EMPTY_LIST;

		matrix.setColumnsSize(0);

		matrix.setCharacters(emptyList);

		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matterJust call setUnassigned so that the cell is in
		// a legal state - it shouldn't really matter
		matrix.getRow(
				matrix.getOTUSet().getOTUs().get(0))
				.setCellsPublic(
						Arrays.asList((StandardCell) cellProvider.get()
								.setUnassigned()));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixWTooFewCharacters() {
		final List<StandardCell> cells = newArrayList(
				(StandardCell) cellProvider.get()
						.setUnassigned(), (StandardCell) cellProvider
						.get().setUnassigned());
		matrix.getRow(matrix.getOTUSet().getOTUs().get(0)).setCellsPublic(cells);
	}
}
