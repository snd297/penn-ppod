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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

@XmlAccessorType(XmlAccessType.PROPERTY)
public final class PPodProteinMatrix
		extends PPodMatrix<PPodProteinRow> {

	/** For JAXB. */
	@SuppressWarnings("unused")
	private PPodProteinMatrix() {}

	public PPodProteinMatrix(
			@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
	}

	@XmlElement(name = "row")
	@Override
	public List<PPodProteinRow> getRows() {
		return super.getRows();
	}

}
