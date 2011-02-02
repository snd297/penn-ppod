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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * A collection of work - inspired by a Mesquite project - sets of OTU sets and,
 * through the OTU sets, matrices and tree sets.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
public final class PPodStudy extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "otuSet")
	private List<PPodOtuSet> otuSets = newArrayList();

	PPodStudy() {}

	public PPodStudy(final String pPodId, final Long version, final String label) {
		super(pPodId, version, label);
	}

	public PPodStudy(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	public List<PPodOtuSet> getOtuSets() {
		return otuSets;
	}

	public void setOtuSets(final List<PPodOtuSet> otuSets) {
		checkNotNull(otuSets);
		this.otuSets = otuSets;
	}
}
