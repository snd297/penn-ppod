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
import static org.testng.Assert.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = { TestGroupDefs.FAST })
public class CellTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<DNAMatrix> dnaMatrixProvider;

	@Inject
	private Provider<DNARow> dnaRowProvider;

	@Inject
	private Provider<DNACell> dnaCellProvider;

	@Inject
	private Provider<VersionInfo> versionInfoProvider;

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

	@Test
	public void setInNeedOfNewVersion() {
		// First let's test when a cell isn't attached to anything
		final DNACell cell = dnaCellProvider.get();
		cell.unsetInNeedOfNewVersion();
		cell.setInNeedOfNewVersion();
		assertTrue(cell.isInNeedOfNewVersion());

		// Now let's put it into a matrix.
		final OTUSet otuSet = otuSetProvider.get();
		final OTU otu = otuSet.addOTU(otuProvider.get().setLabel("otu0"));
		final DNAMatrix matrix = dnaMatrixProvider.get();
		matrix.setColumnsSize(1);

		otuSet.addDNAMatrix(matrix);

		final DNARow row = dnaRowProvider.get();

		matrix.putRow(otu, row);
		row.setCells(ImmutableList.of(cell));

		otuSet.unsetInNeedOfNewVersion();
		otu.unsetInNeedOfNewVersion();
		matrix.unsetInNeedOfNewVersion();
		matrix.setColumnVersionInfos(versionInfoProvider.get());
		row.unsetInNeedOfNewVersion();
		cell.unsetInNeedOfNewVersion();

		cell.setInNeedOfNewVersion();

		assertTrue(otuSet.isInNeedOfNewVersion());
		assertFalse(otu.isInNeedOfNewVersion());
		assertTrue(matrix.isInNeedOfNewVersion());
		assertNull(matrix.getColumnVersionInfos().get(cell.getPosition()));
		assertTrue(row.isInNeedOfNewVersion());
		assertTrue(cell.isInNeedOfNewVersion());

		// Now let's test it in a row w/ no matrix, which should only happen
		// when we remove a row from a matrix.
		otuSet.removeDNAMatrix(matrix);
		otuSet.unsetInNeedOfNewVersion();
		otu.unsetInNeedOfNewVersion();
		matrix.unsetInNeedOfNewVersion();
		row.unsetInNeedOfNewVersion();
		cell.unsetInNeedOfNewVersion();

		cell.setInNeedOfNewVersion();

		assertFalse(otuSet.isInNeedOfNewVersion());
		assertFalse(otu.isInNeedOfNewVersion());
		assertFalse(matrix.isInNeedOfNewVersion());
		assertTrue(row.isInNeedOfNewVersion());
		assertTrue(cell.isInNeedOfNewVersion());

	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void setInNeedOfNewVersionWithNullPosition() {

		final DNACell cell = dnaCellProvider.get();
		final OTUSet otuSet = otuSetProvider.get();
		final OTU otu = otuSet.addOTU(otuProvider.get().setLabel("otu0"));
		final DNAMatrix matrix = dnaMatrixProvider.get();
		matrix.setColumnsSize(1);

		otuSet.addDNAMatrix(matrix);

		final DNARow row = dnaRowProvider.get();

		matrix.putRow(otu, row);
		row.setCells(ImmutableList.of(cell));

		cell.unsetPosition();

		cell.setInNeedOfNewVersion();

	}
}
