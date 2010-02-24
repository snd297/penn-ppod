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
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
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

	private Study study;

	@BeforeMethod
	public void beforeMethod() {
		otuSet = otuSetProvider.get();
		otus = newArrayList();
		otus.add((OTU) otuProvider.get().setLabel("otu0").setPPodId());
		otus.add((OTU) otuProvider.get().setLabel("otu1").setPPodId());
		otus.add((OTU) otuProvider.get().setLabel("otu2").setPPodId());

		otuSet.setOTUs(newHashSet(otus));

		// Do this so we can check that version resets are being done.
		study = studyProvider.get();
		study.addOTUSet(otuSet);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOTUWDuplicateLabel() {
		otus.add(otuProvider.get().setLabel(otus.get(0).getLabel()));
		otuSet.setOTUs(newHashSet(otus));
	}

	public void addTreeSet() {
		final TreeSet treeSet = treeSetProvider.get();
		otuSet.setTreeSets(newHashSet(treeSet));
		assertEquals(getOnlyElement(otuSet.getTreeSets()), treeSet);
	}

	/**
	 * Call {@code OTUSet.resetPPodVersionInfo()} when {@code
	 * OTUSet.getPPodVersionInfo() == null} and make sure that
	 * {@link OTUSet#getpPodVersionInfo()} stays {@code null} and that the OTU
	 * sets's {@code Study}'s pPOD Version info is unaffected.
	 */
	public void resetWhenNotInNeedOfNewPPodVersionInfo() {
		final PPodVersionInfo studyPPodVersionInfo = study.getpPodVersionInfo();
		otuSet.unsetInNeedOfNewPPodVersionInfo();
		otuSet.resetPPodVersionInfo();
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());
		assertEquals(study.getpPodVersionInfo(), studyPPodVersionInfo);
	}

	/**
	 * Add an otu that's already in an otu set into an otu set. The pPOD version
	 * should not be reset when this happens.
	 */
	public void addOTUWAlreadyContainedOTU() {
		otuSet.setOTUs(ImmutableSet.of(otuProvider.get().setLabel("OTU-0")));
		otuSet.unsetInNeedOfNewPPodVersionInfo();

		otuSet.setOTUs(otuSet.getOTUs());
		assertFalse(otuSet.isInNeedOfNewPPodVersionInfo());
	}

	/**
	 * Remove an otu set and make sure it was removed, otuSet is marked for a
	 * new pPOD version, and the return value of removeOTUs contains the removed
	 * OTU.
	 */
	public void removeOTU() {
		otuSet.unsetInNeedOfNewPPodVersionInfo();

		final ImmutableSet<OTU> otus2 = ImmutableSet.of(otus.get(0), otus
				.get(2));

		final Set<OTU> removedOTUs = otuSet.setOTUs(otus2);

		assertFalse(otuSet.getOTUs().contains(otus.get(1)));
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());
		assertEquals(removedOTUs, newHashSet(otus.get(1)));
	}

	/**
	 * Remove all OTUs
	 */
	public void clearOTUs() {
		otuSet.setOTUs(newHashSet(otus));

		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());
		study.setPPodVersionInfo(pPodVersionInfoProvider.get());
		final Set<OTU> removedOTUs = otuSet.setOTUs(new HashSet<OTU>());

		assertEquals(removedOTUs, newHashSet(otus));
		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());
		// assertNull(otuSet.getPPodVersionInfo());
		assertTrue(study.isInNeedOfNewPPodVersionInfo());
		// assertNull(study.getPPodVersionInfo());
		assertEquals(otuSet.getOTUs().size(), 0);
	}

	public void removeMatrix() {
		final CharacterStateMatrix matrix0 = matrixProvider.get();
		final CharacterStateMatrix matrix1 = matrixProvider.get();
		final CharacterStateMatrix matrix2 = matrixProvider.get();

		final Set<CharacterStateMatrix> matrices = newHashSet(matrix0, matrix2);

		Set<CharacterStateMatrix> otuSetMatrices = newHashSet();
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
		matrices.remove(matrix1);
		assertEquals((Object) otuSet.getMatrices(), (Object) matrices);
	}

	public void removeTreeSet() {
		final TreeSet treeSet0 = treeSetProvider.get();
		final TreeSet treeSet1 = treeSetProvider.get();
		final TreeSet treeSet2 = treeSetProvider.get();
		final ImmutableSet<TreeSet> treeSets = ImmutableSet.of(treeSet0,
				treeSet1, treeSet2);

		otuSet.setTreeSets(treeSets);

		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());

		final ImmutableSet<TreeSet> treeSetsMinusTreeSet1 = ImmutableSet.of(
				treeSet0, treeSet2);

		final Set<TreeSet> removedTreeSets = otuSet
				.setTreeSets(treeSetsMinusTreeSet1);

		assertEquals(removedTreeSets, newHashSet(treeSet1));

		assertTrue(otuSet.isInNeedOfNewPPodVersionInfo());

		assertEquals(otuSet.getTreeSets(), newHashSet(treeSet0, treeSet2));

		final ImmutableSet<TreeSet> removedTreeSets2 = ImmutableSet
				.copyOf(otuSet.setTreeSets(Collections.EMPTY_SET));

		assertEquals((Object) removedTreeSets2, (Object) treeSetsMinusTreeSet1);

		assertEquals(otuSet.getTreeSets(), Collections.emptySet());
	}
}
