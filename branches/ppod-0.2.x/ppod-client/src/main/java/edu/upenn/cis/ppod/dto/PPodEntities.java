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
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class PPodEntities implements IHasOtuSets {

	private List<PPodStudy> studies = newArrayList();

	public String getLabel() {
		return "From HQL";
	}

	@XmlElement(name = "study")
	public List<PPodStudy> getStudies() {
		return studies;
	}

	public Long getVersion() {
		return 0L;
	}

	public void setStudies(final List<PPodStudy> studies) {
		this.studies = checkNotNull(studies);
	}

	public int countMembers() {
		int count = 0;
		List<PPodOtuSet> otuSets = getOtuSets();
		count += otuSets.size();
		for (PPodOtuSet otuSet : otuSets) {
			count += otuSet.getStandardMatrices().size();
			count += otuSet.getDnaMatrices().size();
			count += otuSet.getProteinMatrices().size();
			count += otuSet.getTreeSets().size();
		}
		return count;
	}

	public List<PPodOtuSet> getOtuSets() {
		List<PPodOtuSet> otuSets = newArrayList();
		for (PPodStudy study : studies) {
			otuSets.addAll(study.getOtuSets());
		}
		return otuSets;
	}
}
