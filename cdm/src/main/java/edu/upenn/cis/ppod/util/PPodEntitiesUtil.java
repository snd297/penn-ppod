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
package edu.upenn.cis.ppod.util;

import java.util.Set;

import edu.upenn.cis.ppod.imodel.IAttachee;
import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.services.ppodentity.IOTUSetCentricEntities;

/**
 * @author Sam Donnelly
 */
public class PPodEntitiesUtil {
	/**
	 * Extract attachment information from {@code attachee} and put it into the
	 * attachment info sets.
	 * 
	 * @param attachee from which we're extracting the attachment info
	 * @param attachmentNamespaces to which to add the extracted attachment
	 *            namespaces
	 * @param attachmentTypes to which to add the extracted attachment types
	 * @param attachments to which to add the extracted attachments
	 * @return {@code attachee}
	 */
	public static IAttachee extractAttachmentInfoFromAttachee(
			final Set<AttachmentNamespace> attachmentNamespaces,
			final Set<AttachmentType> attachmentTypes,
			final IAttachee attachee) {
		for (final IAttachment attachment : attachee.getAttachments()) {
			attachmentNamespaces.add(attachment.getType().getNamespace());
			attachmentTypes.add(attachment.getType());
			extractAttachmentInfoFromAttachee(attachmentNamespaces,
					attachmentTypes, attachment);
		}
		return attachee;
	}

	public static IOTUSetCentricEntities extractAttachmentInfoFromPPodEntities(
			final Set<AttachmentNamespace> studyWideAttachmentNamespaces,
			final Set<AttachmentType> studyWideAttachmentTypes,
				final IOTUSetCentricEntities otuSetCentricEntities) {
		for (final IOTUSet otuSet : otuSetCentricEntities.getOTUSets()) {
			extractAttachmentInfoFromAttachee(studyWideAttachmentNamespaces,
					studyWideAttachmentTypes, otuSet);
			for (final IOTU otu : otuSet.getOTUs()) {
				extractAttachmentInfoFromAttachee(
						studyWideAttachmentNamespaces,
						studyWideAttachmentTypes, otu);
			}
			for (final StandardMatrix matrix : otuSet
					.getStandardMatrices()) {
				extractAttachmentInfoFromAttachee(
						studyWideAttachmentNamespaces,
						studyWideAttachmentTypes, matrix);
				for (final StandardCharacter standardCharacter : matrix
						.getCharacters()) {
					extractAttachmentInfoFromAttachee(
							studyWideAttachmentNamespaces,
							studyWideAttachmentTypes,
							standardCharacter);
				}
			}
		}

		return otuSetCentricEntities;
	}
}
