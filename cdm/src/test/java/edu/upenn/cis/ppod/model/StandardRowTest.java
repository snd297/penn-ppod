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

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * {@link CharacterStateRow} test.
 * 
 * @author Sam Donnelly
 * 
 */
@Test(groups = TestGroupDefs.FAST)
public class StandardRowTest {

	private List<Otu> otus;

	private StandardMatrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = new StandardMatrix();
		matrix.setParent(new OtuSet());
		otus = newArrayList();
		otus.add(new Otu("OTU-0"));
		matrix.getParent().setOtus(newArrayList(otus.get(0)));
		matrix.putRow(otus.get(0), new StandardRow());
		final StandardCharacter character0 = new StandardCharacter();
		character0.setLabel("character-0");
		matrix.setCharacters(newArrayList(character0));
	}

	@Test
	public void addCellToMatrixWOneCharacter() {
		final StandardCell cell = new StandardCell();
		cell.setUnassigned();
		matrix.getRows().get(matrix.getParent().getOtus().get(0)).setCells(
				Arrays.asList(cell));
		assertSame(matrix.getRows().get(matrix.getParent().getOtus().get(0))
				.getCells().get(0),
				cell);
	}

	@Test
	void setCells() {
		matrix = new StandardMatrix();
		matrix.setParent(new OtuSet());
		otus = newArrayList();
		final Otu otu0 = new Otu();
		otu0.setLabel("OTU-0");
		otus.add(otu0);
		matrix.getParent().setOtus(newArrayList(otus.get(0)));
		matrix.putRow(otus.get(0), new StandardRow());

		final StandardRow row = matrix.getRows().get(otu0);

		final ImmutableList<StandardCharacter> characters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		characters.get(0).setLabel("character-0");
		characters.get(1).setLabel("character-1");
		characters.get(2).setLabel("character-2");

		matrix.setCharacters(characters);

		final List<StandardCell> cells =
				ImmutableList.of(new StandardCell(), new StandardCell(),
						new StandardCell());
		row.setCells(cells);
		assertEquals(row.getCells(), cells);

		assertSame(cells.get(0).getParent(), row);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToRowThatsNotInAMatrix() {
		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matterJust call setUnassigned so that the cell is in
		// a legal state - it shouldn't really matter
		final StandardCell cell = new StandardCell();
		cell.setUnassigned();
		new StandardRow().setCells(Arrays.asList(cell));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixThatHasNoCharacters() {

		@SuppressWarnings("unchecked")
		final List<StandardCharacter> emptyList = (List<StandardCharacter>) Collections.EMPTY_LIST;

		matrix.setCharacters(emptyList);

		// Just call setUnassigned so that the cell is in a legal state - it
		// shouldn't really matter.
		final StandardCell cell = new StandardCell();
		cell.setUnassigned();
		matrix.getRows().get(
				matrix.getParent().getOtus().get(0))
				.setCells(Arrays.asList(cell));
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void addCellToMatrixWTooFewCharacters() {
		final StandardCell cell0 = new StandardCell();
		cell0.setUnassigned();

		final StandardCell cell1 = new StandardCell();
		cell1.setUnassigned();
		final List<StandardCell> cells =
				newArrayList(cell0, cell1);

		matrix.getRows().get(matrix.getParent().getOtus().get(0))
				.setCells(cells);
	}
}
