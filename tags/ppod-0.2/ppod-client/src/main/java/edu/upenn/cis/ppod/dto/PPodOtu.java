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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodOtu extends UuPPodDomainObjectWithLabel {

	@XmlID
	@XmlAttribute
	private String docId = UUID.randomUUID().toString();

	/** For JAXB. */
	PPodOtu() {}

	public PPodOtu(final String label) {
		super(label);
	}

	public PPodOtu(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	public PPodOtu(@CheckForNull final String pPodId, final String label,
			final String docId) {
		super(pPodId, label);
		this.docId = checkNotNull(docId);
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(final String docId) {
		this.docId = checkNotNull(docId);
	}
}
