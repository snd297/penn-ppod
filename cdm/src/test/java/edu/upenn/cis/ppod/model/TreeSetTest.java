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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IOtuSet;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Test {@link TreeSet}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class TreeSetTest {

	@Test
	public void addTree() {
		final ITreeSet treeSet = new TreeSet();
		final ITree tree = new Tree();

		assertFalse(treeSet.isInNeedOfNewVersion());
		treeSet.addTree(tree);
		assertTrue(treeSet.isInNeedOfNewVersion());
		assertSame(tree.getParent(), treeSet);
		assertEquals(getOnlyElement(treeSet.getTrees()), tree);
	}

	@Test
	public void setTrees() {
		final ITreeSet treeSet = new TreeSet();

		assertFalse(treeSet.isInNeedOfNewVersion());

		final List<Tree> trees =
				ImmutableList.of(new Tree(),
						new Tree(),
						new Tree());

		final List<ITree> returnedTrees = treeSet.setTrees(trees);

		assertTrue(treeSet.isInNeedOfNewVersion());

		assertTrue(isEmpty(returnedTrees));

		assertEquals(treeSet.getTrees(), trees);

		treeSet.unsetInNeedOfNewVersion();
		assertFalse(treeSet.isInNeedOfNewVersion());
		final List<ITree> returnedTrees2 = treeSet.setTrees(trees);
		assertTrue(isEmpty(returnedTrees2));
		assertFalse(treeSet.isInNeedOfNewVersion());
		assertEquals(treeSet.getTrees(), trees);

		treeSet.unsetInNeedOfNewVersion();

		final List<Tree> trees2 =
				ImmutableList.of(trees.get(1));

		final List<ITree> returnedTrees3 = treeSet.setTrees(trees2);
		assertTrue(treeSet.isInNeedOfNewVersion());
		assertEquals(returnedTrees3,
				ImmutableList.of(
						trees.get(0),
						trees.get(2)));
		assertEquals(treeSet.getTrees(), trees2);
		for (final ITree returnedTree3 : returnedTrees3) {
			assertNull(returnedTree3.getParent());
		}
	}

	/**
	 * Verify that the owning OTUSet gets marked as in need of a new version too
	 * and that it works too if there is no parent OTUSet.
	 */
	@Test
	public void setInNeedOfNewVersionInfo() {

		final ITreeSet treeSet = new TreeSet();
		treeSet.unsetInNeedOfNewVersion();

		treeSet.setInNeedOfNewVersion();

		assertTrue(treeSet.isInNeedOfNewVersion());

		final IOtuSet otuSet = new OtuSetChangeSet();
		otuSet.addTreeSet(treeSet);
		otuSet.unsetInNeedOfNewVersion();
		treeSet.unsetInNeedOfNewVersion();

		treeSet.setInNeedOfNewVersion();

		assertTrue(treeSet.isInNeedOfNewVersion());
		assertTrue(otuSet.isInNeedOfNewVersion());

	}

	@Test
	public void setLabel() {
		final ITreeSet treeSet = new TreeSet();
		treeSet.unsetInNeedOfNewVersion();
		final String otuSetLabel = "otu-set-label";
		treeSet.setLabel(otuSetLabel);
		assertTrue(treeSet.isInNeedOfNewVersion());
		treeSet.isInNeedOfNewVersion();
		assertEquals(treeSet.getLabel(), otuSetLabel);

		treeSet.unsetInNeedOfNewVersion();
		treeSet.setLabel(otuSetLabel);
		assertFalse(treeSet.isInNeedOfNewVersion());
		assertEquals(treeSet.getLabel(), otuSetLabel);
	}

	@Test
	public void accept() {
		final ITreeSet treeSet = new TreeSet();
		treeSet.addTree(new Tree());
		treeSet.addTree(new Tree());
		treeSet.addTree(new Tree());

		final IAttachment a0 = new Attachment();
		final IAttachment a1 = new Attachment();
		final IAttachment a2 = new Attachment();

		treeSet.addAttachment(a0);
		treeSet.addAttachment(a1);
		treeSet.addAttachment(a2);

		final IVisitor visitor = mock(IVisitor.class);

		treeSet.accept(visitor);

		verify(visitor, times(treeSet.getTrees().size())).visitTree(
				any(Tree.class));
		verify(visitor, times(1)).visitTree(treeSet.getTrees().get(0));
		verify(visitor, times(1)).visitTree(treeSet.getTrees().get(1));
		verify(visitor, times(1)).visitTree(treeSet.getTrees().get(2));

		verify(visitor, times(treeSet.getAttachments().size()))
				.visitAttachment(
						any(Attachment.class));
		verify(visitor, times(1)).visitAttachment(a0);
		verify(visitor, times(1)).visitAttachment(a1);
		verify(visitor, times(1)).visitAttachment(a2);
	}
}
