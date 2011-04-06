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

import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.BROKEN })
public class CellTest {

	/**
	 * Matrix must be ready to have a row with one cell added to it.
	 * 
	 * @param matrix
	 * @param elements
	 */
	@Test
	public void getStatesSmartlyWhenCellHasMultipleElements() {
		final StandardCell cell = new StandardCell();

		final Set<StandardState> states =
				ImmutableSet.of(new StandardState(0), new StandardState(1));

		cell.setPolymorphic(newHashSet(0, 1));
		assertEquals(cell.getStatesSmartly(), states);

		cell.setUncertain(newHashSet(0, 1));
		assertEquals(cell.getStatesSmartly(), states);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void getElementsWhenNoTypeSet() {
		final StandardCell cell = new StandardCell();
		cell.getStatesSmartly();
	}

	@Test
	public void getStatesWhenCellHasNoElements() {
		final StandardCell cell = new StandardCell();
		cell.setUnassigned();

		assertEquals(cell.getStatesSmartly(), Collections.emptyList());

		cell.setInapplicable();
		assertEquals(cell.getStatesSmartly(), Collections.emptyList());
	}

	@Test
	public void getStatesWhenCellHasOneElement() {
		final StandardCell cell = new StandardCell();

		final StandardState state = new StandardState(1);

		cell.setSingle(state.getStateNumber());

		assertEquals(cell.getStatesSmartly(), ImmutableSet.of(state));
	}

	// @Test
	// public void setInNeedOfNewVersion() {
	// final DnaCell cell = new DnaCell();
	// cell.unsetInNeedOfNewVersion();
	//
	// cell.setInNeedOfNewVersion();
	// assertTrue(cell.isInNeedOfNewVersion());
	//
	// final DnaRow row = new DnaRow();
	//
	// final DnaMatrix matrix = new DnaMatrix();
	// // matrix.setColumnsSize(1);
	// final OtuSet otuSet = new OtuSet();
	// otuSet.addDnaMatrix(matrix);
	// otuSet.addOtu(new Otu("otu-0"));
	//
	// matrix.putRow(otuSet.getOtus().get(0), row);
	//
	// row.setCells(ImmutableList.of(cell));
	//
	// cell.unsetInNeedOfNewVersion();
	// row.unsetInNeedOfNewVersion();
	// // matrix.setInNeedOfNewColumnVersion(0);
	//
	// cell.setInNeedOfNewVersion();
	// assertTrue(cell.isInNeedOfNewVersion());
	// assertTrue(row.isInNeedOfNewVersion());
	// // assertNull(matrix.getColumnVersionInfos().get(0));
	//
	// matrix.putRow(otuSet.getOtus().get(0), new DnaRow());
	//
	// cell.unsetInNeedOfNewVersion();
	// row.unsetInNeedOfNewVersion();
	// // matrix.setColumnVersionInfos(new VersionInfo());
	//
	// cell.setInNeedOfNewVersion();
	//
	// assertTrue(cell.isInNeedOfNewVersion());
	// assertTrue(row.isInNeedOfNewVersion());
	// // assertNotNull(matrix.getColumnVersionInfos().get(0));
	//
	// }
	//
	// @Test
	// public void setInapplicable() {
	// final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
	// cell.unsetInNeedOfNewVersion();
	// cell.setInapplicable();
	//
	// assertTrue(cell.isInNeedOfNewVersion());
	// assertEquals(cell.getType(), PPodCellType.INAPPLICABLE);
	// assertEquals(cell.getElements(), Collections.emptySet());
	//
	// cell.unsetInNeedOfNewVersion();
	// cell.setInapplicable();
	//
	// assertFalse(cell.isInNeedOfNewVersion());
	// assertEquals(cell.getType(), PPodCellType.INAPPLICABLE);
	// assertEquals(cell.getElements(), Collections.emptySet());
	// }
	//
	// /**
	// * Straight test of
	// * {@link
	// Cell#setPolymorphicOrUncertain(edu.upenn.cis.ppod.model.Cell.Type, Set)}
	// * . NOTE: we don't check to see if the pPOD version number has been
	// * incremented since that's really not part of the method spec. That
	// * functionality is only guaranteed in the public mutators.
	// */
	// @Test
	// public void setPolymorphicOrUncertain() {
	// final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
	//
	// final Set<PPodDnaNucleotide> nucleotides =
	// EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C);
	//
	// cell.setPolymorphicOrUncertain(
	// PPodCellType.UNCERTAIN,
	// nucleotides);
	//
	// assertSame(PPodCellType.UNCERTAIN, cell.getType());
	// assertEquals(nucleotides, cell.getElements());
	// }
	//
	// @Test(expectedExceptions = IllegalArgumentException.class)
	// public void setPolymorphicOrUncertainWInapplicable() {
	// final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
	// cell.setPolymorphicOrUncertain(
	// PPodCellType.INAPPLICABLE,
	// ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C));
	// }
	//
	// /**
	// * Test to make sure
	// * {@link
	// Cell#setPolymorphicOrUncertain(edu.upenn.cis.ppod.model.Cell.Type, Set)}
	// * works when we call it with the same values a cell already has. NOTE: we
	// * don't check to see if the pPOD version number has been incremented
	// since
	// * that's really not part of the method spec. That functionality is only
	// * guaranteed in the public mutators.
	// */
	// @Test
	// public void setPolymorphicOrUncertainWSameTypeAndNucleotides() {
	// final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
	//
	// final Set<PPodDnaNucleotide> nucleotides =
	// EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C);
	//
	// cell.setPolymorphicOrUncertain(
	// PPodCellType.UNCERTAIN,
	// nucleotides);
	//
	// cell.setPolymorphicOrUncertain(
	// PPodCellType.UNCERTAIN,
	// nucleotides);
	//
	// assertSame(PPodCellType.UNCERTAIN, cell.getType());
	// assertEquals(nucleotides, cell.getElements());
	// }
	//
	// @Test(expectedExceptions = IllegalArgumentException.class)
	// public void setPolymorphicOrUncertainWSingle() {
	// final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
	// cell.setPolymorphicOrUncertain(
	// PPodCellType.SINGLE,
	// ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C));
	// }
	//
	// @Test(expectedExceptions = IllegalArgumentException.class)
	// public void setPolymorphicOrUncertainWTooFewElements() {
	// final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
	// ;
	// cell.setPolymorphicOrUncertain(
	// PPodCellType.POLYMORPHIC,
	// ImmutableSet.of(PPodDnaNucleotide.A));
	// }
	//
	// @Test(expectedExceptions = IllegalArgumentException.class)
	// public void setPolymorphicOrUncertainWUnassigned() {
	// final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
	// cell.setPolymorphicOrUncertain(
	// PPodCellType.UNASSIGNED,
	// ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C));
	// }
	//
	// @Test(expectedExceptions = IllegalArgumentException.class)
	// public void setPosition() {
	// final Cell<?, ?> cell = new DnaCell();
	// cell.setPosition(-1);
	// }
	//
	// @Test
	// public void setUnassigned() {
	// final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
	// cell.unsetInNeedOfNewVersion();
	// cell.setUnassigned();
	//
	// assertTrue(cell.isInNeedOfNewVersion());
	// assertEquals(cell.getType(), PPodCellType.UNASSIGNED);
	// assertEquals(cell.getElements(), Collections.emptySet());
	//
	// cell.unsetInNeedOfNewVersion();
	// cell.setUnassigned();
	//
	// assertFalse(cell.isInNeedOfNewVersion());
	// assertEquals(cell.getType(), PPodCellType.UNASSIGNED);
	// assertEquals(cell.getElements(), Collections.emptySet());
	//
	// }
}