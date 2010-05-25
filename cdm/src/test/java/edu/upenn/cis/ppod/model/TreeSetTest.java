package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link TreeSet}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.SINGLE }, dependsOnGroups = TestGroupDefs.INIT)
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
}
