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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;

/**
 * For communicating back to a client certain information about a pPOD entity
 * without having to send the entire entity: the pPOD ID, pPOD Version, database
 * id, and the doc id from a corresponding request.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PPodEntityInfo implements IWithPPodId {

	private String pPodId;

	private Long pPodVersion;

	private Long entityId;

	/**
	 * Get the entityId.
	 * 
	 * @return the entityId
	 */
	@XmlAttribute
	public Long getEntityId() {
		return entityId;
	}

	/**
	 * Get the pPodId.
	 * 
	 * @return the pPodId
	 */
	@XmlAttribute(name = "pPodId")
	public String getPPodId() {
		return pPodId;
	}

	/**
	 * Get the pPodVersion.
	 * 
	 * @return the pPodVersion
	 */
	@XmlAttribute(name = "pPodVersion")
	public Long getPPodVersion() {
		return pPodVersion;
	}

	/**
	 * Set the entityId.
	 * 
	 * @param entityId the entityId to set
	 * 
	 * @return this
	 */
	public PPodEntityInfo setEntityId(final Long entityId) {
		this.entityId = entityId;
		return this;
	}

	/**
	 * Set the pPodId.
	 * 
	 * @param pPodId the pPodId to set
	 * 
	 * @return this
	 */
	public PPodEntityInfo setPPodId(final String pPodId) {
		this.pPodId = pPodId;
		return this;
	}

	/**
	 * Set the pPodVersion.
	 * 
	 * @param pPodVersion the pPodVersion to set
	 * 
	 * @return this
	 */
	public PPodEntityInfo setVersion(final Long pPodVersion) {
		this.pPodVersion = pPodVersion;
		return this;
	}

	/**
	 * This method is not supported.
	 */
	public PPodEntityInfo setPPodId() {
		throw new UnsupportedOperationException();
	}
}
