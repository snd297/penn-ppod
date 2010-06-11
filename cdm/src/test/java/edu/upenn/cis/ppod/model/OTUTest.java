package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class OTUTest {

	@Inject
	private Provider<OTU> otuProvider;

	@Test
	public void setLabel() {
		final OTU otu = otuProvider.get();
		otu.unsetInNeedOfNewVersion();
		final String label = "otu-label";
		final OTU returnedOTU = otu.setLabel(label);
		assertTrue(otu.isInNeedOfNewVersion());
		assertSame(returnedOTU, otu);
		otu.isInNeedOfNewVersion();
		assertEquals(otu.getLabel(), label);

		otu.unsetInNeedOfNewVersion();
		otu.setLabel(label);
		assertFalse(otu.isInNeedOfNewVersion());
		assertEquals(otu.getLabel(), label);
	}

}
