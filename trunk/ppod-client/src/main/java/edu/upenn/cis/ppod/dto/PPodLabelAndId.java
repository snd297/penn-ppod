/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.dto;

import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class PPodLabelAndId {

	public static final Comparator<PPodLabelAndId> LABEL_COMPARATOR =
			new java.util.Comparator<PPodLabelAndId>() {

				public int compare(final PPodLabelAndId o1,
						final PPodLabelAndId o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			};

	@XmlAttribute(name = "pPodId")
	private String pPodId;

	@XmlAttribute
	private String label;

	@SuppressWarnings("unused")
	private PPodLabelAndId() {}

	public PPodLabelAndId(final String pPodId, final String label) {
		this.pPodId = pPodId;
		this.label = label;
	}

	public String getPPodId() {
		return pPodId;
	}

	public String getLabel() {
		return label;
	}
}
