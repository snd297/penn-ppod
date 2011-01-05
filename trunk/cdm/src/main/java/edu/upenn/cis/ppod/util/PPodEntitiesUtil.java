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
import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.imodel.IAttachmentType;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.services.ppodentity.IOTUSets;

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
	 */
	public static void extractAttachmentInfoFromAttachee(
			final Set<? super IAttachmentNamespace> attachmentNamespaces,
			final Set<? super IAttachmentType> attachmentTypes,
			final IAttachee attachee) {
		for (final IAttachment attachment : attachee.getAttachments()) {
			attachmentNamespaces.add(attachment.getType().getNamespace());
			attachmentTypes.add(attachment.getType());
			extractAttachmentInfoFromAttachee(attachmentNamespaces,
					attachmentTypes, attachment);
		}
	}

	public static void extractAttachmentInfoFromPPodEntities(
			final Set<? super IAttachmentNamespace> studyWideAttachmentNamespaces,
			final Set<? super IAttachmentType> studyWideAttachmentTypes,
			final IOTUSets otuSetCentricEntities) {
		for (final OtuSet otuSet : otuSetCentricEntities.getOTUSets()) {
			extractAttachmentInfoFromAttachee(
					studyWideAttachmentNamespaces,
					studyWideAttachmentTypes,
					otuSet);
			for (final Otu otu : otuSet.getOtus()) {
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
	}
}
