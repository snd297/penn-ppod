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
import static com.google.common.collect.Maps.newTreeMap;

import java.util.Map;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlElementWrapper;

public class StandardMatrixInfo extends MatrixInfo {
	/**
	 * Version of column header {@code ic} is at
	 * {@code columnHeaderVersionsByColumnIndex.get(ic)}. The returned value
	 * will be {@code null} if no version number was inserted.
	 * <p>
	 * Uses a {@link TreeMap} to ease debugging: it's easier to read if it
	 * prints out in order.
	 */
	private Map<Integer, Long> columnHeaderVersionsByIdx = newTreeMap();

	@XmlElementWrapper
	public Map<Integer, Long> getColumnHeaderVersionsByIdx() {
		return columnHeaderVersionsByIdx;
	}

	public void setColumnHeaderVersionsByIdx(
			final SortedMap<Integer, Long> columnHeaderPPodVersions) {
		checkNotNull(columnHeaderPPodVersions);
		this.columnHeaderVersionsByIdx = columnHeaderPPodVersions;
	}
}
