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
package edu.upenn.cis.ppod.services.ppodentity;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.util.PPodEntitiesUtil;

/**
 * A container for serializing pPOD entities.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PPodEntities implements IPPodEntities {

	@XmlElement(name = "otuSet")
	private final Set<OTUSet> otuSets = newHashSet();

	@XmlElement(name = "otu")
	private final Set<OTU> otus = newHashSet();

	@XmlElement(name = "pPodEntitiesWideAttachmentNamespace")
	private final Set<AttachmentNamespace> pPodEntitiesWideAttachmentNamespaces = newHashSet();

	@XmlElement(name = "pPodEntitiesWideAttachmentType")
	private final Set<AttachmentType> pPodEntitiesWideAttachmentTypes = newHashSet();

	@XmlElement(name = "pPodEntitiesWideAttachment")
	private final Set<Attachment> pPodEntitiesWideAttachment = newHashSet();

	@XmlElement(name = "studyWideCharacter")
	private final Set<Character> studyWideCharacters = newHashSet();

	public OTUSet addOTUSet(final OTUSet otuSet) {
		otuSets.add(otuSet);
		return otuSet;
	}

	/**
	 * We gather up all of the attachment info of this object's OTU sets,
	 * matrices, and trees and add them to the {@code studyWide*}s.
	 * 
	 * See {@link Marshaller}.
	 * 
	 * @param m see {@code Marshaller}
	 * @return see {@code Marshaller}
	 */
	public boolean beforeMarshal(final Marshaller m) {
		if (pPodEntitiesWideAttachmentNamespaces.size() == 0) {
			PPodEntitiesUtil.extractAttachmentInfoFromPPodEntities(
					pPodEntitiesWideAttachmentNamespaces,
					pPodEntitiesWideAttachmentTypes,
					pPodEntitiesWideAttachment, this);
			for (final Iterator<OTUSet> otuSetsItr = getOTUSetsIterator(); otuSetsItr
					.hasNext();) {
				final OTUSet otuSet = otuSetsItr.next();
				for (final Iterator<StandardMatrix> matrixItr = otuSet
						.getStandardMatricesIterator(); matrixItr.hasNext();) {
					studyWideCharacters
							.addAll(newArrayList(matrixItr.next()
									.charactersIterator()));
				}
			}
		}
		return true;
	}

	public Set<OTU> getOTUs() {
		return Collections.unmodifiableSet(otus);
	}

	public OTU addOTU(final OTU otu) {
		otus.add(otu);
		return otu;
	}

	public Iterator<OTUSet> getOTUSetsIterator() {
		return otuSets.iterator();
	}

}
