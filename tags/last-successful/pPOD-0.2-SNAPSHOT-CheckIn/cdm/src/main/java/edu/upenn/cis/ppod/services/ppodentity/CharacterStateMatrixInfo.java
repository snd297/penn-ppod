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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newTreeMap;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class CharacterStateMatrixInfo extends PPodEntityInfoWDocId {

	private Map<Integer, PPodEntityInfo> characterInfosByIdx = newTreeMap();

	/**
	 * Version of column header {@code ic} is at {@code
	 * columnHeaderVersionsByColumnIndex.get(ic)}. The returned value will be
	 * {@code null} if no version number was inserted.
	 * <p>
	 * Uses a {@link TreeMap} to ease debugging: it's easier to read if it
	 * prints out in order.
	 */
	private Map<Integer, Long> columnHeaderVersionsByIdx = newTreeMap();

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

	private Provider<CellVersionsByColumnIdx> cellPPodIdAndVersionsByColumnIdxProvider;

	CharacterStateMatrixInfo() {}

	@Inject
	CharacterStateMatrixInfo(
			final Provider<CellVersionsByColumnIdx> cellPPodIdAndVersionsByColumnIdxProvider) {
		this.cellPPodIdAndVersionsByColumnIdxProvider = cellPPodIdAndVersionsByColumnIdxProvider;
	}

	public Long getCellVersion(final int it, final int ic) {
		if (cellPPodIdAndVersionsByMN.get(it) == null) {
			return null;
		} else {
			return cellPPodIdAndVersionsByMN.get(it)
					.getCellVersionsByColumnIdx().get(ic);
		}
	}

	@XmlElementWrapper
	public Map<Integer, CellVersionsByColumnIdx> getCellVersionsByMN() {
		return cellPPodIdAndVersionsByMN;
	}

	/**
	 * Get the characterInfosByIdx.
	 * 
	 * @return the characterInfosByIdx
	 */
	@XmlElementWrapper
	public Map<Integer, PPodEntityInfo> getCharacterInfosByIdx() {
		return characterInfosByIdx;
	}

	@XmlElementWrapper
	public Map<Integer, Long> getColumnHeaderVersionsByIdx() {
		return columnHeaderVersionsByIdx;
	}

	@XmlElementWrapper
	public Map<Integer, Long> getRowHeaderVersionsByIdx() {
		return rowVersionsByIdx;
	}

	public CharacterStateMatrixInfo setCellPPodIdAndVersion(final Integer x,
			final Integer y, final Long cellPPodVersion) {
		checkNotNull(x);
		checkNotNull(y);
		checkNotNull(cellPPodVersion);
		if (cellPPodIdAndVersionsByMN.containsKey(x)) {

		} else {
			cellPPodIdAndVersionsByMN.put(x,
					cellPPodIdAndVersionsByColumnIdxProvider.get());
		}
		cellPPodIdAndVersionsByMN.get(x).getCellVersionsByColumnIdx().put(y,
				cellPPodVersion);
		return this;
	}

	/**
	 * Set the characterInfosByIdx.
	 * 
	 * @param characterInfosByIdx the characterInfosByIdx to set
	 * 
	 * @return this
	 */
	public CharacterStateMatrixInfo setCharacterInfosByIdx(
			final Map<Integer, PPodEntityInfo> characterInfosByIdx) {
		this.characterInfosByIdx = characterInfosByIdx;
		return this;
	}

	public CharacterStateMatrixInfo setColumnHeaderVersionsByIdx(
			final SortedMap<Integer, Long> columnHeaderPPodVersions) {
		this.columnHeaderVersionsByIdx = columnHeaderPPodVersions;
		return this;
	}

	public CharacterStateMatrixInfo setRowHeaderVersionsByRowIdx(
			final SortedMap<Integer, Long> rowHeaderPPodVersions) {
		this.rowVersionsByIdx = rowHeaderPPodVersions;
		return this;
	}

}
