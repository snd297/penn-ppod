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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.imodel.IAttachmentType;
import edu.upenn.cis.ppod.imodel.IOtuChangeCase;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.util.PPodEntitiesUtil;

/**
 * A container for serializing pPOD entities.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PPodEntities implements IPPodEntities {

	public PPodEntities() {}

	@XmlElement(name = "otuSet")
	private final List<IOTUSet> otuSets = newArrayList();

	@XmlElement(name = "otu")
	private final Set<IOtuChangeCase> otus = newHashSet();

	@XmlElement(name = "attachmentNamespace")
	private final Set<IAttachmentNamespace> pPodEntitiesWideAttachmentNamespaces = newHashSet();

	@XmlElement(name = "attachmentType")
	private final Set<IAttachmentType> pPodEntitiesWideAttachmentTypes = newHashSet();

	/** {@inheritDoc} */
	public void addOTUSet(final IOTUSet otuSet) {
		checkNotNull(otuSet);
		checkArgument(!getOTUSets().contains(otuSet),
				"this study already contains otu set [" + otuSet.getLabel()
						+ "]");
		otuSets.add(otuSet);
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
	protected boolean beforeMarshal(final Marshaller m) {
		if (pPodEntitiesWideAttachmentNamespaces.size() == 0) {
			PPodEntitiesUtil.extractAttachmentInfoFromPPodEntities(
					pPodEntitiesWideAttachmentNamespaces,
					pPodEntitiesWideAttachmentTypes,
					this);
		}
		return true;
	}

	public Set<IOtuChangeCase> getOTUs() {
		return Collections.unmodifiableSet(otus);
	}

	public IOtuChangeCase addOTU(final IOtuChangeCase otu) {
		otus.add(otu);
		return otu;
	}

	public List<IOTUSet> getOTUSets() {
		return Collections.unmodifiableList(otuSets);
	}

}
