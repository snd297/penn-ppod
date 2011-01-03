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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class OtuSetInfoCaseChange extends PPodEntityInfoWDocId {

	/** Order matters for these. */
	private final List<PPodEntityInfoWDocId> otuInfos = newArrayList();

	private final Set<MatrixInfo> matrixInfos = newHashSet();

	private final Set<SequenceSetInfo> sequenceSetInfos = newHashSet();

	private final Set<TreeSetInfo> treeSetInfos = newHashSet();

	public OtuSetInfoCaseChange() {}

	@XmlElement(name = "matrixInfo")
	public Set<MatrixInfo> getMatrixInfos() {
		return matrixInfos;
	}

	/**
	 * Guaranteed to be in the same order as they were in the {@code OTUSet} on
	 * the upload.
	 * 
	 * @return
	 */
	@XmlElement(name = "otuInfo")
	public List<PPodEntityInfoWDocId> getOTUInfos() {
		return otuInfos;
	}

	@XmlElement(name = "sequenceSetInfo")
	public Set<SequenceSetInfo> getSequenceSetInfos() {
		return sequenceSetInfos;
	}

	@XmlElement(name = "treeSetInfo")
	public Set<TreeSetInfo> getTreeSetInfos() {
		return treeSetInfos;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = "    ";

		String retValue = "";

		retValue = "OTUSetInfo ( " + super.toString() + TAB
					+ "otuInfosByDocId = " + this.otuInfos + TAB + " )";

		return retValue;
	}

}
