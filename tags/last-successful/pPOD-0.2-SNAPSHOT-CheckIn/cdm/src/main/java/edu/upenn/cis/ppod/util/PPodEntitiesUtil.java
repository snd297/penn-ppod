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

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Set;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.IAttachee;
import edu.upenn.cis.ppod.model.IUUPPodEntity;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.services.ppodentity.IPPodEntities;

/**
 * @author Sam Donnelly
 * 
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
			final Set<Attachment> attachments, final IAttachee attachee) {
		for (final Attachment attachment : attachee.getAttachments()) {
			attachmentNamespaces.add(attachment.getType().getNamespace());
			attachmentTypes.add(attachment.getType());
			attachments.add(attachment);
			extractAttachmentInfoFromAttachee(attachmentNamespaces,
					attachmentTypes, attachments, attachment);
		}
		return attachee;
	}

	public static IPPodEntities extractAttachmentInfoFromPPodEntities(
			final Set<AttachmentNamespace> studyWideAttachmentNamespaces,
			final Set<AttachmentType> studyWideAttachmentTypes,
			final Set<Attachment> studyWideAttachments,
			final IPPodEntities pPodEntities) {
		for (final OTUSet otuSet : pPodEntities.getOTUSets()) {
			extractAttachmentInfoFromAttachee(studyWideAttachmentNamespaces,
					studyWideAttachmentTypes, studyWideAttachments, otuSet);
			for (final OTU otu : otuSet.getOTUs()) {
				extractAttachmentInfoFromAttachee(
						studyWideAttachmentNamespaces,
						studyWideAttachmentTypes, studyWideAttachments, otu);
			}
			for (final CharacterStateMatrix matrix : otuSet.getMatrices()) {
				extractAttachmentInfoFromAttachee(
						studyWideAttachmentNamespaces,
						studyWideAttachmentTypes, studyWideAttachments, matrix);
				for (final Character character : matrix.getCharacters()) {
					extractAttachmentInfoFromAttachee(
							studyWideAttachmentNamespaces,
							studyWideAttachmentTypes, studyWideAttachments,
							character);
				}
			}
		}

		return pPodEntities;
	}

	public static IUUPPodEntity getUUPPodEntityByPPodId(
			final Iterable<? extends IUUPPodEntity> uuPPodEntities,
			final String pPodId) {
		if (pPodId == null) {
			return null;
		} else {
			return findIf(uuPPodEntities, compose(equalTo(pPodId),
					IUUPPodEntity.getPPodId));
		}
	}

}