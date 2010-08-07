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
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.util.TestVisitor;

/**
 * Test {@link TreeSet}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST }, dependsOnGroups = TestGroupDefs.INIT)
public class TreeSetTest {

	@Inject
	private Provider<ITreeSet> treeSetProvider;

	@Inject
	private Provider<ITree> treeProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<Attachment> attachmentProvider;

	@Test
	public void addTree() {
		final ITreeSet treeSet = treeSetProvider.get();
		final ITree tree = treeProvider.get();

		assertFalse(treeSet.isInNeedOfNewVersion());
		final ITree returnedTree = treeSet.addTree(tree);
		assertTrue(treeSet.isInNeedOfNewVersion());
		assertSame(returnedTree, tree);
		assertSame(tree.getParent(), treeSet);
		assertEquals(getOnlyElement(treeSet.getTrees()), tree);
	}

	@Test
	public void setTrees() {
		final ITreeSet treeSet = treeSetProvider.get();

		assertFalse(treeSet.isInNeedOfNewVersion());

		final List<ITree> trees =
				ImmutableList.of(
						treeProvider.get(),
						treeProvider.get(),
						treeProvider.get());
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

		final List<ITree> trees2 =
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

		final ITreeSet treeSet = treeSetProvider.get();
		treeSet.unsetInNeedOfNewVersion();

		treeSet.setInNeedOfNewVersion();

		assertTrue(treeSet.isInNeedOfNewVersion());

		final OTUSet otuSet = otuSetProvider.get();
		otuSet.addTreeSet(treeSet);
		otuSet.unsetInNeedOfNewVersion();
		treeSet.unsetInNeedOfNewVersion();

		treeSet.setInNeedOfNewVersion();

		assertTrue(treeSet.isInNeedOfNewVersion());
		assertTrue(otuSet.isInNeedOfNewVersion());

	}

	@Test
	public void setLabel() {
		final ITreeSet treeSet = treeSetProvider.get();
		treeSet.unsetInNeedOfNewVersion();
		final String otuSetLabel = "otu-set-label";
		final ITreeSet returnedTreeSet = treeSet.setLabel(otuSetLabel);
		assertTrue(treeSet.isInNeedOfNewVersion());
		assertSame(returnedTreeSet, treeSet);
		treeSet.isInNeedOfNewVersion();
		assertEquals(treeSet.getLabel(), otuSetLabel);

		treeSet.unsetInNeedOfNewVersion();
		treeSet.setLabel(otuSetLabel);
		assertFalse(treeSet.isInNeedOfNewVersion());
		assertEquals(treeSet.getLabel(), otuSetLabel);
	}

	@Inject
	private TestVisitor visitor;

	@Test
	public void accept() {
		final ITreeSet treeSet = treeSetProvider.get();
		treeSet.addTree(treeProvider.get());
		treeSet.addTree(treeProvider.get());
		treeSet.addTree(treeProvider.get());

		treeSet.addAttachment(attachmentProvider.get());
		treeSet.addAttachment(attachmentProvider.get());
		treeSet.addAttachment(attachmentProvider.get());

		treeSet.accept(visitor);

		assertEquals(visitor.getVisited().size(),
				ImmutableSet.of(treeSet).size()
						+ treeSet.getTrees().size()
						+ treeSet.getAttachments().size());

		for (final ITree tree : treeSet.getTrees()) {
			assertTrue(visitor.getVisited().contains(tree));
		}

		for (final IAttachment attachment : treeSet.getAttachments()) {
			assertTrue(visitor.getVisited().contains(attachment));
		}

	}
}
