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
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.model.Cell.Type;

/**
 * Test {@link DNACell}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST }, dependsOnGroups = TestGroupDefs.INIT)
public class DNACellTest {

	@Inject
	private CellTest<DNAMatrix, DNARow, DNACell, DNANucleotide> cellTest;

	@Inject
	private Provider<DNAMatrix> dnaMatrix2Provider;

	@Inject
	private Provider<DNACell> dnaCellProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<DNARow> dnaRowProvider;

	@Test
	public void getStatesWhenCellHasOneState() {
		final DNAMatrix matrix = dnaMatrix2Provider.get();
		matrix.setColumnsSize(1);
		// nothing special about A,C,T.
		cellTest.getStatesWhenCellHasMultipleElements(matrix,
				ImmutableSet.of(
						DNANucleotide.A, DNANucleotide.C,
						DNANucleotide.T));
	}

	@Test
	public void afterUnmarshal() {
		final DNAMatrix matrix = dnaMatrix2Provider.get();
		matrix.setColumnsSize(1);

		// nothing special about A,C,T.
		cellTest.afterUnmarshal(matrix, ImmutableSet.of(
				DNANucleotide.A, DNANucleotide.C,
				DNANucleotide.T));
	}

	/**
	 * {@code beforeMarshal(...)} should throw an exception if the type has not
	 * bee set yet.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void beforeMarshalBeforeTypeHasBeenSet() {
		cellTest.beforeMarshalBeforeTypeHasBeenSet();
	}

	/**
	 * Straight {@link Cell#beforeMarshal(javax.xml.bind.Marshaller)} test.
	 * Makes sure that {@link Cell#getXmlElements()} contains the elements after
	 * {@code beforeMarshal()} is called.
	 */
	@Test
	public void beforeMarshal() {
		final DNAMatrix matrix = dnaMatrix2Provider.get();
		matrix.setColumnsSize(1);
		final DNACell cell = dnaCellProvider.get();

		final OTUSet otuSet = otuSetProvider.get();
		otuSet.addOTU(otuProvider.get());

		matrix.setOTUSet(otuSet);

		matrix.putRow(otuSet.getOTU(0), dnaRowProvider.get());

		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				ImmutableList.of(cell));

		final Set<DNANucleotide> elements = ImmutableSet.of(DNANucleotide.A,
				DNANucleotide.C);

		cell.setPolymorphicElements(elements);

		cell.beforeMarshal(null);
		final Set<DNANucleotide> xmlStates = cell.getXmlElements();
		assertEquals(xmlStates.size(), elements.size());
		for (final DNANucleotide expectedElement : elements) {
			assertTrue(xmlStates.contains(expectedElement));
		}
	}

	@Test
	public void getStatesWhenCellHasOneElement() {
		cellTest.getStatesWhenCellHasOneElement((DNAMatrix)
				dnaMatrix2Provider.get().setColumnsSize(1),
				DNANucleotide.C);
	}

	@Test
	public void setTypeAndStatesInapplicable() {
		final DNACell dnaCell = dnaCellProvider.get();
		dnaCell.unsetInNeedOfNewPPodVersionInfo();

		@SuppressWarnings("unchecked")
		final Set<DNANucleotide> emptySet = Collections.EMPTY_SET;
		dnaCell.setTypeAndElements(Type.INAPPLICABLE, emptySet);
		assertEquals(dnaCell.getType(), Type.INAPPLICABLE);
		assertEquals((Object) newHashSet(dnaCell.iterator()),
					(Object) emptySet);
		assertTrue(dnaCell.isInNeedOfNewPPodVersionInfo());
	}

	@Test
	public void setTypeAndStatesSingle() {
		final DNACell dnaCell = dnaCellProvider.get();
		dnaCell.unsetInNeedOfNewPPodVersionInfo();
		dnaCell.setTypeAndElements(Type.SINGLE,
				newHashSet(DNANucleotide.A));
		assertEquals(dnaCell.getType(), Type.SINGLE);
		assertEquals((Object) newHashSet(dnaCell.iterator()),
					(Object) newHashSet(DNANucleotide.A));
		assertTrue(dnaCell.isInNeedOfNewPPodVersionInfo());
	}

	@Test
	public void setTypeAndStatesSingleWithValuesItAlreadyHad() {
		final DNACell dnaCell = dnaCellProvider.get();

		dnaCell.unsetInNeedOfNewPPodVersionInfo();
		dnaCell.setTypeAndElements(Type.SINGLE,
				newHashSet(DNANucleotide.A));
		dnaCell.setTypeAndElements(Type.SINGLE, newHashSet(DNANucleotide.A));

		assertEquals(dnaCell.getType(), Type.SINGLE);
		assertEquals((Object) newHashSet(dnaCell.iterator()),
					(Object) newHashSet(DNANucleotide.A));
		assertTrue(dnaCell.isInNeedOfNewPPodVersionInfo());
	}
}
