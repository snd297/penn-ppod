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
package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;

/**
 * @author Sam Donnelly
 */
public class MergeAttachment implements IMergeAttachment {

	private final IAttachmentNamespaceDAO attachmentNamespaceDAO;
	private final IAttachmentTypeDAO attachmentTypeDAO;

	private final Provider<AttachmentNamespace> attachmentNamespaceProvider;
	private final Provider<AttachmentType> attachmentTypeProvider;

	// Temporary measure: both the the type and namespace should be passed in
	// from caller (is that really true?)
	private final Map<String, AttachmentNamespace> namespacesByLabel = newHashMap();

	private final Map<AttachmentNamespace, Map<String, AttachmentType>> typesByNamespaceAndLabel = newHashMap();

	@Inject
	MergeAttachment(
			final Provider<AttachmentNamespace> attachmentNamespaceProvider,
			final Provider<AttachmentType> attachmentTypeProvider,
			@Assisted final IAttachmentNamespaceDAO attachmentNamespaceDAO,
			@Assisted final IAttachmentTypeDAO attachmentTypeDAO) {

		this.attachmentNamespaceDAO = attachmentNamespaceDAO;
		this.attachmentTypeDAO = attachmentTypeDAO;
		this.attachmentNamespaceProvider = attachmentNamespaceProvider;
		this.attachmentTypeProvider = attachmentTypeProvider;
	}

	public Attachment merge(final Attachment targetAttachment,
			final Attachment sourceAttachment) {
		checkNotNull(targetAttachment);
		checkNotNull(sourceAttachment);
		checkArgument(sourceAttachment.getType() != null,
				"sourceAttachment.getType() == null");
		checkArgument(sourceAttachment.getType().getNamespace() != null,
				"sourceAttachment.getType().getNamespace() == null");
		AttachmentNamespace dbAttachmentNamespace = namespacesByLabel
				.get(sourceAttachment.getType().getNamespace().getLabel());
		if (null == dbAttachmentNamespace) {
			dbAttachmentNamespace = attachmentNamespaceDAO
					.getNamespaceByLabel(sourceAttachment.getType()
							.getNamespace().getLabel());
			if (null == dbAttachmentNamespace) {
				dbAttachmentNamespace = attachmentNamespaceProvider.get()
						.setLabel(
								sourceAttachment.getType().getNamespace()
										.getLabel());
			}
			namespacesByLabel.put(dbAttachmentNamespace.getLabel(),
					dbAttachmentNamespace);
			typesByNamespaceAndLabel.put(dbAttachmentNamespace,
					new HashMap<String, AttachmentType>());
		}

		AttachmentType targetAttachmentType = typesByNamespaceAndLabel.get(
				dbAttachmentNamespace).get(
				sourceAttachment.getType().getLabel());
		if (null == targetAttachmentType) {
			targetAttachmentType = attachmentTypeDAO
					.getAttachmentTypeByNamespaceAndType(dbAttachmentNamespace
							.getLabel(), sourceAttachment.getType().getLabel());
			if (null == targetAttachmentType) {
				targetAttachmentType = attachmentTypeProvider.get().setLabel(
						sourceAttachment.getType().getLabel());
				targetAttachmentType.setNamespace(dbAttachmentNamespace);
			}
			typesByNamespaceAndLabel.get(dbAttachmentNamespace).put(
					targetAttachmentType.getLabel(), targetAttachmentType);
		}

		targetAttachment.setLabel(sourceAttachment.getLabel());
		targetAttachment.setStringValue(sourceAttachment.getStringValue());

		targetAttachment.setByteArrayValue(sourceAttachment.getBytesValue());
		targetAttachment.setType(targetAttachmentType);

		return targetAttachment;
	}
}
