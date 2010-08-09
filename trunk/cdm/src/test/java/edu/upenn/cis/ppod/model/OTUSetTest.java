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
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.util.TestVisitor;

/**
 * {@link OTUSet} test.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST },
		dependsOnGroups = TestGroupDefs.INIT)
public class OTUSetTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	private OTUSet otuSet;

	private List<IOTU> otus;

	@Inject
	private Provider<Study> studyProvider;

	@Inject
	private Provider<TreeSet> treeSetProvider;

	@Inject
	private Provider<VersionInfo> pPodVersionInfoProvider;

	@Inject
	private Provider<StandardMatrix> standardMatrixProvider;

	@Inject
	private Provider<DNASequenceSet> dnaSequenceSetProvider;

	@Inject
	private Provider<DNAMatrix> dnaMatrixProvider;

	@Inject
	private Provider<Attachment> attachmentProvider;

	private Study study;

	@Inject
	private Provider<TestVisitor> testVisitorProvider;

	@Test
	public void accept() {
		final OTUSet otuSet = otuSetProvider.get();

		otuSet.addOTU(otuProvider.get().setLabel("otu-0"));
		otuSet.addOTU(otuProvider.get().setLabel("otu-1"));
		otuSet.addOTU(otuProvider.get().setLabel("otu-2"));

		otuSet.addStandardMatrix(standardMatrixProvider.get());
		otuSet.addStandardMatrix(standardMatrixProvider.get());
		otuSet.addStandardMatrix(standardMatrixProvider.get());

		otuSet.addDNAMatrix(dnaMatrixProvider.get());
		otuSet.addDNAMatrix(dnaMatrixProvider.get());
		otuSet.addDNAMatrix(dnaMatrixProvider.get());

		otuSet.addDNASequenceSet(dnaSequenceSetProvider.get());
		otuSet.addDNASequenceSet(dnaSequenceSetProvider.get());
		otuSet.addDNASequenceSet(dnaSequenceSetProvider.get());

		otuSet.addTreeSet(treeSetProvider.get());
		otuSet.addTreeSet(treeSetProvider.get());
		otuSet.addTreeSet(treeSetProvider.get());

		otuSet.addAttachment(attachmentProvider.get());
		otuSet.addAttachment(attachmentProvider.get());
		otuSet.addAttachment(attachmentProvider.get());

		final TestVisitor visitor = testVisitorProvider.get();
		otuSet.accept(visitor);

		// Get rid of stuff we don't care about
		final Set<Object> visited = newHashSet();

		for (final Object visitedObject : visitor.getVisited()) {
			if (visitedObject.getClass() == OTUSet.class
					|| visitedObject.getClass() == OTU.class
					|| visitedObject.getClass() == StandardMatrix.class
					|| visitedObject.getClass() == DNAMatrix.class
					|| visitedObject.getClass() == DNASequenceSet.class
					|| visitedObject.getClass() == TreeSet.class
					|| visitedObject.getClass() == Attachment.class) {
				visited.add(visitedObject);
			}
		}

		// We make a set of the objects visited just to make it clear what we're
		// counting here.
		assertEquals(visited.size(),
				otuSet.getChildren().size()
						+ otuSet.getAttachments().size()
						+ ImmutableSet.of(otuSet).size());

		assertTrue(visited.contains(otuSet));

		for (final Object child : otuSet.getChildren()) {
			assertTrue(visited.contains(child));
		}

		for (final IAttachment attachment : otuSet.getAttachments()) {
			assertTrue(visited.contains(attachment));
		}
	}

	@Test
	public void addDNAMatrix() {
		final DNAMatrix dnaMatrix = dnaMatrixProvider.get();

		otuSet.unsetInNeedOfNewVersion();

		final IDNAMatrix dnaMatrixReturned = otuSet.addDNAMatrix(dnaMatrix);
		assertSame(dnaMatrixReturned, dnaMatrix);
		assertEquals(getOnlyElement(otuSet.getDNAMatrices()), dnaMatrix);
		assertSame(dnaMatrix.getParent(), otuSet);
		assertTrue(otuSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		otuSet.addDNAMatrix(dnaMatrix);

		assertEquals(getOnlyElement(otuSet.getDNAMatrices()), dnaMatrix);
		assertFalse(otuSet.isInNeedOfNewVersion());

	}

	@Test
	public void addOTU() {
		final OTUSet otuSet = otuSetProvider.get();
		final StandardMatrix standardMatrix = standardMatrixProvider.get();
		otuSet.addStandardMatrix(standardMatrix);

		final DNAMatrix dnaMatrix = dnaMatrixProvider.get();
		otuSet.addDNAMatrix(dnaMatrix);

		final DNASequenceSet dnaSequenceSet = dnaSequenceSetProvider.get();
		otuSet.addDNASequenceSet(dnaSequenceSet);

		final OTU otu0 = otuProvider.get().setLabel("otu-0");
		final OTU otu1 = otuProvider.get().setLabel("otu-1");
		final OTU otu2 = otuProvider.get().setLabel("otu-2");

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		final IOTU shouldBeOTU0 = otuSet.addOTU(otu0);
		assertSame(shouldBeOTU0, otu0);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		final IOTU shouldBeOTU1 = otuSet.addOTU(otu1);
		assertSame(shouldBeOTU1, otu1);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		standardMatrix.unsetInNeedOfNewVersion();
		dnaMatrix.unsetInNeedOfNewVersion();
		dnaSequenceSet.unsetInNeedOfNewVersion();
		final IOTU shouldBeOTU2 = otuSet.addOTU(otu2);
		assertSame(shouldBeOTU2, otu2);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(standardMatrix.isInNeedOfNewVersion());
		assertTrue(dnaMatrix.isInNeedOfNewVersion());
		assertTrue(dnaSequenceSet.isInNeedOfNewVersion());

		assertSame(shouldBeOTU0, otu0);
		assertSame(shouldBeOTU1, otu1);
		assertSame(shouldBeOTU2, otu2);

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
		otuSet.setOTUs(ImmutableList.of(otuProvider.get().setLabel("OTU-0")));
		otuSet.unsetInNeedOfNewVersion();

		otuSet.setOTUs(otuSet.getOTUs());
		assertFalse(otuSet.isInNeedOfNewVersion());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOTUWDuplicateLabel() {
		otus.add(otuProvider.get().setLabel(otus.get(0).getLabel()));
		otuSet.setOTUs(newArrayList(otus));
	}

	@Test
	public void afterUnmarshal() {
		final IStudy study = studyProvider.get();
		otuSet.unsetInNeedOfNewVersion();
		otuSet.afterUnmarshal(null, study);
		assertSame(otuSet.getParent(), study);
		assertFalse(otuSet.isInNeedOfNewVersion());

	}

	@BeforeMethod
	public void beforeMethod() {
		otuSet = otuSetProvider.get();
		otus = newArrayList();
		otus.add((IOTU) otuProvider.get().setLabel("otu0").setPPodId());
		otus.add((IOTU) otuProvider.get().setLabel("otu1").setPPodId());
		otus.add((IOTU) otuProvider.get().setLabel("otu2").setPPodId());

		otuSet.setOTUs(newArrayList(otus));

		// Do this so we can check that version resets are being done.
		study = studyProvider.get();
		study.addOTUSet(otuSet);
	}

	/**
	 * Remove all OTUs
	 */
	@Test
	public void clearOTUs() {
		otuSet.setOTUs(newArrayList(otus));

		otuSet.setVersionInfo(pPodVersionInfoProvider.get());
		study.setVersionInfo(pPodVersionInfoProvider.get());
		final List<IOTU> removedOTUs = otuSet.setOTUs(new ArrayList<OTU>());

		assertEquals(removedOTUs, otus);
		assertTrue(otuSet.isInNeedOfNewVersion());
		// assertNull(otuSet.getPPodVersionInfo());
		assertTrue(study.isInNeedOfNewVersion());
		// assertNull(study.getPPodVersionInfo());
		assertEquals(otuSet.getOTUs().size(), 0);
	}

	@Test
	public void removeDNASequenceSet() {

		final OTUSet otuSet = otuSetProvider.get();
		final IDNASequenceSet dnaSequenceSet0 =
				otuSet.addDNASequenceSet(dnaSequenceSetProvider.get());
		final IDNASequenceSet dnaSequenceSet1 =
				otuSet.addDNASequenceSet(dnaSequenceSetProvider.get());
		final IDNASequenceSet dnaSequenceSet2 =
				otuSet.addDNASequenceSet(dnaSequenceSetProvider.get());

		final boolean booleanReturned =
				otuSet.removeDNASequenceSet(dnaSequenceSet1);

		assertTrue(booleanReturned);

		assertEquals(
				otuSet.getDNASequenceSets(),
				ImmutableSet.of(dnaSequenceSet0, dnaSequenceSet2));

		otuSet.unsetInNeedOfNewVersion();

		final boolean booleanReturned2 =
				otuSet.removeDNASequenceSet(dnaSequenceSet1);
		assertFalse(booleanReturned2);
		assertFalse(otuSet.isInNeedOfNewVersion());
	}

	@Test
	public void removeDNAMatrix() {
		final IDNAMatrix matrix0 =
				otuSet.addDNAMatrix(dnaMatrixProvider.get());
		final IDNAMatrix matrix1 =
				otuSet.addDNAMatrix(dnaMatrixProvider.get());
		final IDNAMatrix matrix2 =
				otuSet.addDNAMatrix(dnaMatrixProvider.get());

		otuSet.unsetInNeedOfNewVersion();

		study.unsetInNeedOfNewVersion();

		boolean returnedBoolean = otuSet.removeDNAMatrix(matrix1);

		assertTrue(returnedBoolean);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertNull(matrix1.getParent());

		assertEquals(
				otuSet.getDNAMatrices(),
				ImmutableSet.of(matrix0, matrix2));

		otuSet.unsetInNeedOfNewVersion();

		boolean returnedBoolean2 = otuSet.removeDNAMatrix(matrix1);
		assertFalse(returnedBoolean2);
		assertFalse(otuSet.isInNeedOfNewVersion());
		assertNull(matrix1.getParent());
	}

	@Test
	public void removeStandardMatrix() {
		final IStandardMatrix matrix0 =
				otuSet.addStandardMatrix(standardMatrixProvider.get());
		final IStandardMatrix matrix1 =
				otuSet.addStandardMatrix(standardMatrixProvider.get());
		final IStandardMatrix matrix2 =
				otuSet.addStandardMatrix(standardMatrixProvider.get());

		otuSet.unsetInNeedOfNewVersion();

		study.unsetInNeedOfNewVersion();

		boolean returnedBoolean = otuSet.removeStandardMatrix(matrix1);

		assertTrue(returnedBoolean);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertNull(matrix1.getParent());

		assertEquals(
				otuSet.getStandardMatrices(),
				ImmutableSet.of(matrix0, matrix2));

		otuSet.unsetInNeedOfNewVersion();

		boolean returnedBoolean2 = otuSet.removeStandardMatrix(matrix1);
		assertFalse(returnedBoolean2);
		assertFalse(otuSet.isInNeedOfNewVersion());
		assertNull(matrix1.getParent());
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
	}

	@Test
	public void removeTreeSet() {
		final ITreeSet treeSet0 =
				otuSet.addTreeSet(treeSetProvider.get().setLabel("treeSet0"));
		final ITreeSet treeSet1 =
				otuSet.addTreeSet(treeSetProvider.get().setLabel("treeSet1"));
		final ITreeSet treeSet2 =
				otuSet.addTreeSet(treeSetProvider.get().setLabel("treeSet2"));

		otuSet.setVersionInfo(pPodVersionInfoProvider.get());

		final boolean returnedBoolean = otuSet.removeTreeSet(treeSet1);

		assertTrue(returnedBoolean);

		assertTrue(otuSet.isInNeedOfNewVersion());

		assertEquals(otuSet.getTreeSets(),
						ImmutableSet.of(treeSet0, treeSet2));

		otuSet.unsetInNeedOfNewVersion();

		final boolean returnedBoolean2 = otuSet.removeTreeSet(treeSet1);
		assertFalse(returnedBoolean2);
		assertFalse(otuSet.isInNeedOfNewVersion());

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
		final OTUSet otuSetWithNoStudy = otuSetProvider.get();
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
		final Study study = studyProvider.get();
		otuSet.setParent(study);
		assertSame(otuSet.getParent(), study);

		// Setting the study should have no affect
		assertFalse(otuSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();
		otuSet.setParent(null);
		assertNull(otuSet.getParent());
		assertFalse(otuSet.isInNeedOfNewVersion());
	}

	@Test
	public void addTreeSet() {
		final TreeSet treeSet0 = treeSetProvider.get();

		otuSet.unsetInNeedOfNewVersion();
		final ITreeSet returnedTreeSet0 = otuSet.addTreeSet(treeSet0);
		assertSame(returnedTreeSet0, treeSet0);
		assertTrue(otuSet.isInNeedOfNewVersion());

		otuSet.unsetInNeedOfNewVersion();

		final ITreeSet returnedTreeSet1 = otuSet.addTreeSet(treeSet0);
		assertSame(returnedTreeSet1, treeSet0);
		assertFalse(otuSet.isInNeedOfNewVersion());

	}
}
