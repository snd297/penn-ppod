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
package edu.upenn.cis.ppod.model;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import edu.upenn.cis.ppod.imodel.IHasWormDocId;

/**
 * A {@code PPodEntity} w/ an {@link XmlID} attribute called {@code "docId"}.
 * 
 * @author Sam Donnelly
 */
public abstract class PPodEntityWDocId
		extends PPodEntity
		implements IHasWormDocId {

	/**
	 * Intended for referencing elements within a document - be it XML, JSON,
	 * etc. This is distinct from the pPOD Id of {@link UUPPodEntity}.
	 */
	@Nullable
	private String docId;

	/** {@inheritDoc} */
	@XmlID
	@XmlAttribute
	@Nullable
	public String getDocId() {
		return docId;
	}

	/** {@inheritDoc} */
	public void setDocId() {
		setDocId(UUID.randomUUID().toString());
	}

	/** {@inheritDoc} */
	public void setDocId(final String docId) {
		if (getDocId() != null) {
			throw new IllegalStateException("docId was already set");
		}
		this.docId = docId;
	}

}
