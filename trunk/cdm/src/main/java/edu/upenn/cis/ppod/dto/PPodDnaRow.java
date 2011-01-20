package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableSet;

final public class PPodDnaRow extends PPodDomainObject {

	public final static Set<java.lang.Character> LEGAL_CHARS =
			ImmutableSet.of(
					'A', 'a',
					'C', 'c',
					'G', 'g',
					'T', 't',
					'R',
					'Y',
					'K',
					'M',
					'S',
					'W',
					'B',
					'D',
					'H',
					'V',
					'N',
					'-',
					'?');

	private String sequence;

	@XmlElement(name = "cellVersion")
	private List<Long> cellVersions = newArrayList();

	PPodDnaRow() {}

	public PPodDnaRow(final Long version, final String sequence) {
		super(version);
		checkNotNull(sequence);
		setSequence(sequence);
	}

	public PPodDnaRow(final String sequence) {
		checkNotNull(sequence);
		this.sequence = sequence;
	}

	public List<Long> getCellVersions() {
		return cellVersions;
	}

	public String getSequence() {
		return sequence;
	}

	public void setCellVersions(final List<Long> cellVersions) {
		checkNotNull(cellVersions);
		this.cellVersions = cellVersions;
	}

	public void setSequence(final String sequence) {
		checkNotNull(sequence);
		for (int i = 0; i < sequence.length(); i++) {
			checkArgument(LEGAL_CHARS.contains(sequence.charAt(i)),
					"position " + i + " is " + sequence.charAt(i)
							+ " which is illegal");
		}
		this.sequence = sequence;
	}
}
