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
package edu.upenn.cis.ppod.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * For communicating back to a client certain information about a pPOD entity
 * without having to send the entire entity: the pPOD ID, pPOD Version, database
 * id, and the doc id from a corresponding request.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PPodEntityInfo implements IHasPPodId {

	private String pPodId;

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
	 * Set the pPodId.
	 * 
	 * @param pPodId the pPodId to set
	 */
	public void setPPodId(final String pPodId) {
		this.pPodId = pPodId;
	}

	/**
	 * This method is not supported.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	public void setPPodId() {
		throw new UnsupportedOperationException();
	}
}
