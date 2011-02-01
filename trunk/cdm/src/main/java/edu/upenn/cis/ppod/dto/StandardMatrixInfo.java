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
