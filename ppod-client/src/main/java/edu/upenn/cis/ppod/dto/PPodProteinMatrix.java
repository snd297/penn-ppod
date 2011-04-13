package edu.upenn.cis.ppod.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import edu.umd.cs.findbugs.annotations.CheckForNull;

@XmlAccessorType(XmlAccessType.PROPERTY)
public final class PPodProteinMatrix extends
		PPodMatrix<PPodProteinRow> {

	/** For JAXB. */
	@SuppressWarnings("unused")
	private PPodProteinMatrix() {}

	public PPodProteinMatrix(@CheckForNull final String pPodId,
			final String label) {
		super(pPodId, label);
	}

	@XmlElement(name = "row")
	@Override
	public List<PPodProteinRow> getRows() {
		return super.getRows();
	}

}
