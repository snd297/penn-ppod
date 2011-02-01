package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

final public class PPodDnaMatrix extends UuPPodDomainObjectWithLabel {

	@XmlElement(name = "row")
	private List<PPodDnaRow> rows = newArrayList();

	PPodDnaMatrix() {}

	public PPodDnaMatrix(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	public PPodDnaMatrix(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	public List<PPodDnaRow> getRows() {
		return rows;
	}

	public void setRows(final List<PPodDnaRow> rows) {
		checkNotNull(rows);
		this.rows = rows;
	}
}
