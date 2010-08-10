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
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IStandardRow;

/**
 * Logic tests of {@link StandardMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class StandardMatrixTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private VersionInfo versionInfo;

	@Inject
	private Provider<StandardMatrix> matrixProvider;

	@Inject
	private Provider<StandardCharacter> characterProvider;

	@Inject
	private Provider<StandardRow> rowProvider;

	@Inject
	private Provider<StandardCell> cellProvider;

	@Inject
	private StandardState.IFactory stateFactory;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	private OTUSet otuSet012;
	private OTU otu0;
	private OTU otu1;
	private OTU otu2;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	private StandardMatrix matrix;

	@BeforeMethod
	public void beforeMethod() {
		matrix = matrixProvider.get();

		otu0 = otuProvider.get().setLabel("otu0");
		otu1 = otuProvider.get().setLabel("otu1");
		otu2 = otuProvider.get().setLabel("otu2");

		otuSet012 = otuSetProvider.get();
		otuSet012.setOTUs(newArrayList(otu0, otu1, otu2));

		otuSet012.addStandardMatrix(matrix);

	}

	@Test
	public void setOTUsWReorderedOTUs() {

		final StandardCharacter standardCharacter = characterProvider.get()
				.setLabel(
						"testLabel");
		matrix.setCharacters(newArrayList(standardCharacter));

		matrix.putRow(otu0, rowProvider.get());
		final StandardCell cell00 = cellProvider.get();
		matrix.getRows().get(otu0).setCells(
				Arrays.asList(new StandardCell[] { cell00 }));
		standardCharacter.addState(stateFactory.create(0));

		final IStandardState state = standardCharacter.getState(0);
		assertNotNull(state);

		cell00.setSingleElement(state);

		matrix.putRow(otu1, rowProvider.get());
		final StandardCell cell10 = cellProvider.get();
		matrix.getRows().get(otu1).setCells(
				Arrays.asList(new StandardCell[] { cell10 }));

		final IStandardState state1 = stateFactory.create(1);
		standardCharacter.addState(state1);
		cell10.setSingleElement(state1);

		matrix.putRow(otu2, rowProvider.get());
		final StandardCell cell20 = cellProvider.get();
		matrix.getRows().get(otu2).setCells(
				Arrays.asList(new StandardCell[] { cell20 }));

		final IStandardState state0 = stateFactory.create(0);
		standardCharacter.addState(state0);
		cell20.setSingleElement(state0);

		final int originalRowsSize = matrix.getRows().size();

		final ImmutableList<OTU> otus210 = ImmutableList.of(otu2, otu1, otu0);
		matrix.getParent().setOTUs(otus210);

		assertEquals(matrix.getParent().getOTUs(), otus210);
		assertEquals(matrix.getRows().size(), originalRowsSize);

	}

	@Test
	public void setOTUsWithLessOTUs() {

		otuSet012.setOTUs(newArrayList(otu1, otu2));

		final ImmutableList<OTU> otus12 = ImmutableList.of(otu1, otu2);

		assertEquals(matrix.getParent().getOTUs(), otus12);
		assertEquals(matrix.getRows().size(), otus12.size());
	}

	/**
	 * Straight {@link CharacterStateMatrix#setCharacters(List)} test.
	 */
	@Test
	public void setCharacters() {

		final ImmutableList<StandardCharacter> standardCharacters = ImmutableList
				.of(
						characterProvider.get().setLabel("character0"),
						characterProvider.get().setLabel("character1"),
						characterProvider.get().setLabel("character2"));

		matrix.setCharacters(standardCharacters);

		assertNotSame(matrix.getCharactersModifiable(), standardCharacters);
		Assert.assertEquals(matrix.getCharactersModifiable(),
				standardCharacters);

		// Assert.assertEquals(matrix.getCharactersToPositions()
		// .get(standardCharacters.get(0)),
		// Integer.valueOf(0));
		// Assert.assertEquals(matrix.getCharactersToPositions()
		// .get(standardCharacters.get(1)),
		// Integer.valueOf(1));
		// Assert.assertEquals(matrix.getCharactersToPositions()
		// .get(standardCharacters.get(2)),
		// Integer.valueOf(2));
	}

	/**
	 * Make sure when we move a character it the character->position map is
	 * updated.
	 */
	@Test
	public void moveCharacter() {

		final ImmutableList<StandardCharacter> standardCharacters = ImmutableList
				.of(
						characterProvider.get().setLabel("character0"),
						characterProvider.get().setLabel("character1"),
						characterProvider.get().setLabel("character2"));

		final VersionInfo pPodVersionInfo0 = pPodVersionInfoProvider.get();
		final VersionInfo pPodVersionInfo1 = pPodVersionInfoProvider.get();
		final VersionInfo pPodVersionInfo2 = pPodVersionInfoProvider.get();

		matrix.setCharacters(standardCharacters);

		matrix.getColumnVersionInfosModifiable().set(0, pPodVersionInfo0);
		matrix.getColumnVersionInfosModifiable().set(1, pPodVersionInfo1);
		matrix.getColumnVersionInfosModifiable().set(2, pPodVersionInfo2);

		final ImmutableList<StandardCharacter> shuffledCharacters = ImmutableList
				.of(
						standardCharacters.get(1), standardCharacters.get(2),
						standardCharacters.get(0));

		matrix.setCharacters(shuffledCharacters);

		assertNotSame(matrix.getCharacters(), shuffledCharacters);
		assertEquals(matrix.getCharacters(), shuffledCharacters);
	}

	/**
	 * Test replacing all of the characters.
	 */
	@Test
	public void replaceCharacters() {
		final ImmutableList<StandardCharacter> standardCharacters = ImmutableList
				.of(
						characterProvider.get().setLabel("character0"),
						characterProvider.get().setLabel("character1"),
						characterProvider.get().setLabel("character2"));

		final VersionInfo pPodVersionInfo0 = pPodVersionInfoProvider.get();
		final VersionInfo pPodVersionInfo1 = pPodVersionInfoProvider.get();
		final VersionInfo pPodVersionInfo2 = pPodVersionInfoProvider.get();

		matrix.setCharacters(standardCharacters);

		matrix.getColumnVersionInfosModifiable().set(0, pPodVersionInfo0);
		matrix.getColumnVersionInfosModifiable().set(1, pPodVersionInfo1);
		matrix.getColumnVersionInfosModifiable().set(2, pPodVersionInfo2);

		final ImmutableList<StandardCharacter> characters2 =
				ImmutableList.of(
						characterProvider.get().setLabel("character2-0"),
						characterProvider.get().setLabel("character2-1"),
						characterProvider.get().setLabel("character2-2"));

		matrix.unsetInNeedOfNewVersion();

		matrix.setCharacters(characters2);
		assertTrue(matrix.isInNeedOfNewVersion());
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
		final ImmutableList<StandardCharacter> standardCharacters = ImmutableList
				.of(
						characterProvider.get().setLabel("character0"),
						characterProvider.get().setLabel("character1"),
						characterProvider.get().setLabel("character2"));

		matrix.setCharacters(standardCharacters);

		matrix.unsetInNeedOfNewVersion();

		matrix.setCharacters(standardCharacters);

		assertFalse(matrix.isInNeedOfNewVersion());

	}

	/**
	 * When we set a character that was already at some position, then it should
	 * not be marked as in need of a new pPOD version info.
	 */
	@Test
	public void setWithSameRow() {
		final IStandardRow row1 = rowProvider.get();
		matrix.putRow(otu1, row1);
		matrix.setVersionInfo(versionInfo);
		matrix.putRow(otu1, row1);
		assertFalse(matrix.isInNeedOfNewVersion());
	}

	// /**
	// * When we move a row, its previous position should be automatically
	// null'd.
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
		final StandardRow row1 = rowProvider.get();
		matrix.putRow(otu1, row1);
		final IStandardRow row2 = rowProvider.get();
		final IStandardRow someRow = matrix.putRow(otu1, row2);
		assertEquals(someRow, row1);
		assertNull(row1.getParent());
	}

	@Inject
	private Provider<VersionInfo> pPodVersionInfoProvider;

	@Test
	public void beforeMarshal() {

		matrix.setCharacters(ImmutableList.of(
				characterProvider.get().setLabel("character0"),
				characterProvider.get().setLabel("character1"),
				characterProvider.get().setLabel("character2")));
		nullFillAndSet(
				matrix.getColumnVersionInfosModifiable(),
				0,
				pPodVersionInfoProvider.get().setVersion(3L));
		nullFillAndSet(
				matrix.getColumnVersionInfosModifiable(),
				2,
				pPodVersionInfoProvider.get().setVersion(8L));

		matrix.beforeMarshal(null);
		assertEquals(matrix.getColumnVersions().size(),
				matrix.getColumnVersionInfos().size());
		for (int i = 0; i < matrix.getColumnVersionInfos().size(); i++) {
			if (matrix.getColumnVersionInfos().get(i) == null) {
				assertNull(matrix.getColumnVersions().get(i));
			} else {
				assertEquals(matrix.getColumnVersions().get(i),
						matrix
								.getColumnVersionInfos().get(i)
								.getVersion());
			}
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setCharactersW2EqualsCharacters() {
		final StandardCharacter character0 = characterProvider.get().setLabel(
				"character0");
		final StandardCharacter character1 = characterProvider.get().setLabel(
				"character1");

		final ImmutableList<StandardCharacter> standardCharacters = ImmutableList
				.of(
						character0, character1, character0);
		matrix.setCharacters(standardCharacters);
	}

	public void setColumnPPodVersionInfos() {
		final VersionInfo versionInfo = pPodVersionInfoProvider.get();

		matrix.setColumnVersionInfos(versionInfo);
		for (final VersionInfo columnPPodVersionInfo : matrix
				.getColumnVersionInfos()) {
			assertSame(columnPPodVersionInfo, versionInfo);
		}
	}

	public void setDescription() {
		matrix.unsetInNeedOfNewVersion();
		final String description = "DESCRIPTION";
		matrix.setDescription(description);
		assertEquals(matrix.getDescription(), description);
		assertTrue(matrix.isInNeedOfNewVersion());

		matrix.unsetInNeedOfNewVersion();
		matrix.setDescription(description);
		assertFalse(matrix.isInNeedOfNewVersion());

		matrix.unsetInNeedOfNewVersion();
		matrix.setDescription(null);
		assertNull(matrix.getDescription());
		assertTrue(matrix.isInNeedOfNewVersion());

		matrix.unsetInNeedOfNewVersion();
		matrix.setDescription(null);
		assertNull(matrix.getDescription());
		assertFalse(matrix.isInNeedOfNewVersion());
	}
}
