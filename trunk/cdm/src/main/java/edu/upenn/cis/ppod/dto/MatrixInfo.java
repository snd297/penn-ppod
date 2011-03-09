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
import static com.google.common.collect.Maps.newTreeMap;

import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author Sam Donnelly
 */
public class MatrixInfo extends PPodEntityInfo {

	private Map<Integer, PPodEntityInfo> characterInfosByIdx = newTreeMap();

	public MatrixInfo() {}

	/**
	 * Get the characterInfosByIdx.
	 * 
	 * @return the characterInfosByIdx
	 */
	@XmlElementWrapper
	public final Map<Integer, PPodEntityInfo> getCharacterInfosByIdx() {
		return characterInfosByIdx;
	}

	/**
	 * Set the characterInfosByIdx.
	 * 
	 * @param characterInfosByIdx the characterInfosByIdx to set
	 */
	public final void setCharacterInfosByIdx(
			final Map<Integer, PPodEntityInfo> characterInfosByIdx) {
		this.characterInfosByIdx = checkNotNull(characterInfosByIdx);
	}

}
