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
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = { TestGroupDefs.FAST })
public class CellTest {

	@Inject
	private Provider<DNACell> dnaCellProvider;

	@Test
	public void afterUnmarshal() {
		final DNARow row = new DNARow();
		final DNACell cell = new DNACell();
		cell.afterUnmarshal(null, row);
		assertSame(cell.getParent(), row);
	}

	/**
	 * Verify that {@link PPodEntity#beforeMarshal(javax.xml.bind.Marshaller)}
	 * is called when {@link Cell#beforeMarshal(javax.xml.bind.Marshaller)} is
	 * called.
	 */
	@Test
	public void beforeMarshal() {
		final VersionInfo versionInfo = new VersionInfo().setVersion(23L);
		final Cell<DNANucleotide, ?> cell = new DNACell();
		cell.setType(Cell.Type.INAPPLICABLE);
		cell.setVersionInfo(versionInfo);
		cell.beforeMarshal(null);
		assertEquals(cell.getVersion(), versionInfo.getVersion());
	}

	/**
	 * {@code beforeMarshal(...)} should throw an {@code IllegalStateException}
	 * if the type has not bee set yet.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void beforeMarshalBeforeTypeHasBeenSet() {
		final DNACell cell = dnaCellProvider.get();
		cell.beforeMarshal(null);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void beforeMarshalWNoType() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.beforeMarshal(null);
	}

	/**
	 * Matrix must be ready to have a row with one cell added to it.
	 * 
	 * @param matrix
	 * @param elements
	 */
	@Test
	public void getElementsWhenCellHasMultipleElements() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();

		final Set<DNANucleotide> elements =
				ImmutableSet.of(DNANucleotide.A, DNANucleotide.T);

		cell.setType(Cell.Type.POLYMORPHIC);
		cell.setElements(elements);
		assertEquals(cell.getElements(), elements);

		cell.setType(Cell.Type.UNCERTAIN);
		assertEquals(cell.getElements(), elements);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void getElementsWhenNoTypeSet() {
		final Cell<?, ?> cell = dnaCellProvider.get();
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

		cell.setSingleElement(DNANucleotide.A, true);
		assertNull(cell.getElementsXml());

		cell.setInapplicable();
		assertNull(cell.getElementsXml());

		final Set<DNANucleotide> nucleotides = EnumSet.of(DNANucleotide.A,
				DNANucleotide.G);
		cell.setPolymorphicElements(nucleotides, true);
		assertEquals((Object) cell.getElementsXml(), (Object) nucleotides);
		cell.setUncertainElements(nucleotides);
		assertEquals((Object) cell.getElementsXml(), (Object) nucleotides);

	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void getElementsXmlWNoType() {
		final Cell<?, ?> cell = new DNACell();
		cell.getElementsXml();
	}

	@Test
	public void getStatesWhenCellHasNoElements() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.setType(Cell.Type.UNASSIGNED);
		cell.setElement(null);
		cell.setElements(null);
		assertEquals(cell.getElements(), Collections.emptyList());

		cell.setType(Cell.Type.INAPPLICABLE);
		assertEquals(cell.getElements(), Collections.emptyList());
	}

	@Test
	public void getStatesWhenCellHasOneElement() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();

		final DNANucleotide nucleotide = DNANucleotide.C;

		cell.setElement(nucleotide);
		cell.setType(Cell.Type.SINGLE);
		assertEquals(cell.getElements(), ImmutableSet.of(nucleotide));
	}

	@Test(groups = TestGroupDefs.SINGLE)
	public void setInNeedOfNewVersion() {
		final DNACell cell = new DNACell();
		cell.unsetInNeedOfNewVersion();

		cell.setInNeedOfNewVersion();
		assertTrue(cell.isInNeedOfNewVersion());

		final DNARow row = new DNARow();

		final DNAMatrix matrix = new DNAMatrix(new DNARows());
		matrix.setColumnsSize(1);
		final OTUSet otuSet = new OTUSet();
		otuSet.addDNAMatrix(matrix);
		otuSet.addOTU(new OTU().setLabel("otu-0"));

		matrix.putRow(otuSet.getOTUs().get(0), row);

		row.setCells(ImmutableList.of(cell));

		cell.unsetInNeedOfNewVersion();
		row.unsetInNeedOfNewVersion();
		matrix.setInNeedOfNewColumnVersion(0);

		cell.setInNeedOfNewVersion();
		assertTrue(cell.isInNeedOfNewVersion());
		assertTrue(row.isInNeedOfNewVersion());
		assertNull(matrix.getColumnVersionInfos().get(0));

		matrix.putRow(otuSet.getOTUs().get(0), new DNARow());

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
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.unsetInNeedOfNewVersion();
		cell.setInapplicable();

		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), Cell.Type.INAPPLICABLE);
		assertEquals(cell.getElements(), Collections.emptySet());

		cell.unsetInNeedOfNewVersion();
		cell.setInapplicable();

		assertFalse(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), Cell.Type.INAPPLICABLE);
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
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();

		final Set<DNANucleotide> nucleotides =
				EnumSet.of(DNANucleotide.A, DNANucleotide.C);

		cell.setPolymorphicOrUncertain(
				Cell.Type.UNCERTAIN,
				nucleotides);

		assertSame(Cell.Type.UNCERTAIN, cell.getType());
		assertEquals(nucleotides, cell.getElements());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicOrUncertainWInapplicable() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.setPolymorphicOrUncertain(
				Cell.Type.INAPPLICABLE,
				ImmutableSet.of(DNANucleotide.A, DNANucleotide.C));
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
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();

		final Set<DNANucleotide> nucleotides =
				EnumSet.of(DNANucleotide.A, DNANucleotide.C);

		cell.setPolymorphicOrUncertain(
				Cell.Type.UNCERTAIN,
				nucleotides);

		cell.setPolymorphicOrUncertain(
				Cell.Type.UNCERTAIN,
				nucleotides);

		assertSame(Cell.Type.UNCERTAIN, cell.getType());
		assertEquals(nucleotides, cell.getElements());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicOrUncertainWSingle() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.setPolymorphicOrUncertain(
				Cell.Type.SINGLE,
				ImmutableSet.of(DNANucleotide.A, DNANucleotide.C));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicOrUncertainWTooFewElements() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.setPolymorphicOrUncertain(
				Cell.Type.POLYMORPHIC,
				ImmutableSet.of(DNANucleotide.A));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPolymorphicOrUncertainWUnassigned() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.setPolymorphicOrUncertain(
				Cell.Type.UNASSIGNED,
				ImmutableSet.of(DNANucleotide.A, DNANucleotide.C));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setPosition() {
		final Cell<?, ?> cell = new DNACell();
		cell.setPosition(-1);
	}

	@Test
	public void setUnassigned() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.unsetInNeedOfNewVersion();
		cell.setUnassigned();

		assertTrue(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), Cell.Type.UNASSIGNED);
		assertEquals(cell.getElements(), Collections.emptySet());

		cell.unsetInNeedOfNewVersion();
		cell.setUnassigned();

		assertFalse(cell.isInNeedOfNewVersion());
		assertEquals(cell.getType(), Cell.Type.UNASSIGNED);
		assertEquals(cell.getElements(), Collections.emptySet());

	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setUncertainElementsTooFewStates() {
		final Cell<DNANucleotide, ?> cell = dnaCellProvider.get();
		cell.setUncertainElements(ImmutableSet.of(DNANucleotide.G));
	}
}