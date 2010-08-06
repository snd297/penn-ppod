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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.modelinterfaces.IWithXmlID;

/**
 * A {@code UUPPodEntity} w/ an {@link XmlID} attribute called {@code "docId"}.
 * 
 * @author Sam Donnelly
 */
public abstract class UUPPodEntityWithXmlId
		extends UUPPodEntity
		implements IWithXmlID {

	/**
	 * Intended for referencing elements within a document - be it XML, JSON,
	 * etc. This is distinct from the pPOD Id of {@link UUPPodEntity}.
	 */
	@CheckForNull
	private String docId;

	/**
	 * {@inheritDoc}
	 * <p>
	 * We call the xml attribute {@code "docId"} so it's format agnostic: we
	 * could be serializing to, eg, JSON.
	 */
	@XmlAttribute(name = "docId")
	@XmlID
	@Nullable
	public String getXmlId() {
		return docId;
	}

	/** {@inheritDoc} */
	public UUPPodEntityWithXmlId setXmlId() {
		return setXmlId(UUID.randomUUID().toString());
	}

	/** {@inheritDoc} */
	public UUPPodEntityWithXmlId setXmlId(final String docId) {
		// Let's not checkNotNul(xmlId) since JAXB calls this and we can't
		// control what it does.a
		if (getXmlId() != null) {
			throw new IllegalStateException("xmlId was already set");
		}
		this.docId = docId;
		return this;
	}
}
