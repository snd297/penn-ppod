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
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class PPodEntities implements IHasOtuSets {

	private Set<PPodOtu> otus = newHashSet();

	private List<PPodOtuSet> otuSets = newArrayList();

	public String getLabel() {
		return "From HQL";
	}

	/**
	 * @return the otus
	 */
	@XmlElement(name = "otu")
	public Set<PPodOtu> getOtus() {
		return otus;
	}

	@XmlElement(name = "otuSet")
	public List<PPodOtuSet> getOtuSets() {
		return otuSets;
	}

	public Long getVersion() {
		return 0L;
	}

	/**
	 * @param otus the otus to set
	 */
	public void setOtus(final Set<PPodOtu> otus) {
		this.otus = checkNotNull(otus);
	}

	public void setOtuSets(final List<PPodOtuSet> otuSets) {
		this.otuSets = checkNotNull(otuSets);
	}

}
