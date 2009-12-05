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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

/**
 * A {@code UUPPodEntity} w/ an {@link XmlID} attribute called {@code "docId"}.
 * 
 * @author Sam Donnelly
 */
abstract class UUPPodEntityWXmlId extends UUPPodEntity {

	/**
	 * Intended for referencing elements within a document - be it XML, JSON,
	 * etc. This is distinct from the pPOD Id of {@link UUPPodEntity}.
	 * <p>
	 * Called "docId" and not "xmlId" so that it's format agnostic.
	 */
	private String docId;

	/**
	 * Get the {@link XmlID} attribute.
	 * 
	 * @return the {@code XmlID} attribute
	 */
	@XmlAttribute
	@XmlID
	public String getDocId() {
		return docId;
	}

	/**
	 * Create and set this {@code UUPPodEntityWXmlId}'s doc id.
	 * 
	 * @return this {@code UUPPodEntityWXmlId}
	 * 
	 * @throws IllegalStateException if {@code getDocId() != null} when this
	 *             method is called
	 */
	public UUPPodEntityWXmlId setDocId() {
		return setDocId(UUID.randomUUID().toString());
	}

	/**
	 * Set this {@code UUPPodEntityWXmlId}'s doc id.
	 * 
	 * @param docId the doc id
	 * 
	 * @return this {@code UUPPodEntityWXmlId}
	 * 
	 * @throws IllegalStateException if {@code getDocId() != null} when this
	 *             method is called
	 */
	public UUPPodEntityWXmlId setDocId(final String docId) {
		if (this.docId != null) {
			throw new IllegalStateException("docId was already set");
		}
		this.docId = docId;
		return this;
	}

}