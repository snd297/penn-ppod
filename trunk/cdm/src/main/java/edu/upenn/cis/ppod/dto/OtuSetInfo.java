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

	/** Order matters for these. */
	private final List<PPodEntityInfo> otuInfos = newArrayList();

	private final List<MatrixInfo> matrixInfos = newArrayList();

	private final List<SequenceSetInfo> sequenceSetInfos = newArrayList();

	private final List<TreeSetInfo> treeSetInfos = newArrayList();

	public OtuSetInfo() {}

	@XmlElement(name = "matrixInfo")
	public List<MatrixInfo> getMatrixInfos() {
		return matrixInfos;
	}

	/**
	 * Guaranteed to be in the same order as they were in the {@code OTUSet} on
	 * the upload.
	 * 
	 * @return
	 */
	@XmlElement(name = "otuInfo")
	public List<PPodEntityInfo> getOTUInfos() {
		return otuInfos;
	}

	@XmlElement(name = "sequenceSetInfo")
	public List<SequenceSetInfo> getSequenceSetInfos() {
		return sequenceSetInfos;
	}

	@XmlElement(name = "treeSetInfo")
	public List<TreeSetInfo> getTreeSetInfos() {
		return treeSetInfos;
	}
}
