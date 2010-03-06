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
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
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

	private OTUSet otuSet012;
	private OTU otu0;
	private OTU otu1;
	private OTU otu2;
	private List<OTU> otus012 = newArrayList();
	private CharacterStateMatrix matrix;

	@BeforeMethod
	private void beforeMethod() {
		matrix = matrixProvider.get();

		otu0 = otuProvider.get().setLabel("otu0");
		otu1 = otuProvider.get().setLabel("otu1");
		otu2 = otuProvider.get().setLabel("otu2");

		otus012 = newArrayList(otu0, otu1, otu2);

		otuSet012 = otuSetProvider.get();
		otuSet012.setOTUs(newHashSet(otu0, otu1, otu2));

		matrix.setOTUSet(otuSet012);

		matrix.setOTUOrdering(otus012);
	}

	/**
	 * When we set with the same OTU's, the pPOD version number of the matrix
	 * should not change.
	 */
	public void setOTUsWSameOTUs() {
		matrix.setPPodVersionInfo(pPodVersionInfo);

		matrix.setOTUOrdering(otus012);

		// Since they were the same, the version should not have been reset to
		// null
		assertNotNull(matrix.getpPodVersionInfo());
	}

	public void setOTUsWReorderedOTUs() {

		final Character character = characterProvider.get().setLabel(
				"testLabel");
		matrix.setCharacters(newArrayList(character));

		matrix.putRow(otu0, rowProvider.get());
		final CharacterStateCell cell00 = cellProvider.get();
		matrix.getRows().get(0).setCells(newArrayList(cell00));
		cell00.setSingleState(stateFactory.create(0).setCharacter(character));

		matrix.putRow(otu1, rowProvider.get());
		final CharacterStateCell cell10 = cellProvider.get();
		matrix.getRows().get(1).setCells(newArrayList(cell10));

		final CharacterState state1 = stateFactory.create(1);
		character.addState(state1);
		cell10.setSingleState(state1);

		matrix.putRow(otu2, rowProvider.get());
		final CharacterStateCell cell20 = cellProvider.get();
		matrix.getRows().get(1).setCells(newArrayList(cell20));

		final CharacterState state0 = stateFactory.create(0);
		character.addState(state0);
		cell20.setSingleState(state0);

		final List<CharacterStateRow> originalRows = newArrayList(matrix
				.getRows());

		final List<OTU> otus210 = newArrayList(otu2, otu1, otu0);

		matrix.setOTUOrdering(otus210);

		assertEquals(matrix.getOTUOrdering(), otus210);
		assertEquals(matrix.getRows().size(), originalRows.size());
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRows().get(
				matrix.getOTUIdx().get(otu2)), originalRows.get(2));
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRows().get(
				matrix.getOTUIdx().get(otu1)), originalRows.get(1));
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRows().get(
				matrix.getOTUIdx().get(otu0)), originalRows.get(0));

	}

	public void setOTUsWLessOTUs() {

		otuSet012.setOTUs(newHashSet(otu1, otu2));

		final List<OTU> otus12 = newArrayList(otu1, otu2);
		matrix.setOTUOrdering(otus12);
		assertEquals(matrix.getOTUOrdering(), otus12);
		assertEquals(matrix.getRows().size(), otus12.size());
	}

	public void setOTUs() {
		Assert.assertEquals(matrix.getOTUOrdering(), newArrayList(otu0, otu1, otu2));

		for (int i = 0; i < matrix.getRows().size(); i++) {
			assertNull(matrix.getRows().get(i));
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setWrongOTUs() {
		matrix.setOTUOrdering(newArrayList(otu0, otu2));
	}

	/**
	 * Straight {@link CharacterStateMatrix#setCharacters(List)} test.
	 */
	public void setCharacters() {

		final ImmutableList<Character> characters = ImmutableList.of(
				characterProvider.get().setLabel("character0"),
				characterProvider.get().setLabel("character1"),
				characterProvider.get().setLabel("character2"));

		matrix.setCharacters(characters);

		assertNotSame(matrix.getCharacters(), characters);
		Assert.assertEquals(matrix.getCharacters(), characters);

		Assert.assertEquals(matrix.getCharacterIdx().get(characters.get(0)),
				Integer.valueOf(0));
		Assert.assertEquals(matrix.getCharacterIdx().get(characters.get(1)),
				Integer.valueOf(1));
		Assert.assertEquals(matrix.getCharacterIdx().get(characters.get(2)),
				Integer.valueOf(2));
	}

	/**
	 * Make sure when we move a character it gets moved and its column version
	 * comes with it
	 */
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

		assertNotSame(matrix.getCharacters(), shuffledCharacters);
		assertEquals(matrix.getCharacters(), shuffledCharacters);

		Assert.assertEquals(matrix.getCharacterIdx().get(
				shuffledCharacters.get(0)), Integer.valueOf(0));
		Assert.assertEquals(matrix.getCharacterIdx().get(
				shuffledCharacters.get(1)), Integer.valueOf(1));
		Assert.assertEquals(matrix.getCharacterIdx().get(
				shuffledCharacters.get(2)), Integer.valueOf(2));

		assertEquals(matrix.getColumnPPodVersionInfos().get(0),
				pPodVersionInfo1);
		assertEquals(matrix.getColumnPPodVersionInfos().get(1),
				pPodVersionInfo2);
		assertEquals(matrix.getColumnPPodVersionInfos().get(2),
				pPodVersionInfo0);

	}

	/**
	 * Test replacing all of the characters.
	 */
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

		for (final PPodVersionInfo columnPPodVersionInfo : matrix
				.getColumnPPodVersionInfos()) {
			assertNull(columnPPodVersionInfo);
		}

	}

	/**
	 * When we set characters to the same characters, the matrix should not be
	 * marked in need of a new version number.
	 */
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
	 * When we set a character that was already at some position, then the its
	 * pPOD version should not be set to {@code null}.
	 */
	public void setWithSameRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.putRow(otu1, row1);
		matrix.setPPodVersionInfo(pPodVersionInfo);
		matrix.putRow(otu1, row1);
		assertNotNull(matrix.getRows().get(matrix.getOTUIdx().get(otu1)));
	}

	/**
	 * When we move a row, its previous position should be automatically null'd.
	 */
	public void moveRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.putRow(otu1, row1);
		matrix.putRow(otu0, row1);
		assertEquals(matrix.getRows().get(matrix.getOTUIdx().get(otu0)), row1);
		assertNull(matrix.getRows().get(matrix.getOTUIdx().get(otu1)));
	}

	/**
	 * Test replacing one row with another.
	 * <ul>
	 * <li>{@link CharacterStateMatrix#setRow(OTU, CharacterStateRow)} should
	 * return the row that had been there</li>
	 * <li>the replaced row should have its matrix (accessed by
	 * {@link CharacterStateRow#getMatrix()}) set to {@code null}</li>
	 * </ul>
	 */
	public void replaceRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.putRow(otu1, row1);
		final CharacterStateRow row2 = rowProvider.get();
		final CharacterStateRow someRow = matrix.putRow(otu1, row2);
		assertEquals(someRow, row1);
		assertEquals(matrix.getRows().get(matrix.getOTUIdx().get(otu1)), row2);
		// assertNull(row1.getMatrix());
	}

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	public void beforeMarshal() {
		nullFillAndSet(matrix.getColumnPPodVersionInfosModifiable(), 2,
				pPodVersionInfoProvider.get().setPPodVersion(3L));
		nullFillAndSet(matrix.getColumnPPodVersionInfosModifiable(), 5,
				pPodVersionInfoProvider.get().setPPodVersion(8L));

		matrix.beforeMarshal(null);
		assertEquals(matrix.getColumnPPodVersions().size(), matrix
				.getColumnPPodVersionInfos().size());
		for (int i = 0; i < matrix.getColumnPPodVersionInfos().size(); i++) {
			if (matrix.getColumnPPodVersionInfos().get(i) == null) {
				assertNull(matrix.getColumnPPodVersions().get(i));
			} else {
				assertEquals(matrix.getColumnPPodVersions().get(i), matrix
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
}
