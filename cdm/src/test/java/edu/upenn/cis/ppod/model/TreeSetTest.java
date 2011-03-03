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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link TreeSet}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class TreeSetTest {

	@Test
	public void addTree() {
		final TreeSet treeSet = new TreeSet();
		final Tree tree = new Tree();

		treeSet.addTree(tree);

		assertSame(tree.getParent(), treeSet);
		assertEquals(getOnlyElement(treeSet.getTrees()), tree);
	}

	@Test
	public void setTrees() {
		final TreeSet treeSet = new TreeSet();

		final List<Tree> trees =
				ImmutableList.of(new Tree(),
						new Tree(),
						new Tree());

		treeSet.setTrees(trees);

		assertEquals(treeSet.getTrees(), trees);

		treeSet.setTrees(trees);

		assertEquals(treeSet.getTrees(), trees);

		final List<Tree> trees2 =
				ImmutableList.of(trees.get(1));

		treeSet.setTrees(trees2);

		assertEquals(treeSet.getTrees(), trees2);

		assertNull(trees.get(0).getParent());
		assertNull(trees.get(2).getParent());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setTreesWDup() {
		final TreeSet treeSet = new TreeSet();
		final Tree tree0 = new Tree();
		final Tree tree1 = new Tree();

		treeSet.setTrees(ImmutableList.of(tree0, tree1, tree1));
	}
}
