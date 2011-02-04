package edu.upenn.cis.ppod.dto;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class PPodDnaRow extends PPodMolecularRow {

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
	@XmlElement(name = "cellVersion")
	@CheckForNull
	private List<Long> cellVersions;

	PPodDnaRow() {}

	public PPodDnaRow(final Long version, final String sequence) {
		super(version, sequence);
	}

	public PPodDnaRow(final String sequence) {
		super(sequence);
	}

	@Override
	protected Set<Character> getLegalChars() {
		return LEGAL_CHARS;
	}

	@Nullable
	public List<Long> getCellVersions() {
		return cellVersions;
	}

	public void setCellVersions(@CheckForNull final List<Long> cellVersions) {
		this.cellVersions = cellVersions;
	}
}
