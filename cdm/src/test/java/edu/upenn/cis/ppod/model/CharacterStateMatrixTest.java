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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
		otuSet012.addOTU(otu0);
		otuSet012.addOTU(otu1);
		otuSet012.addOTU(otu2);

		matrix.setOTUSet(otuSet012);

		matrix.setOTUs(otus012);
	}

	/**
	 * When we set with the same OTU's, the pPOD version number of the matrix
	 * should not change.
	 */
	public void setOTUsWSameOTUs() {
		matrix.setPPodVersionInfo(pPodVersionInfo);

		matrix.setOTUs(otus012);

		// Since they were the same, the version should not have been reset to
		// null
		assertNotNull(matrix.getPPodVersionInfo());
	}

	public void setOTUsWReorderedOTUs() {

		final Character character = characterProvider.get().setLabel(
				"testLabel");
		matrix.setCharacter(0, character);

		matrix.setRow(otu0, rowProvider.get());
		final CharacterStateCell cell00 = cellProvider.get();
		matrix.getRows().get(0).setCells(newArrayList(cell00));
		cell00.setSingleState(stateFactory.create(0).setCharacter(character));

		matrix.setRow(otu1, rowProvider.get());
		final CharacterStateCell cell10 = cellProvider.get();
		matrix.getRows().get(1).setCells(newArrayList(cell10));
		cell10.setSingleState(stateFactory.create(1));

		matrix.setRow(otu2, rowProvider.get());
		final CharacterStateCell cell20 = cellProvider.get();
		matrix.getRows().get(1).setCells(newArrayList(cell20));
		cell20.setSingleState(stateFactory.create(0));

		final List<CharacterStateRow> originalRows = newArrayList(matrix
				.getRows());

		final List<OTU> otus210 = newArrayList(otu2, otu1, otu0);

		matrix.setOTUs(otus210);

		assertEquals(matrix.getOTUs(), otus210);
		assertEquals(matrix.getRows().size(), originalRows.size());
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRows().get(
				matrix.getOTUIdx().get(otu2)), originalRows.get(2));
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRows().get(
				matrix.getOTUIdx().get(otu1)), originalRows.get(1));
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRows().get(
				matrix.getOTUIdx().get(otu0)), originalRows.get(0));

	}

	public void setOTUsWLessOTUs() {

		otuSet012.removeOTU(otu0);

		final List<OTU> otus12 = newArrayList(otu1, otu2);
		matrix.setOTUs(otus12);
		assertEquals(matrix.getOTUs(), otus12);
		assertEquals(matrix.getRows().size(), otus12.size());
	}

	public void setOTUs() {
		Assert.assertEquals(matrix.getOTUs(), newArrayList(otu0, otu1, otu2));

		for (int i = 0; i < matrix.getRows().size(); i++) {
			assertNull(matrix.getRows().get(i));
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setWrongOTUs() {
		matrix.setOTUs(newArrayList(otu0, otu2));
	}

	/**
	 * Straight {@link CharacterStateMatrix#setCharacter(int, Character)} tests.
	 * <p>
	 * Make sure it pads with nulls.
	 */
	public void setCharacter() {
		final Character character = characterProvider.get().setLabel(
				"testLabel");
		matrix.setCharacter(1, character);
		final Character gotCharacter1 = matrix.getCharacters().get(1);
		Assert.assertEquals(gotCharacter1, character);

		final int characterIdx = matrix.getCharacterIdx().get(gotCharacter1);
		Assert.assertEquals(characterIdx, 1);

		matrix.setCharacter(15, new Character().setLabel("phyloChar3"));

		for (final Character thisPhyloChar : matrix.getCharacters().subList(2,
				15)) {
			assertNull(thisPhyloChar);
		}

	}

	/**
	 * Make sure when we move a character it gets moved and its old position is
	 * null'd.
	 */
	public void moveCharacter() {
		final Character character1 = characterProvider.get().setLabel(
				"character1");
		matrix.setCharacter(3, character1);
		assertEquals(matrix.getCharacters().get(3), character1);

		matrix.setCharacter(18, character1);
		assertNull(matrix.getCharacters().get(3));
		assertEquals(matrix.getCharacters().get(18), character1);
	}

	/**
	 * Test replacing a character.
	 */
	public void replaceCharacter() {
		final Character character1 = characterProvider.get().setLabel(
				"character1");
		matrix.setCharacter(3, character1);
		final Character character2 = characterProvider.get().setLabel(
				"character2");
		final Character someCharacter = matrix.setCharacter(3, character2);
		assertEquals(matrix.getCharacters().get(3), character2);
		assertEquals(someCharacter, character1);
		assertNull(matrix.getCharacterIdx().get(character1));
	}

	/**
	 * When we set a character that was already at a position, then the its pPOD
	 * version should not be set to {@code null}.
	 */
	public void setCharacterWithItself() {
		final Character character1 = characterProvider.get();
		matrix.setCharacter(3, character1);
		matrix.setPPodVersionInfo(pPodVersionInfo);
		matrix.setCharacter(3, character1);
		assertNotNull(matrix.getPPodVersionInfo());
	}

	/**
	 * When we set a character that was already at some position, then the its
	 * pPOD version should not be set to {@code null}.
	 */
	public void setWithSameRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.setRow(otu1, row1);
		matrix.setPPodVersionInfo(pPodVersionInfo);
		matrix.setRow(otu1, row1);
		assertNotNull(matrix.getRows().get(matrix.getOTUIdx().get(otu1)));
	}

	/**
	 * When we move a row, its previous position should be automatically null'd.
	 */
	public void moveRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.setRow(otu1, row1);
		matrix.setRow(otu0, row1);
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
		matrix.setRow(otu1, row1);
		final CharacterStateRow row2 = rowProvider.get();
		final CharacterStateRow someRow = matrix.setRow(otu1, row2);
		assertEquals(someRow, row1);
		assertEquals(matrix.getRows().get(matrix.getOTUIdx().get(otu1)), row2);
		// assertNull(row1.getMatrix());
	}

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	public void beforeMarshal() {
		nullFillAndSet(matrix.getColumnPPodVersionInfosMutable(), 2,
				pPodVersionInfoProvider.get().setPPodVersion(3L));
		nullFillAndSet(matrix.getColumnPPodVersionInfosMutable(), 5,
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
}
