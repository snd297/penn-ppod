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

import static com.google.common.collect.Sets.newHashSet;

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
public final class OTUSetInfo extends PPodEntityInfoWDocId {

	private Set<PPodEntityInfoWDocId> otuInfos = newHashSet();

	/**
	 * The pPOD version of char matrix {@code data} is {@code
	 * matrixInfosByDocId.get(PPodUtil.getPPodId(data))}.
	 */
	private Set<CharacterStateMatrixInfo> matrixInfos = newHashSet();

	private Set<MolecularSequenceSetInfo> sequenceSets = newHashSet();

	private Set<TreeSetInfo> treeSetInfos = newHashSet();

	OTUSetInfo() {}

	@XmlElement(name = "matrixInfo")
	public Set<CharacterStateMatrixInfo> getMatrixInfos() {
		return matrixInfos;
	}

	@XmlElement(name = "treeSetInfo")
	public Set<TreeSetInfo> getTreeSetInfos() {
		return treeSetInfos;
	}

	public OTUSetInfo setMatrixInfos(
			final Set<CharacterStateMatrixInfo> matrixInfos) {
		this.matrixInfos = matrixInfos;
		return this;
	}

	public OTUSetInfo setTreeSetInfos(final Set<TreeSetInfo> treeSetInfos) {
		this.treeSetInfos = treeSetInfos;
		return this;
	}

	@XmlElement(name = "otuInfo")
	public Set<PPodEntityInfoWDocId> getOTUInfos() {
		return otuInfos;
	}

	public OTUSetInfo setOTUInfosByDocId(
			final Set<PPodEntityInfoWDocId> otuInfos) {
		this.otuInfos = otuInfos;
		return this;
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
