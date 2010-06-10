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
import static com.google.common.collect.Iterables.isEmpty;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link TreeSet}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST }, dependsOnGroups = TestGroupDefs.INIT)
public class TreeSetTest {

	@Inject
	private Provider<TreeSet> treeSetProvider;

	@Inject
	private Provider<Tree> treeProvider;

	@Test
	public void addTree() {
		final TreeSet treeSet = treeSetProvider.get();
		final Tree tree = treeProvider.get();

		assertFalse(treeSet.isInNeedOfNewVersion());
		final Tree returnedTree = treeSet.addTree(tree);
		assertTrue(treeSet.isInNeedOfNewVersion());
		assertSame(returnedTree, tree);
		assertSame(tree.getTreeSet(), treeSet);
		assertEquals(getOnlyElement(treeSet.getTrees()), tree);

		treeSet.unsetInNeedOfNewVersion();
		treeSet.addTree(tree);
		assertFalse(treeSet.isInNeedOfNewVersion());
		assertEquals(getOnlyElement(treeSet.getTrees()), tree);
	}

	@Test
	public void setTrees() {
		final TreeSet treeSet = treeSetProvider.get();

		assertFalse(treeSet.isInNeedOfNewVersion());

		final List<Tree> trees =
				ImmutableList.of(
						treeProvider.get(),
						treeProvider.get(),
						treeProvider.get());
		final List<Tree> returnedTrees = treeSet.setTrees(trees);

		assertTrue(treeSet.isInNeedOfNewVersion());

		assertTrue(isEmpty(returnedTrees));

		assertEquals(treeSet.getTrees(), trees);

		treeSet.unsetInNeedOfNewVersion();
		assertFalse(treeSet.isInNeedOfNewVersion());
		final List<Tree> returnedTrees2 = treeSet.setTrees(trees);
		assertTrue(isEmpty(returnedTrees2));
		assertFalse(treeSet.isInNeedOfNewVersion());
		assertEquals(treeSet.getTrees(), trees);

		treeSet.unsetInNeedOfNewVersion();

		final List<Tree> trees2 =
				ImmutableList.of(trees.get(1));

		final List<Tree> returnedTrees3 = treeSet.setTrees(trees2);
		assertTrue(treeSet.isInNeedOfNewVersion());
		assertEquals(returnedTrees3,
				ImmutableList.of(
						trees.get(0),
						trees.get(2)));
		assertEquals(treeSet.getTrees(), trees2);
		for (final Tree returnedTree3 : returnedTrees3) {
			assertNull(returnedTree3.getTreeSet());
		}
	}
}
