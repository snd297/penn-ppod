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
package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.collect.Maps.newHashMap;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.imodel.IAttachmentType;
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

	@BeforeMethod
	public void beforeMethod() {
		final Map<String, IAttachmentNamespace> namespacesByLabel = newHashMap();
		namespacesByLabel.put(
				"TEST_ATTACHMENT_NAMESPACE",
				new AttachmentNamespace());
	}

	@Test
	public void mergeOnBlankTarget() {
		IMergeAttachments mergeAttachments = new MergeAttachments(
				mock(IAttachmentNamespaceDAO.class),
				mock(IAttachmentTypeDAO.class));

		final IAttachment targetAttachment = new Attachment(), sourceAttachment = new Attachment();
		sourceAttachment.setLabel("target attachment");
		final IAttachmentType sourceAttachmentType = new AttachmentType();

		sourceAttachmentType.setLabel("SOURCE_ATTACHMENT_TYPE");
		sourceAttachment.setType(sourceAttachmentType);

		final IAttachmentNamespace srcNamespace = new AttachmentNamespace();

		srcNamespace.setLabel("SOURCE_ATTACHMENT_NAMESPACE");

		sourceAttachment.getType().setNamespace(srcNamespace);

		sourceAttachment.setStringValue("STRING_VALUE");
		sourceAttachment.setBytesValue(new byte[] { 0, 1, 2 });
		mergeAttachments.mergeAttachments(targetAttachment, sourceAttachment);
		ModelAssert.assertEqualsAttachments(targetAttachment, sourceAttachment);
	}
}
