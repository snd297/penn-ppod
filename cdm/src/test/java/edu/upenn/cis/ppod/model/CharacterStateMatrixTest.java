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
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Logic tests of {@link CharacterStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class CharacterStateMatrixTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private PPodVersionInfo pPodVersionInfo;

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	@Inject
	private Provider<Character> characterProvider;

	@Inject
	private Provider<CharacterStateRow> rowProvider;

	@Inject
	private Provider<CharacterStateCell> cellProvider;

	@Inject
	private CharacterState.IFactory stateFactory;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	private OTUSet otuSet012;
	private OTU otu0;
	private OTU otu1;
	private OTU otu2;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	private CharacterStateMatrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = matrixProvider.get();

		otu0 = otuProvider.get().setLabel("otu0");
		otu1 = otuProvider.get().setLabel("otu1");
		otu2 = otuProvider.get().setLabel("otu2");

		otuSet012 = otuSetProvider.get();
		otuSet012.setOTUs(newArrayList(otu0, otu1, otu2));

		final ImmutableSet<CharacterStateMatrix> matrices = ImmutableSet
				.of(matrix);

		otuSet012.setCharacterStateMatrices(matrices);

	}

	/**
	 * When we set with the same OTU's, the pPOD version number of the matrix
	 * should not change.
	 */
	@Test
	public void setOTUsWSameOTUs() {
		matrix.setPPodVersionInfo(pPodVersionInfo);

		// Since they were the same, the version should not have been reset to
		// null
		assertNotNull(matrix.getPPodVersionInfo());
	}

	@Test
	public void setOTUsWReorderedOTUs() {

		final Character character = characterProvider.get().setLabel(
				"testLabel");
		matrix.setCharacters(newArrayList(character));

		matrix.putRow(otu0, rowProvider.get());
		final CharacterStateCell cell00 = cellProvider.get();
		matrix.getRow(otu0).setCells(
				Arrays.asList(new CharacterStateCell[] { cell00 }));
		cell00.setSingleElement(stateFactory.create(0).setCharacter(character));

		matrix.putRow(otu1, rowProvider.get());
		final CharacterStateCell cell10 = cellProvider.get();
		matrix.getRow(otu1).setCells(
				Arrays.asList(new CharacterStateCell[] { cell10 }));

		final CharacterState state1 = stateFactory.create(1);
		character.putState(state1);
		cell10.setSingleElement(state1);

		matrix.putRow(otu2, rowProvider.get());
		final CharacterStateCell cell20 = cellProvider.get();
		matrix.getRow(otu2).setCells(
				Arrays.asList(new CharacterStateCell[] { cell20 }));

		final CharacterState state0 = stateFactory.create(0);
		character.putState(state0);
		cell20.setSingleElement(state0);

		final int originalRowsSize = matrix.getRows().size();

		final ImmutableList<OTU> otus210 = ImmutableList.of(otu2, otu1, otu0);
		matrix.getOTUSet().setOTUs(otus210);

		assertEquals(matrix.getOTUSet().getOTUs(), otus210);
		assertEquals(matrix.getRows().size(), originalRowsSize);

	}

	@Test
	public void setOTUsWithLessOTUs() {

		otuSet012.setOTUs(newArrayList(otu1, otu2));

		final ImmutableList<OTU> otus12 = ImmutableList.of(otu1, otu2);

		assertEquals(matrix.getOTUSet().getOTUs(), otus12);
		assertEquals(matrix.getRows().size(), otus12.size());
	}

	/**
	 * Straight {@link CharacterStateMatrix#setCharacters(List)} test.
	 */
	@Test
	public void setCharacters() {

		final ImmutableList<Character> characters = ImmutableList.of(
				characterProvider.get().setLabel("character0"),
				characterProvider.get().setLabel("character1"),
				characterProvider.get().setLabel("character2"));

		matrix.setCharacters(characters);

		assertNotSame(matrix.getCharactersModifiable(), characters);
		Assert.assertEquals(matrix.getCharactersModifiable(), characters);

		Assert.assertEquals(matrix.getCharactersToPositions()
				.get(characters.get(0)),
				Integer.valueOf(0));
		Assert.assertEquals(matrix.getCharactersToPositions()
				.get(characters.get(1)),
				Integer.valueOf(1));
		Assert.assertEquals(matrix.getCharactersToPositions()
				.get(characters.get(2)),
				Integer.valueOf(2));
	}

	/**
	 * Make sure when we move a character it the character->position map is
	 * updated.
	 */
	@Test
	public void moveCharacter() {

		final ImmutableList<Character> characters = ImmutableList.of(
				characterProvider.get().setLabel("character0"),
				characterProvider.get().setLabel("character1"),
				characterProvider.get().setLabel("character2"));

		final PPodVersionInfo pPodVersionInfo0 = pPodVersionInfoProvider.get();
		final PPodVersionInfo pPodVersionInfo1 = pPodVersionInfoProvider.get();
		final PPodVersionInfo pPodVersionInfo2 = pPodVersionInfoProvider.get();

		matrix.setCharacters(characters);

		matrix.getColumnPPodVersionInfosModifiable().set(0, pPodVersionInfo0);
		matrix.getColumnPPodVersionInfosModifiable().set(1, pPodVersionInfo1);
		matrix.getColumnPPodVersionInfosModifiable().set(2, pPodVersionInfo2);

		final ImmutableList<Character> shuffledCharacters = ImmutableList.of(
				characters.get(1), characters.get(2), characters.get(0));

		matrix.setCharacters(shuffledCharacters);

		assertNotSame(matrix.getCharactersModifiable(), shuffledCharacters);
		assertEquals(matrix.getCharactersModifiable(), shuffledCharacters);

		Assert.assertEquals(
				matrix.getCharactersToPositions().get(
						shuffledCharacters.get(0)),
				Integer.valueOf(0));
		Assert.assertEquals(
				matrix.getCharactersToPositions().get(
						shuffledCharacters.get(1)),
				Integer.valueOf(1));
		Assert.assertEquals(
				matrix.getCharactersToPositions().get(
						shuffledCharacters.get(2)),
				Integer.valueOf(2));

// assertEquals(matrix.getColumnPPodVersionInfos().get(0),
// pPodVersionInfo1);
// assertEquals(matrix.getColumnPPodVersionInfos().get(1),
// pPodVersionInfo2);
// assertEquals(matrix.getColumnPPodVersionInfos().get(2),
// pPodVersionInfo0);

	}

	/**
	 * Test replacing all of the characters.
	 */
	@Test
	public void replaceCharacters() {
		final ImmutableList<Character> characters = ImmutableList.of(
				characterProvider.get().setLabel("character0"),
				characterProvider.get().setLabel("character1"),
				characterProvider.get().setLabel("character2"));

		final PPodVersionInfo pPodVersionInfo0 = pPodVersionInfoProvider.get();
		final PPodVersionInfo pPodVersionInfo1 = pPodVersionInfoProvider.get();
		final PPodVersionInfo pPodVersionInfo2 = pPodVersionInfoProvider.get();

		matrix.setCharacters(characters);

		matrix.getColumnPPodVersionInfosModifiable().set(0, pPodVersionInfo0);
		matrix.getColumnPPodVersionInfosModifiable().set(1, pPodVersionInfo1);
		matrix.getColumnPPodVersionInfosModifiable().set(2, pPodVersionInfo2);

		final ImmutableList<Character> characters2 = ImmutableList.of(
				characterProvider.get().setLabel("character2-0"),
				characterProvider.get().setLabel("character2-1"),
				characterProvider.get().setLabel("character2-2"));

		matrix.unsetInNeedOfNewPPodVersionInfo();

		matrix.setCharacters(characters2);
		assertTrue(matrix.isInNeedOfNewPPodVersionInfo());

		assertEquals(matrix.getCharacterPosition(characters2.get(0)),
				Integer.valueOf(0));
		assertEquals(matrix.getCharacterPosition(characters2.get(1)),
				Integer.valueOf(1));
		assertEquals(matrix.getCharacterPosition(characters2.get(2)),
				Integer.valueOf(2));

	}

	/**
	 * When we set characters to the same characters, the matrix should not be
	 * marked in need of a new version number.
	 */
	@Test
	public void setCharactersWithEqualsCharacters() {
		final ImmutableList<Character> characters = ImmutableList.of(
				characterProvider.get().setLabel("character0"),
				characterProvider.get().setLabel("character1"),
				characterProvider.get().setLabel("character2"));

		matrix.setCharacters(characters);

		matrix.unsetInNeedOfNewPPodVersionInfo();

		matrix.setCharacters(characters);

		assertFalse(matrix.isInNeedOfNewPPodVersionInfo());

	}

	/**
	 * When we set a character that was already at some position, then it should
	 * not be marked as in need of a new pPOD version info.
	 */
	@Test
	public void setWithSameRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.putRow(otu1, row1);
		matrix.setPPodVersionInfo(pPodVersionInfo);
		matrix.putRow(otu1, row1);
		assertFalse(matrix.isInNeedOfNewPPodVersionInfo());
	}

// /**
// * When we move a row, its previous position should be automatically null'd.
// */
// public void moveRow() {
// final CharacterStateRow row1 = rowProvider.get();
// matrix.putRow(otu1, row1);
// matrix.putRow(otu0, row1);
// assertEquals(matrix.getRows().get(matrix.getOTUIdx().get(otu0)), row1);
// assertNull(matrix.getRows().get(matrix.getOTUIdx().get(otu1)));
// }

	/**
	 * Test replacing one row with another.
	 * <ul>
	 * <li>{@link CharacterStateMatrix#setRow(OTU, CharacterStateRow)} should
	 * return the row that had been there</li>
	 * <li>the replaced row should have its matrix (accessed by
	 * {@link CharacterStateRow#getMatrix()}) set to {@code null}</li>
	 * </ul>
	 */
	@Test
	public void replaceRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.putRow(otu1, row1);
		final CharacterStateRow row2 = rowProvider.get();
		final CharacterStateRow someRow = matrix.putRow(otu1, row2);
		assertEquals(someRow, row1);
		assertNull(row1.getMatrix());
	}

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	@Test
	public void beforeMarshal() {
		nullFillAndSet(matrix.getColumnPPodVersionInfosModifiable(), 2,
				pPodVersionInfoProvider.get().setPPodVersion(3L));
		nullFillAndSet(matrix.getColumnPPodVersionInfosModifiable(), 5,
				pPodVersionInfoProvider.get().setPPodVersion(8L));

		matrix.beforeMarshal(null);
		assertEquals(matrix.getColumnPPodVersionsModifiable().size(), matrix
				.getColumnPPodVersionInfos().size());
		for (int i = 0; i < matrix.getColumnPPodVersionInfos().size(); i++) {
			if (matrix.getColumnPPodVersionInfos().get(i) == null) {
				assertNull(matrix.getColumnPPodVersionsModifiable().get(i));
			} else {
				assertEquals(matrix.getColumnPPodVersionsModifiable().get(i), matrix
						.getColumnPPodVersionInfos().get(i).getPPodVersion());
			}
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setCharactersW2EqualsCharacters() {
		final Character character0 = characterProvider.get().setLabel(
				"character0");
		final Character character1 = characterProvider.get().setLabel(
				"character1");

		final ImmutableList<Character> characters = ImmutableList.of(
				character0, character1, character0);
		matrix.setCharacters(characters);
	}

	public void setColumnPPodVersionInfos() {
		final PPodVersionInfo pPodVersionInfo = pPodVersionInfoProvider.get();
		final CharacterStateMatrix returnedMatrix = matrix
				.setColumnPPodVersionInfos(pPodVersionInfo);
		assertSame(returnedMatrix, matrix);

		for (final PPodVersionInfo columnPPodVersionInfo : matrix
				.getColumnPPodVersionInfos()) {
			assertSame(columnPPodVersionInfo, pPodVersionInfo);
		}
	}

	public void setDescription() {
		matrix.unsetInNeedOfNewPPodVersionInfo();
		final String description = "DESCRIPTION";
		matrix.setDescription(description);
		assertEquals(matrix.getDescription(), description);
		assertTrue(matrix.isInNeedOfNewPPodVersionInfo());

		matrix.unsetInNeedOfNewPPodVersionInfo();
		matrix.setDescription(description);
		assertFalse(matrix.isInNeedOfNewPPodVersionInfo());

		matrix.unsetInNeedOfNewPPodVersionInfo();
		matrix.setDescription(null);
		assertNull(matrix.getDescription());
		assertTrue(matrix.isInNeedOfNewPPodVersionInfo());

		matrix.unsetInNeedOfNewPPodVersionInfo();
		matrix.setDescription(null);
		assertNull(matrix.getDescription());
		assertFalse(matrix.isInNeedOfNewPPodVersionInfo());
	}
}
