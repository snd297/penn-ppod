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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.util.IVisitor;

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
	public void accept() {

		final OtuSet otuSet = new OtuSet();

		otuSet.addOtu(new Otu("otu-0"));
		otuSet.addOtu(new Otu("otu-1"));
		otuSet.addOtu(new Otu("otu-2"));

		otuSet.addStandardMatrix(new StandardMatrix());
		otuSet.addStandardMatrix(new StandardMatrix());
		otuSet.addStandardMatrix(new StandardMatrix());

		otuSet.addDnaMatrix(new DnaMatrix());
		otuSet.addDnaMatrix(new DnaMatrix());
		otuSet.addDnaMatrix(new DnaMatrix());

		otuSet.addDnaSequenceSet(new DnaSequenceSet());
		otuSet.addDnaSequenceSet(new DnaSequenceSet());
		otuSet.addDnaSequenceSet(new DnaSequenceSet());

		otuSet.addTreeSet(new TreeSet());
		otuSet.addTreeSet(new TreeSet());
		otuSet.addTreeSet(new TreeSet());

		otuSet.addAttachment(new Attachment());
		otuSet.addAttachment(new Attachment());
		otuSet.addAttachment(new Attachment());

		final IVisitor visitor = mock(IVisitor.class);
		otuSet.accept(visitor);

		verify(visitor, times(otuSet.getOtus().size()))
				.visitOtu(any(Otu.class));
		verify(visitor, times(otuSet.getStandardMatrices().size()))
				.visitStandardMatrix(any(StandardMatrix.class));
		verify(visitor, times(otuSet.getDnaMatrices().size())).visitDnaMatrix(
				any(DnaMatrix.class));
		verify(visitor, times(otuSet.getDnaSequenceSets().size()))
				.visitDnaSequenceSet(any(DnaSequenceSet.class));
		verify(visitor, times(otuSet.getAttachments().size())).visitAttachment(
				any(Attachment.class));
	}

	@Test
	public void addDNAMatrix() {
		final DnaMatrix dnaMatrix = new DnaMatrix();

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addDnaMatrix(dnaMatrix);
		assertEquals(getOnlyElement(otuSet.getDnaMatrices()), dnaMatrix);
		assertSame(dnaMatrix.getParent(), otuSet);
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	@Test
	public void addDNAMatrixPos() {
		final OtuSet otuSet = new OtuSet();
		final DnaMatrix matrix0 = new DnaMatrix();
		final DnaMatrix matrix1 = new DnaMatrix();
		final DnaMatrix matrix2 = new DnaMatrix();
		final DnaMatrix matrix3 = new DnaMatrix();

		otuSet.addDnaMatrix(matrix0);
		otuSet.addDnaMatrix(matrix1);
		otuSet.addDnaMatrix(matrix2);

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addDnaMatrix(2, matrix3);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertEquals(otuSet.getDnaMatrices().size(), 4);
		assertTrue(otuSet.getDnaMatrices().contains(matrix3));

		assertEquals(otuSet.getDnaMatrices(),
				ImmutableSet.of(matrix0, matrix1, matrix3, matrix2));

		assertSame(matrix3.getParent(), otuSet);
	}

	@Test
	public void addDNASequenceSet() {
		final DnaSequenceSet sequenceSet = new DnaSequenceSet();

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addDnaSequenceSet(sequenceSet);
		assertEquals(getOnlyElement(otuSet.getDnaSequenceSets()), sequenceSet);
		assertSame(sequenceSet.getParent(), otuSet);
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	@Test
	public void addDNASequenceSetPos() {
		final OtuSet otuSet = new OtuSet();
		final DnaSequenceSet sequenceSet0 = new DnaSequenceSet();

		final DnaSequenceSet sequenceSet1 = new DnaSequenceSet();

		final DnaSequenceSet sequenceSet2 = new DnaSequenceSet();

		final DnaSequenceSet sequenceSet3 = new DnaSequenceSet();

		otuSet.addDnaSequenceSet(sequenceSet0);
		otuSet.addDnaSequenceSet(sequenceSet1);
		otuSet.addDnaSequenceSet(sequenceSet2);

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addDnaSequenceSet(2, sequenceSet3);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertEquals(otuSet.getDnaSequenceSets().size(), 4);
		assertTrue(otuSet.getDnaSequenceSets().contains(sequenceSet3));

		assertEquals(otuSet.getDnaSequenceSets(),
				ImmutableList.of(sequenceSet0, sequenceSet1, sequenceSet3,
						sequenceSet2));

		assertSame(sequenceSet3.getParent(), otuSet);
	}

	@Test
	public void addOTU() {
		final OtuSet otuSet = new OtuSet();
		final StandardMatrix standardMatrix = new StandardMatrix();

		otuSet.addStandardMatrix(standardMatrix);

		final DnaMatrix dnaMatrix = new DnaMatrix();
		otuSet.addDnaMatrix(dnaMatrix);

		final DnaSequenceSet dnaSequenceSet = new DnaSequenceSet();

		otuSet.addDnaSequenceSet(dnaSequenceSet);

		final Otu otu0 = new Otu("otu-0");
		final Otu otu1 = new Otu("otu-1");
		final Otu otu2 = new Otu("otu-2");

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		otuSet.addOtu(otu0);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		otuSet.addOtu(otu1);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		otuSet.addOtu(otu2);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

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

		assertEquals(
				dnaSequenceSet
						.getSequences()
						.keySet(),
						otusSet012);

		assertNull(dnaSequenceSet.getSequence(otu0));
		assertNull(dnaSequenceSet.getSequence(otu1));
		assertNull(dnaSequenceSet.getSequence(otu2));
	}

	/**
	 * Add an otu that's already in an otu set into an otu set. The pPOD version
	 * should not be reset when this happens.
	 */
	@Test
	public void addOtuWAlreadyContainedOTU() {
		otuSet.setOtus(ImmutableList.of(new Otu("OTU-0")));
		otuSet.unsetInNeedOfNewVersion();

		otuSet.setOtus(otuSet.getOtus());
		assertFalse(otuSet.isInNeedOfNewVersion());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOtuWDuplicateLabel() {
		otus.add(new Otu(otus.get(0).getLabel()));
		otuSet.setOtus(newArrayList(otus));
	}

	@Test
	public void addStandardMatrix() {
		final StandardMatrix dnaMatrix = new StandardMatrix();

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addStandardMatrix(dnaMatrix);
		assertEquals(getOnlyElement(otuSet.getStandardMatrices()), dnaMatrix);
		assertSame(dnaMatrix.getParent(), otuSet);
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addStandardMatrixWDup() {
		final OtuSet otuSet = new OtuSet();
		final StandardMatrix matrix0 = new StandardMatrix();
		otuSet.addStandardMatrix(matrix0);
		otuSet.addStandardMatrix(matrix0);
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

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addStandardMatrix(2, matrix3);
		assertTrue(otuSet.isInNeedOfNewVersion());

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

	@Test
	public void addTreeSet() {
		final TreeSet treeSet0 = new TreeSet();

		otuSet.unsetInNeedOfNewVersion();
		otuSet.addTreeSet(treeSet0);
		assertTrue(otuSet.isInNeedOfNewVersion());
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

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addTreeSet(2, treeSet3);
		assertTrue(otuSet.isInNeedOfNewVersion());
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

		otuSet.setOtus(newArrayList(otus));

		// Do this so we can check that version resets are being done.
		study = new Study();
		study.addOtuSet(otuSet);
	}

	/**
	 * Remove all OTUs
	 */
	@Test
	public void clearOTUs() {
		otuSet.setOtus(newArrayList(otus));

		otuSet.setVersionInfo(new VersionInfo());
		study.setVersionInfo(new VersionInfo());

		study.unsetInNeedOfNewVersion();
		otuSet.unsetInNeedOfNewVersion();

		otuSet.setOtus(new ArrayList<Otu>());

		assertTrue(otuSet.isInNeedOfNewVersion());
		// assertNull(otuSet.getPPodVersionInfo());
		assertTrue(study.isInNeedOfNewVersion());
		// assertNull(study.getPPodVersionInfo());
		assertEquals(otuSet.getOtus().size(), 0);
	}

	@Test
	public void removeDNAMatrix() {

		final DnaMatrix matrix0 = new DnaMatrix();
		otuSet.addDnaMatrix(matrix0);
		final DnaMatrix matrix1 = new DnaMatrix();
		otuSet.addDnaMatrix(matrix1);
		final DnaMatrix matrix2 = new DnaMatrix();
		otuSet.addDnaMatrix(matrix2);

		otuSet.unsetInNeedOfNewVersion();

		study.unsetInNeedOfNewVersion();

		otuSet.removeDnaMatrix(matrix1);

		assertTrue(otuSet.isInNeedOfNewVersion());
		assertNull(matrix1.getParent());

		assertEquals(
				otuSet.getDnaMatrices(),
				ImmutableList.of(matrix0, matrix2));
	}

	@Test
	public void removeDNASequenceSet() {

		final OtuSet otuSet = new OtuSet();
		final DnaSequenceSet dnaSequenceSet0 = new DnaSequenceSet();

		otuSet.addDnaSequenceSet(dnaSequenceSet0);
		final DnaSequenceSet dnaSequenceSet1 = new DnaSequenceSet();

		otuSet.addDnaSequenceSet(dnaSequenceSet1);
		final DnaSequenceSet dnaSequenceSet2 = new DnaSequenceSet();

		otuSet.addDnaSequenceSet(dnaSequenceSet2);

		otuSet.removeDnaSequenceSet(dnaSequenceSet1);

		assertEquals(
				otuSet.getDnaSequenceSets(),
				ImmutableList.of(dnaSequenceSet0, dnaSequenceSet2));
	}

	/**
	 * Remove an otu set and make sure it was removed, otuSet is marked for a
	 * new pPOD version, and the return value of removeOTUs contains the removed
	 * OTU.
	 */
	@Test
	public void removeOtu() {
		otuSet.unsetInNeedOfNewVersion();

		final ImmutableList<Otu> otus2 =
				ImmutableList.of(otus.get(0), otus.get(2));

		otuSet.setOtus(otus2);

		assertFalse(otuSet.getOtus().contains(otus.get(1)));
		assertTrue(otuSet.isInNeedOfNewVersion());
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

		otuSet.unsetInNeedOfNewVersion();

		study.unsetInNeedOfNewVersion();

		otuSet.removeStandardMatrix(matrix1);

		assertTrue(otuSet.isInNeedOfNewVersion());
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

		assertTrue(otuSet.isInNeedOfNewVersion());
		assertNull(treeSet1.getParent());
		assertEquals(otuSet.getTreeSets(),
						ImmutableList.of(treeSet0, treeSet2));
	}

	@Test
	public void setDescription() {
		otuSet.unsetInNeedOfNewVersion();
		final String description = "DESCRIPTION";
		otuSet.setDescription(description);
		assertEquals(otuSet.getDescription(), description);
		assertTrue(otuSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		otuSet.setDescription(description);
		assertFalse(otuSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		otuSet.setDescription(null);
		assertNull(otuSet.getDescription());
		assertTrue(otuSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		otuSet.setDescription(null);
		assertNull(otuSet.getDescription());
		assertFalse(otuSet.isInNeedOfNewVersion());
	}

	@Test
	public void setInNeedOfNewPPodVersion() {
		otuSet.unsetInNeedOfNewVersion();
		otuSet.getParent().unsetInNeedOfNewVersion();

		otuSet.setInNeedOfNewVersion();
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(otuSet.getParent().isInNeedOfNewVersion());

	}

	@Test
	public void setInNeedOfNewPPodVersionWithNoStudy() {
		final OtuSet otuSetWithNoStudy = new OtuSet();
		otuSetWithNoStudy.unsetInNeedOfNewVersion();
		otuSetWithNoStudy.setInNeedOfNewVersion();
		assertTrue(otuSetWithNoStudy.isInNeedOfNewVersion());
	}

	@Test
	public void setLabel() {
		otuSet.unsetInNeedOfNewVersion();
		final String otuSetLabel = "otu-set-label";
		otuSet.setLabel(otuSetLabel);
		assertTrue(otuSet.isInNeedOfNewVersion());

		otuSet.isInNeedOfNewVersion();
		assertEquals(otuSet.getLabel(), otuSetLabel);

		otuSet.unsetInNeedOfNewVersion();
		otuSet.setLabel(otuSetLabel);
		assertFalse(otuSet.isInNeedOfNewVersion());
		assertEquals(otuSet.getLabel(), otuSetLabel);
	}

	@Test
	public void setStudy() {
		otuSet.unsetInNeedOfNewVersion();
		final Study study = new Study();
		otuSet.setParent(study);
		assertSame(otuSet.getParent(), study);

		// Setting the study should have no affect
		assertFalse(otuSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		otuSet.setParent(null);
		assertNull(otuSet.getParent());
		assertFalse(otuSet.isInNeedOfNewVersion());
	}
}
