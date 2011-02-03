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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodDnaNucleotide;

/**
 * {@link MolecularCell} testing.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class MolecularCellTest {

	@Test
	public void setPolymorphicElements() {
		final CellWithCase<PPodDnaNucleotide, ?> cell = new DnaCell();

		cell.unsetInNeedOfNewVersion();

		final Set<PPodDnaNucleotide> nucleotides =
				ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.T);

		cell.setPolymorphicElements(
						nucleotides,
						false);
		assertTrue(cell.isInNeedOfNewVersion());

		assertEquals(cell.getElements(), nucleotides);
		assertFalse(cell.getLowerCase());

		cell.unsetInNeedOfNewVersion();

		cell.setPolymorphicElements(
						nucleotides,
						false);
		assertFalse(cell.isInNeedOfNewVersion());
		assertEquals(cell.getElements(), nucleotides);
		assertFalse(cell.getLowerCase());

		cell.unsetInNeedOfNewVersion();

		final Set<PPodDnaNucleotide> nucleotides2 =
				ImmutableSet.of(PPodDnaNucleotide.T, PPodDnaNucleotide.G);

		cell.setPolymorphicElements(
						nucleotides2,
						false);
		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getElements(), nucleotides2);
		assertFalse(cell.getLowerCase());

		cell.unsetInNeedOfNewVersion();

		cell.setPolymorphicElements(
						nucleotides2,
						true);
		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getElements(), nucleotides2);
		assertTrue(cell.getLowerCase());
	}

	@Test
	public void setSingleElement() {
		final CellWithCase<PPodDnaNucleotide, ?> cell = new DnaCell();

		cell.unsetInNeedOfNewVersion();
		cell.setSingleElement(PPodDnaNucleotide.C, false);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(getOnlyElement(cell.getElements()), PPodDnaNucleotide.C);

		cell.unsetInNeedOfNewVersion();
		cell.setSingleElement(PPodDnaNucleotide.C, false);
		assertFalse(cell.isInNeedOfNewVersion());
		assertSame(getOnlyElement(cell.getElements()), PPodDnaNucleotide.C);

		cell.unsetInNeedOfNewVersion();
		cell.setSingleElement(PPodDnaNucleotide.C, true);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(getOnlyElement(cell.getElements()), PPodDnaNucleotide.C);

		cell.unsetInNeedOfNewVersion();
		cell.setSingleElement(PPodDnaNucleotide.A, true);
		assertTrue(cell.isInNeedOfNewVersion());
		assertSame(getOnlyElement(cell.getElements()), PPodDnaNucleotide.A);
	}

	@Test
	public void setUncertainElements() {
		final CellWithCase<PPodDnaNucleotide, ?> cell = new DnaCell();
		cell.unsetInNeedOfNewVersion();
		final Set<PPodDnaNucleotide> nucleotides =
				ImmutableSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C);
		cell.setUncertainElements(nucleotides);
		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getElements(), nucleotides);
		assertNull(cell.getLowerCase());

		cell.setPolymorphicElements(nucleotides, false);

		cell.unsetInNeedOfNewVersion();
		cell.setUncertainElements(nucleotides);
		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getElements(), nucleotides);
		assertNull(cell.getLowerCase());

		cell.unsetInNeedOfNewVersion();
		cell.setUncertainElements(nucleotides);
		assertFalse(cell.isInNeedOfNewVersion());
		assertEquals(cell.getElements(), nucleotides);
		assertNull(cell.getLowerCase());
	}
}
