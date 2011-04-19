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
import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * {@link OTUSet} test.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class OtuSetTest {

	private OtuSet otuSet;

	private List<Otu> otus;

	private Study study;

	@Test
	public void addDnaMatrix() {
		final DnaMatrix dnaMatrix = new DnaMatrix();

		otuSet.addDnaMatrix(dnaMatrix);
		assertEquals(getOnlyElement(otuSet.getDnaMatrices()), dnaMatrix);
		assertSame(dnaMatrix.getParent(), otuSet);
	}

	@Test
	public void addDnaMatrixPos() {
		final OtuSet otuSet = new OtuSet();
		final DnaMatrix matrix0 = new DnaMatrix();
		final DnaMatrix matrix1 = new DnaMatrix();
		final DnaMatrix matrix2 = new DnaMatrix();
		final DnaMatrix matrix3 = new DnaMatrix();

		otuSet.addDnaMatrix(matrix0);
		otuSet.addDnaMatrix(matrix1);
		otuSet.addDnaMatrix(matrix2);

		otuSet.addDnaMatrix(2, matrix3);
		assertEquals(otuSet.getDnaMatrices().size(), 4);
		assertTrue(otuSet.getDnaMatrices().contains(matrix3));

		assertEquals(otuSet.getDnaMatrices(),
				ImmutableSet.of(matrix0, matrix1, matrix3, matrix2));

		assertSame(matrix3.getParent(), otuSet);
	}

	// @Test
	// public void addDNASequenceSet() {
	// final DnaSequenceSet sequenceSet = new DnaSequenceSet();
	//
	// otuSet.addDnaSequenceSet(sequenceSet);
	// assertEquals(getOnlyElement(otuSet.getDnaSequenceSets()), sequenceSet);
	// assertSame(sequenceSet.getParent(), otuSet);
	// }

	// @Test
	// public void addDNASequenceSetPos() {
	// final OtuSet otuSet = new OtuSet();
	// final DnaSequenceSet sequenceSet0 = new DnaSequenceSet();
	//
	// final DnaSequenceSet sequenceSet1 = new DnaSequenceSet();
	//
	// final DnaSequenceSet sequenceSet2 = new DnaSequenceSet();
	//
	// final DnaSequenceSet sequenceSet3 = new DnaSequenceSet();
	//
	// otuSet.addDnaSequenceSet(sequenceSet0);
	// otuSet.addDnaSequenceSet(sequenceSet1);
	// otuSet.addDnaSequenceSet(sequenceSet2);
	//
	// otuSet.addDnaSequenceSet(2, sequenceSet3);
	//
	// assertEquals(otuSet.getDnaSequenceSets().size(), 4);
	// assertTrue(otuSet.getDnaSequenceSets().contains(sequenceSet3));
	//
	// assertEquals(otuSet.getDnaSequenceSets(),
	// ImmutableList.of(sequenceSet0, sequenceSet1, sequenceSet3,
	// sequenceSet2));
	//
	// assertSame(sequenceSet3.getParent(), otuSet);
	// }

	@Test
	public void addOTU() {
		final OtuSet otuSet = new OtuSet();
		final StandardMatrix standardMatrix = new StandardMatrix();

		otuSet.addStandardMatrix(standardMatrix);

		final DnaMatrix dnaMatrix = new DnaMatrix();
		otuSet.addDnaMatrix(dnaMatrix);

		// final DnaSequenceSet dnaSequenceSet = new DnaSequenceSet();

		// otuSet.addDnaSequenceSet(dnaSequenceSet);

		final Otu otu0 = new Otu("otu-0");
		final Otu otu1 = new Otu("otu-1");
		final Otu otu2 = new Otu("otu-2");

		otuSet.addOtu(otu0);

		otuSet.addOtu(otu1);

		otuSet.addOtu(otu2);

		final List<Otu> otus012 = ImmutableList.of(otu0, otu1, otu2);
		final Set<Otu> otusSet012 = ImmutableSet.copyOf(otus012);

		assertEquals(otuSet.getOtus(), otus012);

		assertEquals(
				standardMatrix
						.getRows()
						.keySet(),
				otusSet012);
		assertNull(standardMatrix.getRows().get(otu0));
		assertNull(standardMatrix.getRows().get(otu1));
		assertNull(standardMatrix.getRows().get(otu2));

		assertEquals(
				dnaMatrix.getRows().keySet(),
				otusSet012);
		assertNull(dnaMatrix.getRows().get(otu0));
		assertNull(dnaMatrix.getRows().get(otu1));
		assertNull(dnaMatrix.getRows().get(otu2));

		// assertEquals(
		// dnaSequenceSet
		// .getSequences()
		// .keySet(),
		// otusSet012);
		//
		// assertNull(dnaSequenceSet.getSequences().get(otu0));
		// assertNull(dnaSequenceSet.getSequences().get(otu1));
		// assertNull(dnaSequenceSet.getSequences().get(otu2));
	}

	/**
	 * Add an otu that's already in an otu set into an otu set. The pPOD version
	 * should not be reset when this happens.
	 */
	@Test
	public void addOtuWAlreadyContainedOTU() {
		otuSet.clearAndAddOtus(ImmutableList.of(new Otu("OTU-0")));

		otuSet.clearAndAddOtus(otuSet.getOtus());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOtuWDuplicateLabel() {
		otus.add(new Otu(otus.get(0).getLabel()));
		otuSet.clearAndAddOtus(newArrayList(otus));
	}

	@Test(groups = { TestGroupDefs.FAST })
	public void addProteinMatrix() {
		final ProteinMatrix matrix = new ProteinMatrix();

		otuSet.addProteinMatrix(matrix);
		assertEquals(getOnlyElement(otuSet.getProteinMatrices()), matrix);
		assertSame(matrix.getParent(), otuSet);
	}

	@Test(groups = { TestGroupDefs.FAST })
	public void addProteinMatrixPos() {
		final OtuSet otuSet = new OtuSet();
		final ProteinMatrix matrix0 = new ProteinMatrix();
		final ProteinMatrix matrix1 = new ProteinMatrix();
		final ProteinMatrix matrix2 = new ProteinMatrix();
		final ProteinMatrix matrix3 = new ProteinMatrix();

		otuSet.addProteinMatrix(matrix0);
		otuSet.addProteinMatrix(matrix1);
		otuSet.addProteinMatrix(matrix2);

		otuSet.addProteinMatrix(2, matrix3);
		assertEquals(otuSet.getProteinMatrices().size(), 4);
		assertTrue(otuSet.getProteinMatrices().contains(matrix3));

		assertEquals(otuSet.getProteinMatrices(),
				ImmutableSet.of(matrix0, matrix1, matrix3, matrix2));

		assertSame(matrix3.getParent(), otuSet);
	}

	@Test
	public void addStandardMatrix() {
		final StandardMatrix matrix = new StandardMatrix();

		otuSet.addStandardMatrix(matrix);
		assertEquals(getOnlyElement(otuSet.getStandardMatrices()), matrix);
		assertSame(matrix.getParent(), otuSet);
	}

	@Test
	public void addStandardMatrixPos() {
		final OtuSet otuSet = new OtuSet();
		final StandardMatrix matrix0 = new StandardMatrix();
		final StandardMatrix matrix1 = new StandardMatrix();
		final StandardMatrix matrix2 = new StandardMatrix();
		final StandardMatrix matrix3 = new StandardMatrix();

		otuSet.addStandardMatrix(matrix0);
		otuSet.addStandardMatrix(matrix1);
		otuSet.addStandardMatrix(matrix2);

		otuSet.addStandardMatrix(2, matrix3);

		assertEquals(otuSet.getStandardMatrices(),
				ImmutableList.of(matrix0, matrix1, matrix3, matrix2));
		assertSame(matrix3.getParent(), otuSet);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addStandardMatrixPosWDup() {
		final OtuSet otuSet = new OtuSet();
		final StandardMatrix matrix0 = new StandardMatrix();
		otuSet.addStandardMatrix(0, matrix0);
		otuSet.addStandardMatrix(0, matrix0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addStandardMatrixWDup() {
		final OtuSet otuSet = new OtuSet();
		final StandardMatrix matrix0 = new StandardMatrix();
		otuSet.addStandardMatrix(matrix0);
		otuSet.addStandardMatrix(matrix0);
	}

	@Test
	public void addTreeSet() {
		final TreeSet treeSet0 = new TreeSet();

		otuSet.addTreeSet(treeSet0);
		assertSame(treeSet0.getParent(), otuSet);
	}

	@Test
	public void addTreeSetPos() {
		final OtuSet otuSet = new OtuSet();
		final TreeSet treeSet0 = new TreeSet();
		final TreeSet treeSet1 = new TreeSet();
		final TreeSet treeSet2 = new TreeSet();
		final TreeSet treeSet3 = new TreeSet();

		otuSet.addTreeSet(treeSet0);
		otuSet.addTreeSet(treeSet1);
		otuSet.addTreeSet(treeSet2);

		otuSet.addTreeSet(2, treeSet3);

		assertEquals(otuSet.getTreeSets(),
				ImmutableList.of(treeSet0, treeSet1, treeSet3, treeSet2));
		assertSame(treeSet3.getParent(), otuSet);
	}

	@BeforeMethod
	public void beforeMethod() {
		otuSet = new OtuSet();
		otus = newArrayList();

		final Otu otu0 = new Otu();
		otus.add(otu0);
		otu0.setLabel("otu0");

		final Otu otu1 = new Otu();
		otus.add(otu1);
		otu1.setLabel("otu1");

		final Otu otu2 = new Otu();
		otus.add(otu2);
		otu2.setLabel("otu2");

		otuSet.clearAndAddOtus(newArrayList(otus));

		// Do this so we can check that version resets are being done.
		study = new Study();
		study.addOtuSet(otuSet);
	}

	/**
	 * There was a bug where clearAndOtus was doing a shallow copy of the
	 * incoming otus. This is a problem for hibernate.
	 */
	@Test
	public void makeSureClearAndAddOtusDoesntRetainReference() {
		final OtuSet otuSet = new OtuSet();
		final Otu otu0 = new Otu("otu0");
		final Otu otu1 = new Otu("otu1");
		final Otu otu2 = new Otu("otu2");

		final List<Otu> otus = newArrayList(otu0, otu1, otu2);

		otuSet.clearAndAddOtus(otus);

		assertNotSame(otuSet.getOtus(), otus);

	}

	/**
	 * There was a bug where clearAndAddOtus was calling updateOtu on the
	 * children after the otu sets otus were cleared. This was nulling out the
	 * rows of the matrices.
	 */
	@Test
	public void makeSureRowsDontGetNullledOut() {
		final OtuSet otuSet = new OtuSet();

		final Otu otu0 = new Otu("otu0");
		final Otu otu1 = new Otu("otu1");
		final Otu otu2 = new Otu("otu2");

		final List<Otu> otus = newArrayList(otu0, otu1, otu2);

		otuSet.getOtus().addAll(otus);

		final StandardMatrix standardMatrix = new StandardMatrix();
		otuSet.addStandardMatrix(standardMatrix);

		standardMatrix.putRow(otu0, new StandardRow());
		standardMatrix.putRow(otu1, new StandardRow());
		standardMatrix.putRow(otu2, new StandardRow());

		otuSet.clearAndAddOtus(otus);

		// We were inadvertently wiping out all rows in one version of this test
		// by using OtuSet.setOtus, so make sure the rows are there
		assertEquals(standardMatrix.getRows().size(), 3);

		for (final StandardRow row : standardMatrix.getRows().values()) {
			assertNotNull(row);
		}

	}

	@Test
	public void removeDNAMatrix() {

		final DnaMatrix matrix0 = new DnaMatrix();
		otuSet.addDnaMatrix(matrix0);
		final DnaMatrix matrix1 = new DnaMatrix();
		otuSet.addDnaMatrix(matrix1);
		final DnaMatrix matrix2 = new DnaMatrix();
		otuSet.addDnaMatrix(matrix2);

		otuSet.removeDnaMatrix(matrix1);

		assertNull(matrix1.getParent());

		assertEquals(
				otuSet.getDnaMatrices(),
				ImmutableList.of(matrix0, matrix2));
	}

	// @Test
	// public void removeDNASequenceSet() {
	//
	// final OtuSet otuSet = new OtuSet();
	// final DnaSequenceSet dnaSequenceSet0 = new DnaSequenceSet();
	//
	// otuSet.addDnaSequenceSet(dnaSequenceSet0);
	// final DnaSequenceSet dnaSequenceSet1 = new DnaSequenceSet();
	//
	// otuSet.addDnaSequenceSet(dnaSequenceSet1);
	// final DnaSequenceSet dnaSequenceSet2 = new DnaSequenceSet();
	//
	// otuSet.addDnaSequenceSet(dnaSequenceSet2);
	//
	// otuSet.removeDnaSequenceSet(dnaSequenceSet1);
	//
	// assertEquals(
	// otuSet.getDnaSequenceSets(),
	// ImmutableList.of(dnaSequenceSet0, dnaSequenceSet2));
	// }

	/**
	 * Remove an otu set and make sure it was removed, otuSet is marked for a
	 * new pPOD version, and the return value of removeOTUs contains the removed
	 * OTU.
	 */
	@Test
	public void removeOtu() {

		final ImmutableList<Otu> otus2 =
				ImmutableList.of(otus.get(0), otus.get(2));

		otuSet.clearAndAddOtus(otus2);

		assertFalse(otuSet.getOtus().contains(otus.get(1)));
		assertNull(otus.get(1).getParent());
	}

	@Test
	public void removeStandardMatrix() {
		final StandardMatrix matrix0 = new StandardMatrix();
		otuSet.addStandardMatrix(matrix0);
		final StandardMatrix matrix1 = new StandardMatrix();
		otuSet.addStandardMatrix(matrix1);
		final StandardMatrix matrix2 = new StandardMatrix();
		otuSet.addStandardMatrix(matrix2);

		otuSet.removeStandardMatrix(matrix1);

		assertNull(matrix1.getParent());

		assertEquals(
				otuSet.getStandardMatrices(),
				ImmutableList.of(matrix0, matrix2));

	}

	@Test
	public void removeTreeSet() {
		final TreeSet treeSet0 = new TreeSet();
		treeSet0.setLabel("treeSet0");
		otuSet.addTreeSet(treeSet0);
		final TreeSet treeSet1 = new TreeSet();
		treeSet1.setLabel("treeSet1");
		otuSet.addTreeSet(treeSet1);
		final TreeSet treeSet2 = new TreeSet();
		treeSet2.setLabel("treeSet2");
		otuSet.addTreeSet(treeSet2);

		otuSet.removeTreeSet(treeSet1);

		assertNull(treeSet1.getParent());
		assertEquals(otuSet.getTreeSets(),
						ImmutableList.of(treeSet0, treeSet2));
	}

}
