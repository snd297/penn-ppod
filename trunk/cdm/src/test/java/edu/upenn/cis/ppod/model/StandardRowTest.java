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
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardRow;

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

	private List<IOTU> otus;

	private StandardMatrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = matrixProvider.get();
		matrix.setParent(otuSetProvider.get());
		otus = newArrayList();
		otus.add(otuProvider.get().setLabel("OTU-0"));
		matrix.getParent().setOTUs(newArrayList(otus.get(0)));
		matrix.putRow(otus.get(0), rowProvider.get());
		final IStandardCharacter character0 = characterProvider.get();
		character0.setLabel("character-0");
		matrix.setCharacters(newArrayList(character0));
	}

	@Test
	public void addCellToMatrixWOneCharacter() {
		final StandardCell cell = cellProvider.get();
		cell.setUnassigned();
		matrix.getRows().get(matrix.getParent().getOTUs().get(0)).setCells(
				Arrays.asList(cell));

		ModelAssert.assertEqualsStandardCells(cell, matrix.getRows().get(
				otus.get(0)).getCells().get(0));
	}

	@Test
	void setCells() {
		matrix = matrixProvider.get();
		matrix.setParent(otuSetProvider.get());
		otus = newArrayList();
		final IOTU otu0 = otuProvider.get().setLabel("OTU-0");
		otus.add(otu0);
		matrix.getParent().setOTUs(newArrayList(otus.get(0)));
		matrix.putRow(otus.get(0), rowProvider.get());

		final IStandardRow row = matrix.getRows().get(otu0);

		final ImmutableList<StandardCharacter> characters =
				ImmutableList.of(
						characterProvider.get(),
						characterProvider.get(),
						characterProvider.get());

		characters.get(0).setLabel("character-0");
		characters.get(1).setLabel("character-1");
		characters.get(2).setLabel("character-2");

		matrix.setCharacters(characters);

		final List<StandardCell> cells =
				ImmutableList.of(cellProvider.get(), cellProvider.get(),
						cellProvider.get());
		row.setCells(cells);
		assertEquals(row.getCells(), cells);

		assertSame(cells.get(0).getParent(), row);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToRowThatsNotInAMatrix() {
		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matterJust call setUnassigned so that the cell is in
		// a legal state - it shouldn't really matter
		final StandardCell cell = cellProvider.get();
		cell.setUnassigned();
		rowProvider.get().setCells(Arrays.asList(cell));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixThatHasNoCharacters() {

		@SuppressWarnings("unchecked")
		final List<StandardCharacter> emptyList = (List<StandardCharacter>) Collections.EMPTY_LIST;

		matrix.resizeColumnVersionInfos(0);

		matrix.setCharacters(emptyList);

		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matter.
		final StandardCell cell = cellProvider.get();
		cell.setUnassigned();
		matrix.getRows().get(
				matrix.getParent().getOTUs().get(0))
				.setCells(Arrays.asList(cell));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixWTooFewCharacters() {
		final StandardCell cell0 = cellProvider.get();
		cell0.setUnassigned();

		final StandardCell cell1 = cellProvider.get();
		cell1.setUnassigned();
		final List<StandardCell> cells =
				newArrayList(cell0, cell1);

		matrix.getRows().get(matrix.getParent().getOTUs().get(0))
				.setCells(cells);
	}
}
