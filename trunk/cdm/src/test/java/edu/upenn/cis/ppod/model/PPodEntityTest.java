package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class PPodEntityTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<Attachment> attachmentProvider;

	public void addAttachment() {
		final OTUSet otuSet = otuSetProvider.get();
		final Attachment attachment = attachmentProvider.get();
		assertFalse(otuSet.getHasAttachments());
		otuSet.addAttachment(attachment);
		assertEquals(getOnlyElement(otuSet.getAttachments()), attachment);
		assertTrue(otuSet.getHasAttachments());
	}

	public void removeAttachment() {
		final OTUSet otuSet = otuSetProvider.get();
		final Attachment attachment1 = attachmentProvider.get();
		final Attachment attachment2 = attachmentProvider.get();
		final Attachment attachment3 = attachmentProvider.get();
		otuSet.addAttachment(attachment1);
		otuSet.addAttachment(attachment2);
		otuSet.addAttachment(attachment3);
		assertEquals(otuSet.getAttachments(), newHashSet(attachment1,
				attachment2, attachment3));
		otuSet.removeAttachment(attachment2);
		assertEquals(otuSet.getAttachments(), newHashSet(attachment1,
				attachment3));
		assertTrue(otuSet.getHasAttachments());

		otuSet.removeAttachment(attachment1);
		otuSet.removeAttachment(attachment3);
		assertEquals(otuSet.getAttachments(), Collections.emptySet());
		assertFalse(otuSet.getHasAttachments());
	}
}
