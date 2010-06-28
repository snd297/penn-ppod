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

import java.util.EnumSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST)
public class CellTest {

	@Inject
	private Provider<DNACell> dnaCellProvider;

	@Test(expectedExceptions = IllegalStateException.class)
	public void getElementsWhenNoTypeSet() {
		final Cell<?> cell = dnaCellProvider.get();
		cell.getElements();
	}

	@Test
	public void getElementsXml() {
		final DNACell cell = dnaCellProvider.get();
		cell.setType(Cell.Type.UNCERTAIN);
		final Set<DNANucleotide> cellElementsXml = cell.getElementsXml();
		assertNotNull(cellElementsXml);
		assertEquals(cellElementsXml.size(), 0);

		cell.setUnassigned();
		assertNull(cell.getElementsXml());

		cell.setSingleElement(DNANucleotide.A);
		assertNull(cell.getElementsXml());

		cell.setInapplicable();
		assertNull(cell.getElementsXml());

		final Set<DNANucleotide> nucleotides = EnumSet.of(DNANucleotide.A,
				DNANucleotide.G);
		cell.setPolymorphicElements(nucleotides);
		assertEquals((Object) cell.getElementsXml(), (Object) nucleotides);

		cell.setUncertainElements(nucleotides);
		assertEquals((Object) cell.getElementsXml(), (Object) nucleotides);

	}

}
