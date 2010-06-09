package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 * 
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class StudyTest {
	@Inject
	private Provider<Study> studyProvider;

	@Test
	public void setLabel() {
		final Study study = studyProvider.get();
		study.unsetInNeedOfNewVersion();
		final String label = "otu-set-label";
		final Study returnedStudy = study.setLabel(label);
		assertTrue(study.isInNeedOfNewVersion());
		assertSame(returnedStudy, study);
		study.isInNeedOfNewVersion();

		assertEquals(study.getLabel(), label);

		study.unsetInNeedOfNewVersion();
		study.setLabel(label);

		assertFalse(study.isInNeedOfNewVersion());
		assertEquals(study.getLabel(), label);
	}
}
