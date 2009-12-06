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

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.OTUSet;

/**
 * @author Sam Donnelly
 * 
 */
public class XmlStudy {
	@XmlAttribute
	private String label;

	@XmlElement(name = "otuSet")
	private List<OTUSet> otuSets = newArrayList();

	@XmlElement(name = "studyWideAttachment")
	private final Set<Attachment> studyWideAttachments = newHashSet();

	@XmlElement(name = "studyWideAttachmentType")
	private final Set<AttachmentType> studyWideAttachmentTypes = newHashSet();

	@XmlElement(name = "studyWideAttachmentNamespace")
	private final Set<AttachmentNamespace> studyWideAttachmentNamespaces = newHashSet();
}
