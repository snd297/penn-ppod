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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.INIT)
public class OTUSetTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	private OTUSet otuSet;

	private List<OTU> otus;

	@Inject
	private Provider<Study> studyProvider;

	@Inject
	private Provider<TreeSet> treeSetProvider;

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	@Inject
	private Provider<DNASequenceSet> dnaSequenceSetProvider;

	private Study study;

	/**
	 * Add an otu that's already in an otu set into an otu set. The pPOD version
	 * should not be reset when this happens.
	 */
	public void addOTUWAlreadyContainedOTU() {
		otuSet.setOTUs(ImmutableList.of(otuProvider.get().setLabel("OTU-0")));
		otuSet.unsetInNeedOfNewPPodVersionInfo();

		otuSet.setOTUs(newArrayList(otuSet));
		assertFalse(otuSet.isInNeedOfNewPPodVersionInfo());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOTUWDuplicateLabel() {
		otus.add(otuProvider.get().setLabel(otus.get(0).getLabel()));
		otuSet.setOTUs(newArrayList(otus));
	}

	public void setTreeSets() {
		final TreeSet treeSet = treeSetProvider.get();
		final ImmutableSet<TreeSet> treeSets = ImmutableSet.of(treeSet);
		otuSet.setTreeSets(treeSets);
		assertEquals(Iterators.getOnlyElement(otuSet.getTreeSetsIterator()),
				treeSet);
	}

	@BeforeMethod
	public void beforeMethod() {
		otuSet = otuSetProvider.get();
		otus = newArrayList();
		otus.add((OTU) otuProvider.get().setLabel("otu0").setPPodId());
		otus.add((OTU) otuProvider.get().setLabel("otu1").setPPodId());
		otus.add((OTU) otuProvider.get().setLabel("otu2").setPPodId());

		otuSet.setOTUs(newArrayList(otus));

		// Do this so we can check that version resets are being done.
		study = studyProvider.get();
		study.addOTUSet(otuSet);
	}

	/**
	 * Remove all OTUs
	 */
	public void clearOTUs() {
		otuSet.setOTUs(newArrayList(otus));

		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());
		study.setPPodVersionInfo(pPodVersionInfoProvider.get());
		final List<OTU> removedOTUs = otuSet.setOTUs(new ArrayList<OTU>());

		assertEquals(removedOTUs, otus);
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());
		// assertNull(otuSet.getPPodVersionInfo());
		assertTrue(study.isInNeedOfNewPPodVersionInfo());
		// assertNull(study.getPPodVersionInfo());
		assertEquals(otuSet.getOTUsSize(), 0);
	}

	public void removeMatrix() {
		final CharacterStateMatrix matrix0 = matrixProvider.get();
		final CharacterStateMatrix matrix1 = matrixProvider.get();
		final CharacterStateMatrix matrix2 = matrixProvider.get();

		final Set<CharacterStateMatrix> otuSetMatrices = newHashSet();
		otuSetMatrices.add(matrix0);
		otuSetMatrices.add(matrix1);
		otuSetMatrices.add(matrix2);
		otuSet.setMatrices(otuSetMatrices);
		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());

		study.setPPodVersionInfo(pPodVersionInfoProvider.get());

		final ImmutableSet<CharacterStateMatrix> matricesMinusMatrix1 = ImmutableSet
				.of(matrix0, matrix2);
		otuSet.setMatrices(matricesMinusMatrix1);

		assertTrue(study.isInNeedOfNewPPodVersionInfo());
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());

		// assertNull(study.getPPodVersionInfo());
		// assertNull(otuSet.getPPodVersionInfo());
		assertEquals((Object) newHashSet(otuSet.getMatricesIterator()),
					(Object) newHashSet(matricesMinusMatrix1));
	}

	/**
	 * Remove an otu set and make sure it was removed, otuSet is marked for a
	 * new pPOD version, and the return value of removeOTUs contains the removed
	 * OTU.
	 */
	public void removeOTU() {
		otuSet.unsetInNeedOfNewPPodVersionInfo();

		final ImmutableList<OTU> otus2 = ImmutableList.of(otus.get(0), otus
				.get(2));

		final ImmutableList<OTU> removedOTUs = ImmutableList.copyOf(otuSet
				.setOTUs(otus2));

		assertFalse(contains(otuSet, otus.get(1)));
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());
		assertEquals(removedOTUs, newHashSet(otus.get(1)));
	}

	public void removeTreeSet() {
		final TreeSet treeSet0 = treeSetProvider.get().setLabel("treeSet0");
		final TreeSet treeSet1 = treeSetProvider.get().setLabel("treeSet1");
		final TreeSet treeSet2 = treeSetProvider.get().setLabel("treeSet2");
		final ImmutableSet<TreeSet> treeSets = ImmutableSet.of(treeSet0,
				treeSet1, treeSet2);

		otuSet.setTreeSets(treeSets);

		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());

		final ImmutableSet<TreeSet> treeSetsMinusTreeSet1 = ImmutableSet.of(
				treeSet0, treeSet2);

		final Set<TreeSet> removedTreeSets = otuSet
				.setTreeSets(treeSetsMinusTreeSet1);

		final ImmutableSet<TreeSet> treeSet1Set = ImmutableSet.of(treeSet1);

		assertEquals(removedTreeSets, treeSet1Set);

		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());

		assertEquals(newHashSet(otuSet.getTreeSetsIterator()), newHashSet(
				treeSet0,
				treeSet2));

		@SuppressWarnings("unchecked")
		final Set<TreeSet> removedTreeSets2 = otuSet
				.setTreeSets(Collections.EMPTY_SET);

		assertEquals((Object) removedTreeSets2,
				(Object) treeSetsMinusTreeSet1);

		assertEquals(newHashSet(otuSet.getTreeSetsIterator()), Collections
				.emptySet());
	}

	/**
	 * Call {@code OTUSet.resetPPodVersionInfo()} when {@code
	 * OTUSet.getPPodVersionInfo() == null} and make sure that
	 * {@link OTUSet#getpPodVersionInfo()} stays {@code null} and that the OTU
	 * sets's {@code Study}'s pPOD Version info is unaffected.
	 */
	public void resetWhenNotInNeedOfNewPPodVersionInfo() {
		final PPodVersionInfo studyPPodVersionInfo = study.getPPodVersionInfo();
		otuSet.unsetInNeedOfNewPPodVersionInfo();
		otuSet.setInNeedOfNewPPodVersionInfo();
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());
		assertEquals(study.getPPodVersionInfo(), studyPPodVersionInfo);
	}

	public void setDNASequenceSets() {
		final DNASequenceSet dnaSequenceSet0 = dnaSequenceSetProvider.get();
		final DNASequenceSet dnaSequenceSet1 = dnaSequenceSetProvider.get();
		final DNASequenceSet dnaSequenceSet2 = dnaSequenceSetProvider.get();
		final ImmutableSet<DNASequenceSet> dnaSequenceSets = ImmutableSet.of(
				dnaSequenceSet0, dnaSequenceSet1, dnaSequenceSet2);
		otuSet.setDNASequenceSets(dnaSequenceSets);

		assertEquals(otuSet.getDNASequenceSetsSize(), dnaSequenceSets.size());

		assertEquals((Object) newHashSet(otuSet.getDNASequenceSetsIterator()),
				(Object) dnaSequenceSets);

		otuSet.unsetInNeedOfNewPPodVersionInfo();

		final Set<DNASequenceSet> shouldBeEmpty = otuSet
				.setDNASequenceSets(dnaSequenceSets);

		assertEquals(shouldBeEmpty.size(), 0);

		assertFalse(otuSet.isInNeedOfNewPPodVersionInfo());

		final ImmutableSet<DNASequenceSet> dnaSequenceSets02 = ImmutableSet.of(
				dnaSequenceSet0, dnaSequenceSet2);

		final Set<DNASequenceSet> removedDNASequenceSets = otuSet
				.setDNASequenceSets(dnaSequenceSets02);

		assertEquals(removedDNASequenceSets, ImmutableSet.of(dnaSequenceSet1));

	}

	public void addOTU() {
		final OTU otu0 = otuProvider.get();
		final OTU shouldBeOTU0 = otuSet.addOTU(otu0);
		assertSame(shouldBeOTU0, otu0);
		assertTrue(contains(otuSet, otu0));
	}

	public void setDescription() {
		otuSet.unsetInNeedOfNewPPodVersionInfo();
		final String description = "DESCRIPTION";
		otuSet.setDescription(description);
		assertEquals(otuSet.getDescription(), description);
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());

		otuSet.unsetInNeedOfNewPPodVersionInfo();
		otuSet.setDescription(description);
		assertFalse(otuSet.isInNeedOfNewPPodVersionInfo());

		otuSet.unsetInNeedOfNewPPodVersionInfo();
		otuSet.setDescription(null);
		assertNull(otuSet.getDescription());
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());

		otuSet.unsetInNeedOfNewPPodVersionInfo();
		otuSet.setDescription(null);
		assertNull(otuSet.getDescription());
		assertFalse(otuSet.isInNeedOfNewPPodVersionInfo());
	}
}
