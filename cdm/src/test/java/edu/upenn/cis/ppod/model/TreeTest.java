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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST)
public class TreeTest {

	@Test
	public void setInNeedOfNewVersionForParentedTree() {
		final TreeSet treeSet = new TreeSet();
		final Tree tree = new Tree();
		treeSet.addTree(tree);
		tree.unsetInNeedOfNewVersion();
		treeSet.unsetInNeedOfNewVersion();

		tree.setInNeedOfNewVersion();

		assertTrue(tree.isInNeedOfNewVersion());
		assertTrue(treeSet.isInNeedOfNewVersion());
	}

	@Test
	public void setInNeedOfNewVersionForParentlessTree() {
		final Tree tree = new Tree();
		assertFalse(tree.isInNeedOfNewVersion());
		tree.setInNeedOfNewVersion();
		assertTrue(tree.isInNeedOfNewVersion());
	}

	@Test
	public void setLabel() {
		final Tree tree = new Tree();
		tree.unsetInNeedOfNewVersion();
		final String label = "otu-label";
		tree.setLabel(label);
		assertTrue(tree.isInNeedOfNewVersion());
		tree.isInNeedOfNewVersion();
		assertEquals(tree.getLabel(), label);

		tree.unsetInNeedOfNewVersion();
		tree.setLabel(label);
		assertFalse(tree.isInNeedOfNewVersion());
		assertEquals(tree.getLabel(), label);
	}

	@Test
	public void setNewick() {
		final Tree tree = new Tree();
		final String newick = "arbitrary string";
		tree.unsetInNeedOfNewVersion();
		tree.setNewick(newick);
		assertEquals(tree.getNewick(), newick);
		assertTrue(tree.isInNeedOfNewVersion());

		tree.unsetInNeedOfNewVersion();
		tree.setNewick(newick);
		assertFalse(tree.isInNeedOfNewVersion());
		assertEquals(tree.getNewick(), newick);
	}
}
