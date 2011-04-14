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

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodOtuSet extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "otu")
	private List<PPodOtu> otus = newArrayList();

	@XmlElement(name = "standardMatrix")
	private List<PPodStandardMatrix> standardMatrices = newArrayList();

	@XmlElement(name = "dnaMatrix")
	private List<PPodDnaMatrix> dnaMatrices = newArrayList();

	@XmlElement(name = "proteinMatrix")
	private List<PPodProteinMatrix> proteinMatrices = newArrayList();

	@XmlElement(name = "dnaSequenceSet")
	private List<PPodDnaSequenceSet> dnaSequenceSets = newArrayList();

	@XmlElement(name = "treeSet")
	private List<PPodTreeSet> treeSets = newArrayList();

	PPodOtuSet() {}

	public PPodOtuSet(final String label) {
		super(label);
	}

	public PPodOtuSet(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
	}

	public List<PPodDnaMatrix> getDnaMatrices() {
		return dnaMatrices;
	}

	/**
	 * @return the dnaSequenceSets
	 */
	public List<PPodDnaSequenceSet> getDnaSequenceSets() {
		return dnaSequenceSets;
	}

	public List<PPodOtu> getOtus() {
		return otus;
	}

	public List<PPodProteinMatrix> getProteinMatrices() {
		return proteinMatrices;
	}

	public List<PPodStandardMatrix> getStandardMatrices() {
		return standardMatrices;
	}

	public List<PPodTreeSet> getTreeSets() {
		return treeSets;
	}

	public void setDnaMatrices(final List<PPodDnaMatrix> dnaMatrices) {
		this.dnaMatrices = checkNotNull(dnaMatrices);
	}

	public void setDnaSequenceSets(
			final List<PPodDnaSequenceSet> dnaSequenceSets) {
		this.dnaSequenceSets = checkNotNull(dnaSequenceSets);
	}

	public void setOtus(final List<PPodOtu> otus) {
		this.otus = checkNotNull(otus);
	}

	public void setProteinMatrices(final List<PPodProteinMatrix> proteinMatrices) {
		this.proteinMatrices = checkNotNull(proteinMatrices);
	}

	public void setStandardMatrices(
			final List<PPodStandardMatrix> standardMatrices) {
		this.standardMatrices = checkNotNull(standardMatrices);
	}

	public void setTreeSets(final List<PPodTreeSet> treeSets) {
		this.treeSets = checkNotNull(treeSets);
	}
}
