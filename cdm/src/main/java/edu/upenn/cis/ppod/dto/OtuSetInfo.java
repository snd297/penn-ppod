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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class OtuSetInfo extends PPodEntityInfo {

	private List<PPodEntityInfo> otuInfos = newArrayList();

	private List<StandardMatrixInfo> standardMatrixInfos = newArrayList();

	private List<MatrixInfo> dnaMatrixInfos = newArrayList();

	private List<MatrixInfo> proteinMatrixInfos = newArrayList();

	private List<SequenceSetInfo> sequenceSetInfos = newArrayList();

	private List<TreeSetInfo> treeSetInfos = newArrayList();

	public OtuSetInfo() {}

	/**
	 * Guaranteed to be in the same order as they were in the {@code OTUSet} on
	 * the upload.
	 * 
	 * @return
	 */
	@XmlElement(name = "dnaMatrixInfo")
	public List<MatrixInfo> getDnaMatrixInfos() {
		return dnaMatrixInfos;
	}

	/**
	 * Guaranteed to be in the same order as they were in the {@code OTUSet} on
	 * the upload.
	 * 
	 * @return
	 */
	@XmlElement(name = "otuInfo")
	public List<PPodEntityInfo> getOtuInfos() {
		return otuInfos;
	}

	/**
	 * Guaranteed to be in the same order as they were in the {@code OTUSet} on
	 * the upload.
	 * 
	 * @return
	 */
	@XmlElement(name = "proteinMatrixInfo")
	public List<MatrixInfo> getProteinMatrixInfos() {
		return proteinMatrixInfos;
	}

	/**
	 * Guaranteed to be in the same order as they were in the {@code OTUSet} on
	 * the upload.
	 * 
	 * @return
	 */
	@XmlElement(name = "sequenceSetInfo")
	public List<SequenceSetInfo> getSequenceSetInfos() {
		return sequenceSetInfos;
	}

	/**
	 * Guaranteed to be in the same order as they were in the {@code OTUSet} on
	 * the upload.
	 * 
	 * @return
	 */
	@XmlElement(name = "standardMatrixInfo")
	public List<StandardMatrixInfo> getStandardMatrixInfos() {
		return standardMatrixInfos;
	}

	/**
	 * Guaranteed to be in the same order as they were in the {@code OTUSet} on
	 * the upload.
	 * 
	 * @return
	 */
	@XmlElement(name = "treeSetInfo")
	public List<TreeSetInfo> getTreeSetInfos() {
		return treeSetInfos;
	}

	/**
	 * @param dnaMatrixInfos the dnaMatrixInfos to set
	 */
	public void setDnaMatrixInfos(final List<MatrixInfo> dnaMatrixInfos) {
		checkNotNull(dnaMatrixInfos);
		this.dnaMatrixInfos = dnaMatrixInfos;
	}

	public void setOtuInfos(final List<PPodEntityInfo> otuInfos) {
		checkNotNull(otuInfos);
		this.otuInfos = otuInfos;
	}

	public void setProteinMatrixInfos(final List<MatrixInfo> proteinMatrixInfos) {
		checkNotNull(proteinMatrixInfos);
		this.proteinMatrixInfos = proteinMatrixInfos;
	}

	/**
	 * @param sequenceSetInfos the sequenceSetInfos to set
	 */
	public void setSequenceSetInfos(final List<SequenceSetInfo> sequenceSetInfos) {
		checkNotNull(sequenceSetInfos);
		this.sequenceSetInfos = sequenceSetInfos;
	}

	/**
	 * @param standardMatrixInfos the standardMatrixInfos to set
	 */
	public void setStandardMatrixInfos(
			final List<StandardMatrixInfo> standardMatrixInfos) {
		checkNotNull(standardMatrixInfos);
		this.standardMatrixInfos = standardMatrixInfos;
	}

	/**
	 * @param treeSetInfos the treeSetInfos to set
	 */
	public void setTreeSetInfos(final List<TreeSetInfo> treeSetInfos) {
		checkNotNull(treeSetInfos);
		this.treeSetInfos = treeSetInfos;
	}

}
