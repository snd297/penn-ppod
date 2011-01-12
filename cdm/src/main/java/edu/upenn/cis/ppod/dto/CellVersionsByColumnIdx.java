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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class CellVersionsByColumnIdx {
	private Map<Integer, Long> cellVersionsByColumnIdx = newHashMap();

	/**
	 * Get the cellVersionsByColumnIdx.
	 * 
	 * @return the cellVersionsByColumnIdx
	 */
	@XmlElementWrapper(name = "cellVersionsByColumnIdx")
	public Map<Integer, Long> getCellVersionsByColumnIdx() {
		return cellVersionsByColumnIdx;
	}

	/**
	 * Set the cellVersionsByColumnIdx.
	 * 
	 * @param cellVersionsByColumnIdx the cellVersionsByColumnIdx to set
	 * 
	 * @return this
	 */
	public CellVersionsByColumnIdx setCellVersionsByColumnIdx(
			final Map<Integer, Long> cellVersionsByColumnIdx) {
		this.cellVersionsByColumnIdx = cellVersionsByColumnIdx;
		return this;
	}
}
