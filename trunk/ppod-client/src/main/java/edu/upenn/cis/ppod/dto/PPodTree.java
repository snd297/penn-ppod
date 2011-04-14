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

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodTree extends UuPPodDomainObjectWithLabel {

	@XmlElement
	private String newick;

	/** For JAXB. */
	@SuppressWarnings("unused")
	private PPodTree() {}

	public PPodTree(@CheckForNull final String pPodId, final String label,
			final String newick) {
		super(pPodId, label);
		this.newick = checkNotNull(newick);
	}

	public String getNewick() {
		return newick;
	}

	public void setNewick(final String newick) {
		this.newick = checkNotNull(newick);
	}

}
