package edu.upenn.cis.ppod.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Counts {

	public Counts() {}

	public long otuSetCount = -1;
	public long standardMatrixCount = -1;
	public long dnaMatrixCount = -1;
	public long treeSetCount = -1;
}
