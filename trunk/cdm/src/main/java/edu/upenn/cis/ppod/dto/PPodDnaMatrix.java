package edu.upenn.cis.ppod.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

final public class PPodDnaMatrix extends PPodMatrix<PPodDnaRow> {

	PPodDnaMatrix() {}

	public PPodDnaMatrix(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	public PPodDnaMatrix(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	@XmlElement(name = "row")
	public final List<PPodDnaRow> getRows() {
		return super.getRows();
	}

}
