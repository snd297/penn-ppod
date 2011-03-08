package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class PPodStandardRow {

	@XmlElement(name = "cell")
	private List<PPodStandardCell> cells = newArrayList();

	public PPodStandardRow() {}

	public List<PPodStandardCell> getCells() {
		return cells;
	}

	public void setCells(final List<PPodStandardCell> cells) {
		checkNotNull(cells);
		this.cells = cells;
	}

}
