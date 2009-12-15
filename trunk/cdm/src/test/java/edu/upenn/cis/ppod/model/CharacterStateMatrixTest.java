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
	}

	public void setOTUsWSameOTUs() {
		matrix.setOTUSet(otuSet012);

		matrix.setOTUs(otus012);
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

		matrix.setOTUSet(otuSet012);

		matrix.setOTUs(otus012);
		matrix.setRow(0, rowProvider.get());
		final CharacterStateCell cell00 = matrix.getRow(0).addCell(
				cellProvider.get());
		cell00.setTypeAndStates(CharacterStateCell.Type.SINGLE,
				newHashSet(stateFactory.create(0).setCharacter(character)));

		matrix.setRow(1, rowProvider.get());
		final CharacterStateCell cell10 = matrix.getRow(1).addCell(
				cellProvider.get());
		cell10.setTypeAndStates(CharacterStateCell.Type.SINGLE,
				newHashSet(stateFactory.create(1)));

		matrix.setRow(2, rowProvider.get());
		final CharacterStateCell cell20 = matrix.getRow(2).addCell(
				cellProvider.get());
		cell20.setTypeAndStates(CharacterStateCell.Type.SINGLE,
				newHashSet(stateFactory.create(2)));

		final List<CharacterStateRow> originalRows = newArrayList(matrix
				.getRows());

		final List<OTU> otus210 = newArrayList(otu2, otu1, otu0);

		matrix.setOTUs(otus210);

		assertEquals(matrix.getOTUs(), otus210);
		assertEquals(matrix.getRows().size(), originalRows.size());
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRow(0),
				originalRows.get(2));
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRow(1),
				originalRows.get(1));
		ModelAssert.assertEqualsCharacterStateRows(matrix.getRow(2),
				originalRows.get(0));

	}

	public void setOTUsWLessOTUs() {
		matrix.setOTUSet(otuSet012);
		matrix.setOTUs(otus012);

		otuSet012.removeOTU(otu0);

		final List<OTU> otus12 = newArrayList(otu1, otu2);
		matrix.setOTUs(otus12);
		assertEquals(matrix.getOTUs(), otus12);
		assertEquals(matrix.getRows().size(), otus12.size());
	}

	public void setOTUs() {

		matrix.setOTUSet(otuSet012);

		final List<OTU> otus = newArrayList(otu0, otu1, otu2);

		matrix.setOTUs(otus);

		Assert.assertEquals(matrix.getOTUs(), newArrayList(otu0, otu1, otu2));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setWrongOTUs() {
		matrix.setOTUSet(otuSet012);
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
		final Character gotCharacter1 = matrix.getCharacter(1);
		Assert.assertEquals(gotCharacter1, character);

		final int characterIdx = matrix.getCharacterIdx(gotCharacter1);
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
		assertEquals(matrix.getCharacter(3), character1);

		matrix.setCharacter(18, character1);
		assertNull(matrix.getCharacter(3));
		assertEquals(matrix.getCharacter(18), character1);
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
		assertEquals(matrix.getCharacter(3), character2);
		assertEquals(someCharacter, character1);
		assertNull(matrix.getCharacterIdx(character1));
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
	 * Straight {@link CharacterStateMatrix#removeLastCharacter()} test.
	 */
// public void removeLastCharacter() {
// final Character character15 = characterProvider.get();
// matrix.setCharacter(15, character15);
// matrix.removeLastCharacter();
//
// Assert.assertEquals(matrix.getCharacters().size(), 15);
// Assert.assertEquals(matrix.getColumnPPodVersionInfos().size(), 15);
// }

	/**
	 * When we set a character that was already at some position, then the its
	 * pPOD version should not be set to {@code null}.
	 */
	public void setWithSameRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.setRow(4, row1);
		matrix.setPPodVersionInfo(pPodVersionInfo);
		matrix.setRow(4, row1);
		assertNotNull(matrix.getRow(4));
	}

	/**
	 * Setting a row should pad with all lesser unassigned rows with {@code
	 * null}.
	 */
	public void setRowCreatesPadding() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.setRow(18, row1);
		for (int i = 0; i < 18; i++) {
			assertNull(matrix.getRow(i));
		}
	}

	/**
	 * When we move a row, its previous position should be automatically null'd.
	 */
	public void moveRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.setRow(3, row1);
		matrix.setRow(0, row1);
		assertEquals(matrix.getRow(0), row1);
		assertNull(matrix.getRow(3));
	}

	/**
	 * Test replacing one row with another.
	 * <ul>
	 * <li>{@link CharacterStateMatrix#setRow(int, CharacterStateRow)} should
	 * return the row that had been there</li>
	 * <li>the replaced row should have its matrix (accessed by
	 * {@link CharacterStateRow#getMatrix()}) set to {@code null}</li>
	 * </ul>
	 */
	public void replaceRow() {
		final CharacterStateRow row1 = rowProvider.get();
		matrix.setRow(5, row1);
		final CharacterStateRow row2 = rowProvider.get();
		final CharacterStateRow someRow = matrix.setRow(5, row2);
		assertEquals(someRow, row1);
		assertEquals(matrix.getRow(5), row2);
		assertNull(row1.getMatrix());
	}

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	public void beforeMarshall() {
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
