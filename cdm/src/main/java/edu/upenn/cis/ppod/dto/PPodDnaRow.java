package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

final public class PPodDnaRow extends PPodDomainObject {

	private String sequence;

	@XmlElement(name = "cellVersion")
	private List<Long> cellVersions = newArrayList();

	PPodDnaRow() {}

	public PPodDnaRow(final Long version, final String sequence) {
		super(version);
		checkNotNull(sequence);
		this.sequence = sequence;
	}

	public PPodDnaRow(final String sequence) {
		checkNotNull(sequence);
		this.sequence = sequence;
	}

	public String getSequence() {
		return sequence;
	}

	public List<Long> getCellVersions() {
		return cellVersions;
	}

	public void setCellVersions(final List<Long> cellVersions) {
		checkNotNull(cellVersions);
		this.cellVersions = cellVersions;
	}
}
