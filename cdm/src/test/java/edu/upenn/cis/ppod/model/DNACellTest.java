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
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.DNANucleotide;
import edu.upenn.cis.ppod.imodel.ICell;
import edu.upenn.cis.ppod.imodel.ICell.Type;

/**
 * Test {@link DNACell}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class DNACellTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicElementsTooFewStates() {
		final DnaCell cell = new DnaCell();
		final Set<DNANucleotide> nucleotides =
				ImmutableSet.of(DNANucleotide.A);
		cell.setPolymorphicElements(nucleotides, true);
	}

	@Test
	public void setTypeAndStatesInapplicable() {
		final DnaCell dnaCell = new DnaCell();
		dnaCell.unsetInNeedOfNewVersion();

		dnaCell.setInapplicable();
		assertEquals(dnaCell.getType(), Type.INAPPLICABLE);
		assertEquals(dnaCell.getElements().size(), 0);
		assertTrue(dnaCell.isInNeedOfNewVersion());
	}

	@Test
	public void setTypeAndStatesSingle() {
		final DnaCell dnaCell = new DnaCell();
		dnaCell.unsetInNeedOfNewVersion();
		dnaCell.setSingleElement(DNANucleotide.A, false);
		assertEquals(dnaCell.getType(), Type.SINGLE);
		assertEquals(getOnlyElement(dnaCell.getElements()),
				DNANucleotide.A);
		assertTrue(dnaCell.isInNeedOfNewVersion());
	}

	@Test
	public void setSingleElementWithValueItAlreadyHad() {
		final DnaCell dnaCell = new DnaCell();

		dnaCell.unsetInNeedOfNewVersion();
		dnaCell.setSingleElement(DNANucleotide.A, false);
		dnaCell.setSingleElement(DNANucleotide.A, false);

		assertEquals(
				dnaCell.getType(),
				Type.SINGLE);
		assertEquals(
				getOnlyElement(
						dnaCell.getElements()),
					DNANucleotide.A);
		assertTrue(dnaCell.isInNeedOfNewVersion());
	}

	public void setPolymorphicOrUncertain() {
		final DnaCell cell = new DnaCell();
		final Set<DNANucleotide> nucleotides =
				ImmutableSet.of(DNANucleotide.A, DNANucleotide.T);
		cell.unsetInNeedOfNewVersion();

		cell.setPolymorphicOrUncertain(
						ICell.Type.POLYMORPHIC,
						nucleotides);

		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), ICell.Type.POLYMORPHIC);
		assertEquals(cell.getElements(),
					nucleotides);

		cell.unsetInNeedOfNewVersion();

		cell.setPolymorphicOrUncertain(ICell.Type.POLYMORPHIC, nucleotides);

		assertFalse(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), ICell.Type.POLYMORPHIC);
		assertEquals((Object) cell.getElements(),
				(Object) nucleotides);

	}
}
