package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.ModelAssert;

/**
 * Tests {@link IMergeAttachment}s.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class MergeAttachmentTest {

	@Inject
	private IMergeAttachment.IFactory mergeAttachmentFactory;

	@Inject
	private TestAttachmentNamespaceDAO.IFactory attachmentNamespaceDAOFactory;

	private IAttachmentNamespaceDAO attachmentNamespaceDAO;

	@Inject
	private TestAttachmentTypeDAO attachmentTypeDAO;

	@Inject
	private Provider<Attachment> attachmentProvider;

	@Inject
	private Provider<AttachmentNamespace> attachmentNamespaceProvider;

	@Inject
	private Provider<AttachmentType> attachmentTypeProvider;

	@BeforeMethod
	void beforeMethod() {
		final Map<String, AttachmentNamespace> namespacesByLabel = newHashMap();
		namespacesByLabel.put("TEST_ATTACHMENT_NAMESPACE",
				attachmentNamespaceProvider.get());
		attachmentNamespaceDAO = attachmentNamespaceDAOFactory
				.create(namespacesByLabel);
	}

	public void mergeOnBlankTarget() {
		IMergeAttachment mergeAttachment = mergeAttachmentFactory.create(
				attachmentNamespaceDAO, attachmentTypeDAO);
		final Attachment targetAttachment = attachmentProvider.get(), sourceAttachment = attachmentProvider
				.get();
		sourceAttachment.setLabel("target attachment");
		sourceAttachment.setType(attachmentTypeProvider.get().setLabel(
				"SOURCE_ATTACHMENT_TYPE"));
		sourceAttachment.getType().setNamespace(
				attachmentNamespaceProvider.get().setLabel(
						"SOURCE_ATTACHMENT_NAMESPACE"));
		mergeAttachment.merge(targetAttachment, sourceAttachment);
		ModelAssert.assertEqualsAttachments(targetAttachment, sourceAttachment);
	}
}
