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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newTreeMap;

import java.util.Map;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlElementWrapper;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * @author Sam Donnelly
 */
public class MatrixInfo extends PPodEntityInfo {

	private Map<Integer, PPodEntityInfo> characterInfosByIdx = newTreeMap();

	/**
	 * Version of row header {@code it} is at {@code rowVersionsByIdx.get(it)}.
	 * The returned value will be {@code null} if no version number was
	 * inserted.
	 * <p>
	 * Uses a {@link TreeMap} to ease debugging: it's easier to read if it
	 * prints out in order.
	 */
	private Map<Integer, Long> rowVersionsByIdx = newTreeMap();

	/**
	 * Version of cell {@code it,ic} is at
	 * 
	 * <pre>
	 * Long versionItIc = null;
	 * if (cellPPodIdAndVersionsByMN.get(it) == null) {
	 * 	versionItIc = null;
	 * } else {
	 * 	versionItIc = cellPPodIdAndVersionsByMN.get(it).get(ic);
	 * }
	 * </pre>
	 * <p>
	 * Uses a {@link TreeMap} to ease debugging: it's easier to read if it
	 * prints out in order.
	 * 
	 * {@code versionItIc} will be {@code null} if no version number was
	 * inserted.
	 */
	private final Map<Integer, CellVersionsByColumnIdx> cellPPodIdAndVersionsByMN = newTreeMap();

	public MatrixInfo() {}

	@Nullable
	public final Long getCellVersion(final int it, final int ic) {
		checkArgument(it >= 0);
		checkArgument(ic >= 0);
		if (cellPPodIdAndVersionsByMN.get(it) == null) {
			return null;
		} else {
			return cellPPodIdAndVersionsByMN.get(it)
					.getCellVersionsByColumnIdx().get(ic);
		}
	}

	@XmlElementWrapper
	public final Map<Integer, CellVersionsByColumnIdx> getCellVersionsByMN() {
		return cellPPodIdAndVersionsByMN;
	}

	/**
	 * Get the characterInfosByIdx.
	 * 
	 * @return the characterInfosByIdx
	 */
	@XmlElementWrapper
	public final Map<Integer, PPodEntityInfo> getCharacterInfosByIdx() {
		return characterInfosByIdx;
	}

	@XmlElementWrapper
	final public Map<Integer, Long> getRowHeaderVersionsByIdx() {
		return rowVersionsByIdx;
	}

	public final void setCellPPodIdAndVersion(final int m,
			final int n, final Long cellPPodVersion) {
		checkArgument(m >= 0);
		checkArgument(n >= 0);
		checkNotNull(cellPPodVersion);
		if (cellPPodIdAndVersionsByMN.containsKey(m)) {

		} else {
			cellPPodIdAndVersionsByMN.put(m,
					new CellVersionsByColumnIdx());
		}
		cellPPodIdAndVersionsByMN.get(m).getCellVersionsByColumnIdx().put(n,
				cellPPodVersion);
	}

	/**
	 * Set the characterInfosByIdx.
	 * 
	 * @param characterInfosByIdx the characterInfosByIdx to set
	 */
	public final void setCharacterInfosByIdx(
			final Map<Integer, PPodEntityInfo> characterInfosByIdx) {
		checkNotNull(characterInfosByIdx);
		this.characterInfosByIdx = characterInfosByIdx;
	}

	public final void setRowHeaderVersionsByRowIdx(
			final SortedMap<Integer, Long> rowHeaderPPodVersions) {
		checkNotNull(rowHeaderPPodVersions);
		this.rowVersionsByIdx = rowHeaderPPodVersions;
	}

}
