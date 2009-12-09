package edu.upenn.cis.ppod.saveorupdate;

import static org.testng.Assert.assertTrue;

import com.google.inject.Inject;

/**
 * Tests {@link IMergeAttachment}s.
 * 
 * @author Sam Donnelly
 */
// @Test(groups = { TestGroupDefs.FAST })
public class MergeAttachmentTest {

	@Inject
	private IMergeAttachment.IFactory mergeAttachmentHibernateFactory;

	public void saveOrUpdate() {
		// mergeAttachment.saveOrUpdate(null, null);
		assertTrue(true);
	}
}
