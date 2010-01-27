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
 * A {@code PPodEntity} w/ an {@link XmlID} attribute called {@code "docId"}.
 * 
 * @author Sam Donnelly
 */
abstract class PPodEntityWXmlId extends PPodEntity {
	/**
	 * Intended for referencing elements within a document - be it XML, JSON,
	 * etc. This is distinct from the pPOD Id of {@link UUPPodEntity}.
	 * <p>
	 * NOTE: we don't require an explicit setter to this because we don't need a
	 * getter method. TODO: figure out why that is! It's something to do with
	 * help avoiding bugs.
	 */
	@XmlAttribute
	@XmlID
	@SuppressWarnings("unused")
	private String docId = UUID.randomUUID().toString();

	protected PPodEntityWXmlId setDocId(final String docId) {
		this.docId = docId;
		return this;
	}
}
