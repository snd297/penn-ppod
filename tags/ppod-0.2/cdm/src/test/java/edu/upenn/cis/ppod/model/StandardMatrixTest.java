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
package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Logic tests of {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class StandardMatrixTest {

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	private OtuSet otuSet012;
	private Otu otu0;
	private Otu otu1;
	private Otu otu2;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	private StandardMatrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = new StandardMatrix();

		otu0 = new Otu();
		otu0.setLabel("otu0");

		otu1 = new Otu();
		otu1.setLabel("otu1");

		otu2 = new Otu();
		otu2.setLabel("otu2");

		otuSet012 = new OtuSet();
		otuSet012.clearAndAddOtus(newArrayList(otu0, otu1, otu2));

		otuSet012.addStandardMatrix(matrix);

	}

	@Test
	public void setOTUsWReorderedOTUs() {

		final StandardCharacter standardCharacter = new StandardCharacter();
		standardCharacter.setLabel("testLabel");
		matrix.clearAndAddCharacters(newArrayList(standardCharacter));

		matrix.putRow(otu0, new StandardRow());
		final StandardCell cell00 = new StandardCell();
		matrix.getRows().get(otu0).clearAndAddCells(
				Arrays.asList(new StandardCell[] { cell00 }));
		standardCharacter.addState(new StandardState(0));

		final StandardState state = standardCharacter.getStates().get(0);
		assertNotNull(state);

		cell00.setSingle(state.getStateNumber());

		matrix.putRow(otu1, new StandardRow());
		final StandardCell cell10 = new StandardCell();
		matrix.getRows().get(otu1).clearAndAddCells(
				Arrays.asList(new StandardCell[] { cell10 }));

		final StandardState state1 = new StandardState(1);
		standardCharacter.addState(state1);
		cell10.setSingle(state1.getStateNumber());

		matrix.putRow(otu2, new StandardRow());
		final StandardCell cell20 = new StandardCell();
		matrix.getRows().get(otu2).clearAndAddCells(
				Arrays.asList(new StandardCell[] { cell20 }));

		final StandardState state0 = new StandardState(0);
		standardCharacter.addState(state0);
		cell20.setSingle(state0.getStateNumber());

		final int originalRowsSize = matrix.getRows().size();

		final ImmutableList<Otu> otus210 = ImmutableList.of(otu2, otu1, otu0);
		matrix.getParent().clearAndAddOtus(otus210);

		assertEquals(matrix.getParent().getOtus(), otus210);
		assertEquals(matrix.getRows().size(), originalRowsSize);

	}

	@Test
	public void setOTUsWithLessOTUs() {

		otuSet012.clearAndAddOtus(newArrayList(otu1, otu2));

		final ImmutableList<Otu> otus12 = ImmutableList.of(otu1, otu2);

		assertEquals(matrix.getParent().getOtus(), otus12);
		assertEquals(matrix.getRows().size(), otus12.size());
	}

	/**
	 * Straight {@link CharacterStateMatrix#setCharacters(List)} test.
	 */
	@Test
	public void setCharacters() {

		final ImmutableList<StandardCharacter> standardCharacters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		standardCharacters.get(0).setLabel("character-0");
		standardCharacters.get(1).setLabel("character-1");
		standardCharacters.get(2).setLabel("character-2");

		matrix.clearAndAddCharacters(standardCharacters);

		assertNotSame(matrix.getCharacters(), standardCharacters);
		Assert.assertEquals(matrix.getCharacters(),
				standardCharacters);
	}

	/**
	 * Make sure when we move a character it the character->position map is
	 * updated.
	 */
	@Test
	public void moveCharacter() {
		final ImmutableList<StandardCharacter> standardCharacters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		standardCharacters.get(0).setLabel("character-0");
		standardCharacters.get(1).setLabel("character-1");
		standardCharacters.get(2).setLabel("character-2");

		matrix.clearAndAddCharacters(standardCharacters);

		final ImmutableList<StandardCharacter> shuffledCharacters = ImmutableList
				.of(
						standardCharacters.get(1), standardCharacters.get(2),
						standardCharacters.get(0));

		matrix.clearAndAddCharacters(shuffledCharacters);

		assertNotSame(matrix.getCharacters(), shuffledCharacters);
		assertEquals(matrix.getCharacters(), shuffledCharacters);
	}

	/**
	 * Test replacing all of the characters.
	 */
	@Test
	public void replaceCharacters() {
		final ImmutableList<StandardCharacter> characters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		characters.get(0).setLabel("character-0");
		characters.get(1).setLabel("character-1");
		characters.get(2).setLabel("character-2");

		matrix.clearAndAddCharacters(characters);

		final ImmutableList<StandardCharacter> characters2 =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		characters.get(0).setLabel("character-2-0");
		characters.get(1).setLabel("character-2-1");
		characters.get(2).setLabel("character-2-2");

		matrix.clearAndAddCharacters(characters2);
		assertEquals(matrix.getCharacters(), characters2);

		// assertEquals(matrix.getCharacterPosition(characters2.get(0)),
		// Integer.valueOf(0));
		// assertEquals(matrix.getCharacterPosition(characters2.get(1)),
		// Integer.valueOf(1));
		// assertEquals(matrix.getCharacterPosition(characters2.get(2)),
		// Integer.valueOf(2));

	}

	/**
	 * When we set characters to the same characters, the matrix should not be
	 * marked in need of a new version number.
	 */
	@Test
	public void setCharactersWithEqualsCharacters() {
		final ImmutableList<StandardCharacter> characters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		characters.get(0).setLabel("character-0");
		characters.get(1).setLabel("character-1");
		characters.get(2).setLabel("character-2");

		matrix.clearAndAddCharacters(characters);

		matrix.clearAndAddCharacters(characters);

	}

	/**
	 * Test replacing one row with another.
	 */
	@Test
	public void replaceRow() {
		final StandardRow row1 = new StandardRow();
		matrix.putRow(otu1, row1);
		final StandardRow row2 = new StandardRow();
		matrix.putRow(otu1, row2);
		assertNull(row1.getParent());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setCharactersW2EqualsCharacters() {
		final StandardCharacter character0 = new StandardCharacter();
		character0.setLabel("character0");
		final StandardCharacter character1 = new StandardCharacter();
		character1.setLabel("character1");

		final ImmutableList<StandardCharacter> standardCharacters = ImmutableList
				.of(
						character0, character1, character0);
		matrix.clearAndAddCharacters(standardCharacters);
	}
}
