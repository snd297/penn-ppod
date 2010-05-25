package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class TreeTest {

	@Inject
	private Provider<TreeSet> treeSetProvider;

	@Inject
	private Provider<Tree> treeProvider;

	@Test
	public void setInNeedOfNewVersionForMotherlessTree() {
		final Tree tree = treeProvider.get();
		assertFalse(tree.isInNeedOfNewVersion());
		tree.setInNeedOfNewVersion();
		assertTrue(tree.isInNeedOfNewVersion());
	}

	@Test
	public void setInNeedOfNewVersionForMotheredTree() {
		final TreeSet treeSet = treeSetProvider.get();
		final Tree tree = treeProvider.get();
		treeSet.addTree(tree);
		tree.unsetInNeedOfNewVersion();
		treeSet.unsetInNeedOfNewVersion();

		tree.setInNeedOfNewVersion();

		assertTrue(tree.isInNeedOfNewVersion());
		assertTrue(treeSet.isInNeedOfNewVersion());
	}
}
