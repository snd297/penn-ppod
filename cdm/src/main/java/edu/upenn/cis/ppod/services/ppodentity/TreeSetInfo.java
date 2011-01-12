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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sam Donnelly
 * 
 */
@XmlRootElement
public final class TreeSetInfo extends PPodEntityInfo {

	private List<PPodEntityInfo> treeInfos = newArrayList();

	/**
	 * Get the treePPodVersions.
	 * 
	 * @return the treePPodVersions
	 */
	@XmlElement(name = "treeInfo")
	public List<PPodEntityInfo> getTreeInfos() {
		return treeInfos;
	}

	/**
	 * Set the treePPodVersions.
	 * 
	 * @param treePPodVersions the treePPodVersions to set
	 * 
	 * @return this
	 */
	public TreeSetInfo setTreePPodInfos(
			final List<PPodEntityInfo> treePPodVersions) {
		this.treeInfos = treePPodVersions;
		return this;
	}
}
