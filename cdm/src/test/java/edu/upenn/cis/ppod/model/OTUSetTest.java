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

import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
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
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * {@link OTUSet} test.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class OTUSetTest {

	private OTUSet otuSet;

	private List<IOTU> otus;

	private IStudy study;

	@Test
	public void accept() {

		final OTUSet otuSet = new OTUSet();

		otuSet.addOTU(new OTU().setLabel("otu-0"));
		otuSet.addOTU(new OTU().setLabel("otu-1"));
		otuSet.addOTU(new OTU().setLabel("otu-2"));

		otuSet.addStandardMatrix(new StandardMatrix(new StandardRows()));
		otuSet.addStandardMatrix(new StandardMatrix(new StandardRows()));
		otuSet.addStandardMatrix(new StandardMatrix(new StandardRows()));

		otuSet.addDNAMatrix(new DNAMatrix(new DNARows()));
		otuSet.addDNAMatrix(new DNAMatrix(new DNARows()));
		otuSet.addDNAMatrix(new DNAMatrix(new DNARows()));

		otuSet.addDNASequenceSet(new DNASequenceSet(new DNASequences()));
		otuSet.addDNASequenceSet(new DNASequenceSet(new DNASequences()));
		otuSet.addDNASequenceSet(new DNASequenceSet(new DNASequences()));

		otuSet.addTreeSet(new TreeSet());
		otuSet.addTreeSet(new TreeSet());
		otuSet.addTreeSet(new TreeSet());

		otuSet.addAttachment(new Attachment());
		otuSet.addAttachment(new Attachment());
		otuSet.addAttachment(new Attachment());

		final IVisitor visitor = mock(IVisitor.class);
		otuSet.accept(visitor);

		verify(visitor, times(otuSet.getOTUs().size()))
				.visitOTU(any(OTU.class));
		verify(visitor, times(otuSet.getStandardMatrices().size()))
				.visitStandardMatrix(any(StandardMatrix.class));
		verify(visitor, times(otuSet.getDNAMatrices().size())).visitDNAMatrix(
				any(DNAMatrix.class));
		verify(visitor, times(otuSet.getDNASequenceSets().size()))
				.visitDNASequenceSet(any(DNASequenceSet.class));
		verify(visitor, times(otuSet.getAttachments().size())).visitAttachment(
				any(Attachment.class));
	}

	@Test
	public void addDNAMatrix() {
		final DNAMatrix dnaMatrix = new DNAMatrix(new DNARows());

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addDNAMatrix(dnaMatrix);
		assertEquals(getOnlyElement(otuSet.getDNAMatrices()), dnaMatrix);
		assertSame(dnaMatrix.getParent(), otuSet);
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	public void addDNAMatrixPos() {
		final IOTUSet otuSet = new OTUSet();
		final IDNAMatrix matrix0 = new DNAMatrix(new DNARows());
		final IDNAMatrix matrix1 = new DNAMatrix(new DNARows());
		final IDNAMatrix matrix2 = new DNAMatrix(new DNARows());
		final IDNAMatrix matrix3 = new DNAMatrix(new DNARows());

		otuSet.addDNAMatrix(matrix0);
		otuSet.addDNAMatrix(matrix1);
		otuSet.addDNAMatrix(matrix2);

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addDNAMatrix(2, matrix3);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertEquals(otuSet.getDNAMatrices().size(), 4);
		assertTrue(otuSet.getDNAMatrices().contains(matrix3));

		assertEquals(otuSet.getDNAMatrices(),
				ImmutableSet.of(matrix0, matrix1, matrix3, matrix2));

		assertSame(matrix3.getParent(), otuSet);
	}

	@Test
	public void addDNASequenceSet() {
		final DNASequenceSet sequenceSet = new DNASequenceSet(
				new DNASequences());

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addDNASequenceSet(sequenceSet);
		assertEquals(getOnlyElement(otuSet.getDNASequenceSets()), sequenceSet);
		assertSame(sequenceSet.getParent(), otuSet);
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	@Test(groups = TestGroupDefs.SINGLE)
	public void addDNASequenceSetPos() {
		final IOTUSet otuSet = new OTUSet();
		final IDNASequenceSet sequenceSet0 = new DNASequenceSet(
				new DNASequences());
		final IDNASequenceSet sequenceSet1 = new DNASequenceSet(
				new DNASequences());
		final IDNASequenceSet sequenceSet2 = new DNASequenceSet(
				new DNASequences());
		final IDNASequenceSet sequenceSet3 = new DNASequenceSet(
				new DNASequences());

		otuSet.addDNASequenceSet(sequenceSet0);
		otuSet.addDNASequenceSet(sequenceSet1);
		otuSet.addDNASequenceSet(sequenceSet2);

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addDNASequenceSet(2, sequenceSet3);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertEquals(otuSet.getDNASequenceSets().size(), 4);
		assertTrue(otuSet.getDNASequenceSets().contains(sequenceSet3));

		assertEquals(otuSet.getDNASequenceSets(),
				ImmutableList.of(sequenceSet0, sequenceSet1, sequenceSet3,
						sequenceSet2));

		assertSame(sequenceSet3.getParent(), otuSet);
	}

	@Test
	public void addOTU() {
		final IOTUSet otuSet = new OTUSet();
		final StandardMatrix standardMatrix = new StandardMatrix(
				new StandardRows());
		otuSet.addStandardMatrix(standardMatrix);

		final DNAMatrix dnaMatrix = new DNAMatrix(new DNARows());
		otuSet.addDNAMatrix(dnaMatrix);

		final DNASequenceSet dnaSequenceSet = new DNASequenceSet(
				new DNASequences());
		otuSet.addDNASequenceSet(dnaSequenceSet);

		final OTU otu0 = new OTU().setLabel("otu-0");
		final OTU otu1 = new OTU().setLabel("otu-1");
		final OTU otu2 = new OTU().setLabel("otu-2");

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		otuSet.addOTU(otu0);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		otuSet.addOTU(otu1);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		otuSet.addOTU(otu2);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

		final List<OTU> otus012 = ImmutableList.of(otu0, otu1, otu2);
		final Set<OTU> otusSet012 = ImmutableSet.copyOf(otus012);

		assertEquals(otuSet.getOTUs(), otus012);

		assertEquals(
				standardMatrix
						.getOTUKeyedRows()
						.getValues()
						.keySet(),
				otusSet012);
		assertNull(standardMatrix.getRows().get(otu0));
		assertNull(standardMatrix.getRows().get(otu1));
		assertNull(standardMatrix.getRows().get(otu2));

		assertEquals(
				dnaMatrix.getOTUKeyedRows().getValues().keySet(),
				otusSet012);
		assertNull(dnaMatrix.getRows().get(otu0));
		assertNull(dnaMatrix.getRows().get(otu1));
		assertNull(dnaMatrix.getRows().get(otu2));

		assertEquals(
				dnaSequenceSet
						.getOTUKeyedSequences()
						.getValues()
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
	public void addOTUWAlreadyContainedOTU() {
		otuSet.setOTUs(ImmutableList.of(new OTU().setLabel("OTU-0")));
		otuSet.unsetInNeedOfNewVersion();

		otuSet.setOTUs(otuSet.getOTUs());
		assertFalse(otuSet.isInNeedOfNewVersion());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOTUWDuplicateLabel() {
		otus.add(new OTU().setLabel(otus.get(0).getLabel()));
		otuSet.setOTUs(newArrayList(otus));
	}

	@Test
	public void addStandardMatrix() {
		final StandardMatrix dnaMatrix = new StandardMatrix(new StandardRows());

		otuSet.unsetInNeedOfNewVersion();

		otuSet.addStandardMatrix(dnaMatrix);
		assertEquals(getOnlyElement(otuSet.getStandardMatrices()), dnaMatrix);
		assertSame(dnaMatrix.getParent(), otuSet);
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	@Test(groups = TestGroupDefs.SINGLE)
	public void addStandardMatrixPos() {
		final IOTUSet otuSet = new OTUSet();
		final IStandardMatrix matrix0 = new StandardMatrix(new StandardRows());
		final IStandardMatrix matrix1 = new StandardMatrix(new StandardRows());
		final IStandardMatrix matrix2 = new StandardMatrix(new StandardRows());
		final IStandardMatrix matrix3 = new StandardMatrix(new StandardRows());

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
		final IOTUSet otuSet = new OTUSet();
		final ITreeSet treeSet0 = new TreeSet();
		final ITreeSet treeSet1 = new TreeSet();
		final ITreeSet treeSet2 = new TreeSet();
		final ITreeSet treeSet3 = new TreeSet();

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

	@Test
	public void afterUnmarshal() {
		final IStudy study = new Study();
		otuSet.unsetInNeedOfNewVersion();
		otuSet.afterUnmarshal(null, study);
		assertSame(otuSet.getParent(), study);
		assertFalse(otuSet.isInNeedOfNewVersion());

	}

	@BeforeMethod
	public void beforeMethod() {
		otuSet = new OTUSet();
		otus = newArrayList();
		otus.add((IOTU) new OTU().setLabel("otu0").setPPodId());
		otus.add((IOTU) new OTU().setLabel("otu1").setPPodId());
		otus.add((IOTU) new OTU().setLabel("otu2").setPPodId());

		otuSet.setOTUs(newArrayList(otus));

		// Do this so we can check that version resets are being done.
		study = new Study();
		study.addOTUSet(otuSet);
	}

	/**
	 * Remove all OTUs
	 */
	@Test
	public void clearOTUs() {
		otuSet.setOTUs(newArrayList(otus));

		otuSet.setVersionInfo(new VersionInfo());
		study.setVersionInfo(new VersionInfo());
		final List<IOTU> removedOTUs = otuSet.setOTUs(new ArrayList<OTU>());

		assertEquals(removedOTUs, otus);
		assertTrue(otuSet.isInNeedOfNewVersion());
		// assertNull(otuSet.getPPodVersionInfo());
		assertTrue(study.isInNeedOfNewVersion());
		// assertNull(study.getPPodVersionInfo());
		assertEquals(otuSet.getOTUs().size(), 0);
	}

	@Test
	public void removeDNAMatrix() {

		final IDNAMatrix matrix0 = new DNAMatrix(new DNARows());
		otuSet.addDNAMatrix(matrix0);
		final IDNAMatrix matrix1 = new DNAMatrix(new DNARows());
		otuSet.addDNAMatrix(matrix1);
		final IDNAMatrix matrix2 = new DNAMatrix(new DNARows());
		otuSet.addDNAMatrix(matrix2);

		otuSet.unsetInNeedOfNewVersion();

		study.unsetInNeedOfNewVersion();

		otuSet.removeDNAMatrix(matrix1);

		assertTrue(otuSet.isInNeedOfNewVersion());
		assertNull(matrix1.getParent());

		assertEquals(
				otuSet.getDNAMatrices(),
				ImmutableList.of(matrix0, matrix2));
	}

	@Test
	public void removeDNASequenceSet() {

		final IOTUSet otuSet = new OTUSet();
		final IDNASequenceSet dnaSequenceSet0 = new DNASequenceSet(
				new DNASequences());
		otuSet.addDNASequenceSet(dnaSequenceSet0);
		final IDNASequenceSet dnaSequenceSet1 = new DNASequenceSet(
				new DNASequences());
		otuSet.addDNASequenceSet(dnaSequenceSet1);
		final IDNASequenceSet dnaSequenceSet2 = new DNASequenceSet(
				new DNASequences());
		otuSet.addDNASequenceSet(dnaSequenceSet2);

		otuSet.removeDNASequenceSet(dnaSequenceSet1);

		assertEquals(
				otuSet.getDNASequenceSets(),
				ImmutableList.of(dnaSequenceSet0, dnaSequenceSet2));
	}

	/**
	 * Remove an otu set and make sure it was removed, otuSet is marked for a
	 * new pPOD version, and the return value of removeOTUs contains the removed
	 * OTU.
	 */
	@Test
	public void removeOTU() {
		otuSet.unsetInNeedOfNewVersion();

		final ImmutableList<IOTU> otus2 =
				ImmutableList.of(otus.get(0), otus.get(2));

		final ImmutableList<IOTU> removedOTUs =
				ImmutableList.copyOf(otuSet.setOTUs(otus2));

		assertFalse(contains(otuSet.getOTUs(), otus.get(1)));
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertEquals(removedOTUs, newHashSet(otus.get(1)));
		assertNull(otus.get(1).getParent());
	}

	@Test
	public void removeStandardMatrix() {
		final IStandardMatrix matrix0 = new StandardMatrix(new StandardRows());
		otuSet.addStandardMatrix(matrix0);
		final IStandardMatrix matrix1 = new StandardMatrix(new StandardRows());
		otuSet.addStandardMatrix(matrix1);
		final IStandardMatrix matrix2 = new StandardMatrix(new StandardRows());
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
		final ITreeSet treeSet0 = new TreeSet();
		treeSet0.setLabel("treeSet0");
		otuSet.addTreeSet(treeSet0);
		final ITreeSet treeSet1 = new TreeSet();
		treeSet1.setLabel("treeSet1");
		otuSet.addTreeSet(treeSet1);
		final ITreeSet treeSet2 = new TreeSet();
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
		final IOTUSet otuSetWithNoStudy = new OTUSet();
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
