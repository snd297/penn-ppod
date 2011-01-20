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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodDnaNucleotide;

@Test(groups = { TestGroupDefs.FAST })
public class CellTest {

	/**
	 * Matrix must be ready to have a row with one cell added to it.
	 * 
	 * @param matrix
	 * @param elements
	 */
	@Test
	public void getElementsWhenCellHasMultipleElements() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();

		final Set<PPodDnaNucleotide> elements =
				ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.T);

		cell.setPolymorphicOrUncertain(PPodCellType.POLYMORPHIC, elements);
		assertEquals(cell.getElements(), elements);

		cell.setType(PPodCellType.UNCERTAIN);
		assertEquals(cell.getElements(), elements);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void getElementsWhenNoTypeSet() {
		final Cell<?, ?> cell = new DnaCell();
		cell.getElements();
	}

	@Test
	public void getStatesWhenCellHasNoElements() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
		cell.setType(PPodCellType.UNASSIGNED);
		cell.setElement(null);
		cell.setElements(null);
		assertEquals(cell.getElements(), Collections.emptyList());

		cell.setType(PPodCellType.INAPPLICABLE);
		assertEquals(cell.getElements(), Collections.emptyList());
	}

	@Test
	public void getStatesWhenCellHasOneElement() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();

		final PPodDnaNucleotide nucleotide = PPodDnaNucleotide.C;

		cell.setElement(nucleotide);
		cell.setType(PPodCellType.SINGLE);
		assertEquals(cell.getElements(), ImmutableSet.of(nucleotide));
	}

	@Test
	public void setInNeedOfNewVersion() {
		final DnaCell cell = new DnaCell();
		cell.unsetInNeedOfNewVersion();

		cell.setInNeedOfNewVersion();
		assertTrue(cell.isInNeedOfNewVersion());

		final DnaRow row = new DnaRow();

		final DnaMatrix matrix = new DnaMatrix();
		matrix.setColumnsSize(1);
		final OtuSet otuSet = new OtuSet();
		otuSet.addDnaMatrix(matrix);
		otuSet.addOtu(new Otu("otu-0"));

		matrix.putRow(otuSet.getOtus().get(0), row);

		row.setCells(ImmutableList.of(cell));

		cell.unsetInNeedOfNewVersion();
		row.unsetInNeedOfNewVersion();
		matrix.setInNeedOfNewColumnVersion(0);

		cell.setInNeedOfNewVersion();
		assertTrue(cell.isInNeedOfNewVersion());
		assertTrue(row.isInNeedOfNewVersion());
		assertNull(matrix.getColumnVersionInfos().get(0));

		matrix.putRow(otuSet.getOtus().get(0), new DnaRow());

		cell.unsetInNeedOfNewVersion();
		row.unsetInNeedOfNewVersion();
		matrix.setColumnVersionInfos(new VersionInfo());

		cell.setInNeedOfNewVersion();

		assertTrue(cell.isInNeedOfNewVersion());
		assertTrue(row.isInNeedOfNewVersion());
		assertNotNull(matrix.getColumnVersionInfos().get(0));

	}

	@Test
	public void setInapplicable() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
		cell.unsetInNeedOfNewVersion();
		cell.setInapplicable();

		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), PPodCellType.INAPPLICABLE);
		assertEquals(cell.getElements(), Collections.emptySet());

		cell.unsetInNeedOfNewVersion();
		cell.setInapplicable();

		assertFalse(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), PPodCellType.INAPPLICABLE);
		assertEquals(cell.getElements(), Collections.emptySet());
	}

	/**
	 * Straight test of
	 * {@link Cell#setPolymorphicOrUncertain(edu.upenn.cis.ppod.model.Cell.Type, Set)}
	 * . NOTE: we don't check to see if the pPOD version number has been
	 * incremented since that's really not part of the method spec. That
	 * functionality is only guaranteed in the public mutators.
	 */
	@Test
	public void setPolymorphicOrUncertain() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();

		final Set<PPodDnaNucleotide> nucleotides =
				EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C);

		cell.setPolymorphicOrUncertain(
				PPodCellType.UNCERTAIN,
				nucleotides);

		assertSame(PPodCellType.UNCERTAIN, cell.getType());
		assertEquals(nucleotides, cell.getElements());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicOrUncertainWInapplicable() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
		cell.setPolymorphicOrUncertain(
				PPodCellType.INAPPLICABLE,
				ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C));
	}

	/**
	 * Test to make sure
	 * {@link Cell#setPolymorphicOrUncertain(edu.upenn.cis.ppod.model.Cell.Type, Set)}
	 * works when we call it with the same values a cell already has. NOTE: we
	 * don't check to see if the pPOD version number has been incremented since
	 * that's really not part of the method spec. That functionality is only
	 * guaranteed in the public mutators.
	 */
	@Test
	public void setPolymorphicOrUncertainWSameTypeAndNucleotides() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();

		final Set<PPodDnaNucleotide> nucleotides =
				EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C);

		cell.setPolymorphicOrUncertain(
				PPodCellType.UNCERTAIN,
				nucleotides);

		cell.setPolymorphicOrUncertain(
				PPodCellType.UNCERTAIN,
				nucleotides);

		assertSame(PPodCellType.UNCERTAIN, cell.getType());
		assertEquals(nucleotides, cell.getElements());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicOrUncertainWSingle() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
		cell.setPolymorphicOrUncertain(
				PPodCellType.SINGLE,
				ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicOrUncertainWTooFewElements() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
		;
		cell.setPolymorphicOrUncertain(
				PPodCellType.POLYMORPHIC,
				ImmutableSet.of(PPodDnaNucleotide.A));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicOrUncertainWUnassigned() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
		cell.setPolymorphicOrUncertain(
				PPodCellType.UNASSIGNED,
				ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPosition() {
		final Cell<?, ?> cell = new DnaCell();
		cell.setPosition(-1);
	}

	@Test
	public void setUnassigned() {
		final Cell<PPodDnaNucleotide, ?> cell = new DnaCell();
		cell.unsetInNeedOfNewVersion();
		cell.setUnassigned();

		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), PPodCellType.UNASSIGNED);
		assertEquals(cell.getElements(), Collections.emptySet());

		cell.unsetInNeedOfNewVersion();
		cell.setUnassigned();

		assertFalse(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), PPodCellType.UNASSIGNED);
		assertEquals(cell.getElements(), Collections.emptySet());

	}
}