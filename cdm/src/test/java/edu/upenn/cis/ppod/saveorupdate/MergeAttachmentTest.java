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
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * Tests {@link IMergeAttachments}s.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class MergeAttachmentTest {

	@Inject
	private IMergeAttachments.IFactory mergeAttachmentFactory;

	@Inject
	private Provider<TestAttachmentNamespaceDAO> attachmentNamespaceDAOProvider;

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
		attachmentNamespaceDAO = attachmentNamespaceDAOProvider.get()
				.setNamespacesByLabel(namespacesByLabel);
	}

	public void mergeOnBlankTarget() {
		System.out.println("entering...mergeOnBlankTarget");
		IMergeAttachments mergeAttachments = mergeAttachmentFactory.create(
				attachmentNamespaceDAO, attachmentTypeDAO);
		final Attachment targetAttachment = attachmentProvider.get(), sourceAttachment = attachmentProvider
				.get();
		sourceAttachment.setLabel("target attachment");
		sourceAttachment.setType(attachmentTypeProvider.get().setLabel(
				"SOURCE_ATTACHMENT_TYPE"));
		sourceAttachment.getType().setNamespace(
				attachmentNamespaceProvider.get().setLabel(
						"SOURCE_ATTACHMENT_NAMESPACE"));
		sourceAttachment.setStringValue("STRING_VALUE");
		sourceAttachment.setByteArrayValue(new byte[] { 0, 1, 2 });
		mergeAttachments.merge(targetAttachment, sourceAttachment);
		ModelAssert.assertEqualsAttachments(targetAttachment, sourceAttachment);
	}
}
