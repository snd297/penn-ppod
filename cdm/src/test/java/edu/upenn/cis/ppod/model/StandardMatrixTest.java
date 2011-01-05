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

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IVersionInfo;

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
		otuSet012.setOTUs(newArrayList(otu0, otu1, otu2));

		otuSet012.addStandardMatrix(matrix);

	}

	@Test
	public void setOTUsWReorderedOTUs() {

		final StandardCharacter standardCharacter = new StandardCharacter();
		standardCharacter.setLabel("testLabel");
		matrix.setCharacters(newArrayList(standardCharacter));

		matrix.putRow(otu0, new StandardRow());
		final StandardCell cell00 = new StandardCell();
		matrix.getRows().get(otu0).setCells(
				Arrays.asList(new StandardCell[] { cell00 }));
		standardCharacter.addState(new StandardState(0));

		final StandardState state = standardCharacter.getState(0);
		assertNotNull(state);

		cell00.setSingleWithStateNo(state.getStateNumber());

		matrix.putRow(otu1, new StandardRow());
		final StandardCell cell10 = new StandardCell();
		matrix.getRows().get(otu1).setCells(
				Arrays.asList(new StandardCell[] { cell10 }));

		final StandardState state1 = new StandardState(1);
		standardCharacter.addState(state1);
		cell10.setSingleWithStateNo(state1.getStateNumber());

		matrix.putRow(otu2, new StandardRow());
		final StandardCell cell20 = new StandardCell();
		matrix.getRows().get(otu2).setCells(
				Arrays.asList(new StandardCell[] { cell20 }));

		final StandardState state0 = new StandardState(0);
		standardCharacter.addState(state0);
		cell20.setSingleWithStateNo(state0.getStateNumber());

		final int originalRowsSize = matrix.getRows().size();

		final ImmutableList<Otu> otus210 = ImmutableList.of(otu2, otu1, otu0);
		matrix.getParent().setOTUs(otus210);

		assertEquals(matrix.getParent().getOtus(), otus210);
		assertEquals(matrix.getRows().size(), originalRowsSize);

	}

	@Test
	public void setOTUsWithLessOTUs() {

		otuSet012.setOTUs(newArrayList(otu1, otu2));

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
		final ImmutableList<StandardCharacter> standardCharacters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		standardCharacters.get(0).setLabel("character-0");
		standardCharacters.get(1).setLabel("character-1");
		standardCharacters.get(2).setLabel("character-2");

		final VersionInfo pPodVersionInfo0 = new VersionInfo();
		final VersionInfo pPodVersionInfo1 = new VersionInfo();
		final VersionInfo pPodVersionInfo2 = new VersionInfo();

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
		final ImmutableList<StandardCharacter> characters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		characters.get(0).setLabel("character-0");
		characters.get(1).setLabel("character-1");
		characters.get(2).setLabel("character-2");

		final VersionInfo pPodVersionInfo0 = new VersionInfo();
		final VersionInfo pPodVersionInfo1 = new VersionInfo();
		final VersionInfo pPodVersionInfo2 = new VersionInfo();

		matrix.setCharacters(characters);

		matrix.getColumnVersionInfosModifiable().set(0, pPodVersionInfo0);
		matrix.getColumnVersionInfosModifiable().set(1, pPodVersionInfo1);
		matrix.getColumnVersionInfosModifiable().set(2, pPodVersionInfo2);

		final ImmutableList<StandardCharacter> characters2 =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		characters.get(0).setLabel("character-2-0");
		characters.get(1).setLabel("character-2-1");
		characters.get(2).setLabel("character-2-2");

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
		final ImmutableList<StandardCharacter> characters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		characters.get(0).setLabel("character-0");
		characters.get(1).setLabel("character-1");
		characters.get(2).setLabel("character-2");

		matrix.setCharacters(characters);

		matrix.unsetInNeedOfNewVersion();

		matrix.setCharacters(characters);

		assertFalse(matrix.isInNeedOfNewVersion());

	}

	/**
	 * When we set a character that was already at some position, then it should
	 * not be marked as in need of a new pPOD version info.
	 */
	@Test
	public void setWithSameRow() {
		final StandardRow row1 = new StandardRow();
		matrix.putRow(otu1, row1);
		matrix.setVersionInfo(new VersionInfo());
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
		final StandardRow row1 = new StandardRow();
		matrix.putRow(otu1, row1);
		final StandardRow row2 = new StandardRow();
		final StandardRow someRow = matrix.putRow(otu1, row2);
		assertEquals(someRow, row1);
		assertNull(row1.getParent());
	}

	@Test
	public void beforeMarshal() {

		final ImmutableList<StandardCharacter> characters =
				ImmutableList.of(
						new StandardCharacter(),
						new StandardCharacter(),
						new StandardCharacter());

		characters.get(0).setLabel("character-0");
		characters.get(1).setLabel("character-1");
		characters.get(2).setLabel("character-2");

		nullFillAndSet(
				matrix.getColumnVersionInfosModifiable(),
				0,
				new VersionInfo().setVersion(3L));
		nullFillAndSet(
				matrix.getColumnVersionInfosModifiable(),
				2,
				new VersionInfo().setVersion(8L));

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
		final StandardCharacter character0 = new StandardCharacter();
		character0.setLabel("character0");
		final StandardCharacter character1 = new StandardCharacter();
		character1.setLabel("character1");

		final ImmutableList<StandardCharacter> standardCharacters = ImmutableList
				.of(
						character0, character1, character0);
		matrix.setCharacters(standardCharacters);
	}

	public void setColumnPPodVersionInfos() {
		final VersionInfo versionInfo = new VersionInfo();

		matrix.setColumnVersionInfos(versionInfo);
		for (final IVersionInfo columnPPodVersionInfo : matrix
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

	public void removeColumn() {

	}

	// void populateMatrix(final StandardMatrix matrix) {
	// final IOTUSet otuSet = otuSetProvider.get();
	//
	// otuSet.setLabel("otu-set");
	// otuSet.setVersion(78732598732L);
	// otuSet.setPPodId();
	//
	// final TestOTU otu0 = otuProvider.get();
	// otuSet.addOTU(otu0);
	// otu0.setLabel("otu-0");
	// otu0.setVersion(343L);
	// otu0.setPPodId();
	//
	// final TestOTU otu1 = otuProvider.get();
	// otuSet.addOTU(otu1);
	// otu1.setLabel("otu-1");
	// otu1.setVersion(343L);
	// otu1.setPPodId();
	//
	// final TestOTU otu2 = otuProvider.get();
	// otuSet.addOTU(otu2);
	// otu2.setLabel("otu-2");
	// otu2.setVersion(343L);
	// otu2.setPPodId();
	//
	// final TestStandardCharacter character0 = standardCharacterProvider
	// .get();
	// character0.setLabel("character-0");
	// character0.setVersion(45245L);
	// character0.setPPodId();
	//
	// final StandardState state00 = standardStateFactory.create(0);
	// character0.addState(state00);
	// state00.setLabel("state-00");
	// final StandardState state01 = standardStateFactory.create(1);
	// character0.addState(state01);
	// state01.setLabel("state-01");
	// final StandardState state02 = standardStateFactory.create(2);
	// character0.addState(state02);
	// state02.setLabel("state-02");
	//
	// final TestStandardCharacter character1 = standardCharacterProvider
	// .get();
	// character1.setLabel("character-1");
	// character1.setVersion(45245L);
	// character1.setPPodId();
	//
	// final StandardState state10 = standardStateFactory.create(0);
	// character1.addState(state10);
	// state10.setLabel("state-10");
	// final StandardState state11 = standardStateFactory.create(1);
	// character1.addState(state11);
	// state11.setLabel("state-11");
	// final StandardState state12 = standardStateFactory.create(2);
	// character1.addState(state12);
	// state12.setLabel("state-12");
	//
	// final TestStandardCharacter character2 = standardCharacterProvider
	// .get();
	// character2.setLabel("character-2");
	// character2.setVersion(45245L);
	// character2.setPPodId();
	//
	// final StandardState state20 = standardStateFactory.create(0);
	// character2.addState(state20);
	// state20.setLabel("state-20");
	// final StandardState state21 = standardStateFactory.create(1);
	// character2.addState(state21);
	// state21.setLabel("state-21");
	// final StandardState state22 = standardStateFactory.create(2);
	// character2.addState(state22);
	// state22.setLabel("state-22");
	//
	// final TestStandardMatrix matrix = standardMatrixProvider.get();
	// matrix.setLabel("matrix");
	// otuSet.addStandardMatrix(matrix);
	// matrix.setVersion(8735873L);
	// matrix.setPPodId();
	// matrix.setCharacters(ImmutableList.of(character0, character1,
	// character2));
	// matrix.setColumnVersions(ImmutableList.of(58L, 34783L, 325L));
	//
	// final TestStandardRow row0 = standardRowProvider.get();
	// matrix.putRow(otu0, row0);
	// row0.setVersion(4759879L);
	//
	// final TestStandardCell cell00 = standardCellProvider.get();
	// cell00.setVersion(353L);
	// final TestStandardCell cell01 = standardCellProvider.get();
	// cell01.setVersion(353L);
	// final TestStandardCell cell02 = standardCellProvider.get();
	// cell02.setVersion(353L);
	//
	// row0.setCells(ImmutableList.of(cell00, cell01, cell02));
	//
	// cell00.setUnassigned();
	// cell01.setSingleWithStateNo(0);
	// cell02.setPolymorphicWithStateNos(ImmutableSet.of(0, 2));
	//
	// final TestStandardRow row1 = standardRowProvider.get();
	// matrix.putRow(otu1, row1);
	// row1.setVersion(4759879L);
	//
	// final TestStandardCell cell10 = standardCellProvider.get();
	// cell10.setVersion(353L);
	// final TestStandardCell cell11 = standardCellProvider.get();
	// cell11.setVersion(353L);
	// final TestStandardCell cell12 = standardCellProvider.get();
	// cell12.setVersion(353L);
	//
	// row1.setCells(ImmutableList.of(cell10, cell11, cell12));
	//
	// cell10.setUncertainWithStateNos(ImmutableSet.of(1, 2));
	// cell11.setInapplicable();
	// cell12.setSingleWithStateNo(1);
	//
	// final TestStandardRow row2 = standardRowProvider.get();
	// matrix.putRow(otu2, row2);
	// row2.setVersion(4759879L);
	//
	// final TestStandardCell cell20 = standardCellProvider.get();
	// cell20.setVersion(353L);
	// final TestStandardCell cell21 = standardCellProvider.get();
	// cell21.setVersion(353L);
	// final TestStandardCell cell22 = standardCellProvider.get();
	// cell22.setVersion(353L);
	//
	// row2.setCells(ImmutableList.of(cell20, cell21, cell22));
	//
	// cell20.setPolymorphicWithStateNos(ImmutableSet.of(0, 1));
	// cell21.setInapplicable();
	// cell22.setSingleWithStateNo(2);
	//
	// }
}
