package edu.upenn.cis.ppod.dto;

import java.util.List;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public final class PPodProteinMatrix extends
		PPodMatrix<PPodProteinRow> {

	PPodProteinMatrix() {
		super();
	}

	public PPodProteinMatrix(String pPodId, Long version, String label) {
		super(pPodId, version, label);
	}

	public PPodProteinMatrix(@CheckForNull String pPodId, String label) {
		super(pPodId, label);
	}

	@Override
	public final List<PPodProteinRow> getRows() {
		return super.getRows();
	}

}
