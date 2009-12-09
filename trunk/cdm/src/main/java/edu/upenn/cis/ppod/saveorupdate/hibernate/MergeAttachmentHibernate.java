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
package edu.upenn.cis.ppod.saveorupdate.hibernate;

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
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachment;

/**
 * @author Sam Donnelly
 */
public class MergeAttachmentHibernate implements IMergeAttachment {

	private final IAttachmentNamespaceDAO attachmentNamespaceDAO;
	private final IAttachmentTypeDAO attachmentTypeDAO;

	private final Provider<AttachmentNamespace> attachmentNamespaceProvider;
	private final Provider<AttachmentType> attachmentTypeProvider;

	// Temporary measure: both the the type and namespace should be passed in
	// from caller (is that really true?)
	private final Map<String, AttachmentNamespace> namespacesByLabel = newHashMap();

	private final Map<AttachmentNamespace, Map<String, AttachmentType>> typesByNamespaceAndLabel = newHashMap();

	@Inject
	MergeAttachmentHibernate(
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

		AttachmentType dbAttachmentType = typesByNamespaceAndLabel.get(
				dbAttachmentNamespace).get(
				sourceAttachment.getType().getLabel());
		if (null == dbAttachmentType) {
			dbAttachmentType = attachmentTypeDAO
					.getAttachmentTypeByNamespaceAndType(dbAttachmentNamespace
							.getLabel(), sourceAttachment.getType().getLabel());
			if (null == dbAttachmentType) {
				dbAttachmentType = attachmentTypeProvider.get().setLabel(
						sourceAttachment.getType().getLabel());
				dbAttachmentType.setNamespace(dbAttachmentNamespace);
			}
			typesByNamespaceAndLabel.get(dbAttachmentNamespace).put(
					dbAttachmentType.getLabel(), dbAttachmentType);
		}

		targetAttachment.setLabel(sourceAttachment.getLabel());
		targetAttachment.setStringValue(sourceAttachment.getStringValue());
		targetAttachment.setType(dbAttachmentType);

		return targetAttachment;
	}
}
