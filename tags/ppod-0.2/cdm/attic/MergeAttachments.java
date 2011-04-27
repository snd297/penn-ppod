/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;

/**
 * @author Sam Donnelly
 */
public final class MergeAttachments {

	private final IAttachmentNamespaceDAO attachmentNamespaceDAO;
	private final IAttachmentTypeDAO attachmentTypeDAO;

	// Temporary measure: both the the type and namespace should be passed in
	// from caller (is that really true?)
	private final Map<String, AttachmentNamespace> labelsToNamespaces = newHashMap();

	private final Map<AttachmentNamespace, Map<String, AttachmentType>> typesByNamespaceAndLabel =
			newHashMap();

	@Inject
	MergeAttachments(
			final IAttachmentNamespaceDAO attachmentNamespaceDAO,
			final IAttachmentTypeDAO attachmentTypeDAO) {
		this.attachmentNamespaceDAO = attachmentNamespaceDAO;
		this.attachmentTypeDAO = attachmentTypeDAO;
	}

	public void mergeAttachments(
			final Attachment targetAttachment,
			final Attachment sourceAttachment) {
		checkNotNull(targetAttachment);
		checkNotNull(sourceAttachment);
		checkArgument(sourceAttachment.getType() != null,
				"sourceAttachment.getType() == null");
		checkArgument(sourceAttachment.getType().getLabel() != null,
				"sourceAttachment.getType().getLabel() == null");
		checkArgument(sourceAttachment.getType().getNamespace() != null,
				"sourceAttachment.getType().getNamespace() == null");
		checkArgument(
				sourceAttachment.getType().getNamespace().getLabel() != null,
				"sourceAttachment.getType().getNamespace().getLabel() == null");

		AttachmentNamespace targetAttachmentNamespace =
				labelsToNamespaces
						.get(sourceAttachment
								.getType()
								.getNamespace()
								.getLabel());
		if (null == targetAttachmentNamespace) {
			targetAttachmentNamespace = attachmentNamespaceDAO
					.getNamespaceByLabel(
							sourceAttachment.getType()
									.getNamespace().getLabel());
			if (null == targetAttachmentNamespace) {
				targetAttachmentNamespace =
						new AttachmentNamespace();

				targetAttachmentNamespace
								.setLabel(
										sourceAttachment.getType()
												.getNamespace()
												.getLabel());
				attachmentNamespaceDAO
						.makePersistent(targetAttachmentNamespace);
			}
			labelsToNamespaces.put(
					targetAttachmentNamespace.getLabel(),
					targetAttachmentNamespace);
			typesByNamespaceAndLabel
					.put(
							targetAttachmentNamespace,
							new HashMap<String, AttachmentType>());
		}

		AttachmentType targetAttachmentType =
				typesByNamespaceAndLabel
						.get(targetAttachmentNamespace)
						.get(sourceAttachment.getType().getLabel());
		if (null == targetAttachmentType) {
			targetAttachmentType =
					attachmentTypeDAO.getTypeByNamespaceAndLabel(
							targetAttachmentNamespace.getLabel(),
							sourceAttachment.getType().getLabel());
			if (null == targetAttachmentType) {
				targetAttachmentType = new AttachmentType();
				targetAttachmentType
						.setLabel(sourceAttachment
								.getType()
								.getLabel());
				targetAttachmentType.setNamespace(targetAttachmentNamespace);
				attachmentTypeDAO.makePersistent(targetAttachmentType);
			}
			typesByNamespaceAndLabel.get(targetAttachmentNamespace)
					.put(targetAttachmentType
							.getLabel(),
							targetAttachmentType);
		}

		targetAttachment.setLabel(sourceAttachment.getLabel());
		targetAttachment.setStringValue(sourceAttachment.getStringValue());

		targetAttachment.setBytesValue(sourceAttachment.getBytesValue());
		targetAttachment.setType(targetAttachmentType);
	}
}
