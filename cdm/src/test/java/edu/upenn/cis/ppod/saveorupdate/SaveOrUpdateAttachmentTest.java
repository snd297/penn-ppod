package edu.upenn.cis.ppod.saveorupdate;

import com.google.inject.Inject;

/**
 * Tests {@link IMergeAttachment}s.
 * 
 * @author Sam Donnelly
 */
//@Test(groups = { TestGroupDefs.FAST })
public class SaveOrUpdateAttachmentTest {

	@Inject
	private IMergeAttachment mergeAttachment;

	public void saveOrUpdate() {
		mergeAttachment.saveOrUpdate(null, null);
	}
}
