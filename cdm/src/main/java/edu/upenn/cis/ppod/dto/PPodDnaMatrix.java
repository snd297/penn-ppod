package edu.upenn.cis.ppod.dto;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

final public class PPodDnaMatrix extends UuPPodDomainObjectWithLabel {

	PPodDnaMatrix() {}

	@XmlElement(name = "row")
	private final List<PPodDnaRow> rows = newArrayList();

	@XmlElement(name = "columnVersion")
	private final List<Long> columnVersions = newArrayList();

	public PPodDnaMatrix(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	public List<PPodDnaRow> getRows() {
		return rows;
	}

	public PPodDnaMatrix(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	public List<Long> getColumnVersions() {
		return columnVersions;
	}

}
