package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.upenn.cis.ppod.dto.PPodCellType;

public class PPodSequenceTokenizer {

	public static class Token {
		public final PPodCellType cellType;
		public final String sequence;

		public Token(final PPodCellType cellType, final String sequence) {
			checkNotNull(cellType);
			checkNotNull(sequence);
			this.cellType = cellType;
			this.sequence = sequence;
		}
	}

	private final String sequence;

	private int pos = 0;

	public PPodSequenceTokenizer(final String sequence) {
		checkNotNull(sequence);
		this.sequence = sequence;
	}

	public boolean hasMoreTokens() {
		if (pos < sequence.length()) {
			return true;
		}
		return false;
	}

	public Token nextToken() {
		final char pPodChar = sequence.charAt(pos);

		int endPos;

		PPodCellType cellType = null;

		if (pPodChar == '(') {
			cellType = PPodCellType.POLYMORPHIC;
			endPos = sequence.indexOf(')', pos + 1);
		} else if (pPodChar == '{') {
			cellType = PPodCellType.UNCERTAIN;
			endPos = sequence.indexOf('}', pos + 1);
		} else {
			if ('-' == pPodChar) {
				cellType = PPodCellType.INAPPLICABLE;
			} else if ('?' == pPodChar) {
				cellType = PPodCellType.UNASSIGNED;
			} else {
				cellType = PPodCellType.SINGLE;
			}
			endPos = pos;
		}

		final Token token =
				new Token(cellType, sequence.substring(pos, endPos + 1));
		pos = endPos + 1;
		return token;
	}
}
