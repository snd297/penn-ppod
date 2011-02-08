package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

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

	/**
	 * @throws NoSuchElementException if there are no more tokens
	 */
	public Token nextToken() {

		if (!hasMoreTokens()) {
			throw new NoSuchElementException();
		}

		final char pPodChar = sequence.charAt(pos);

		int startPos;
		int endPos;

		PPodCellType cellType = null;

		if (pPodChar == '(') {
			cellType = PPodCellType.POLYMORPHIC;
			startPos = pos + 1;
			endPos = sequence.indexOf(')', startPos);
			pos = endPos + 1;
			endPos--;
		} else if (pPodChar == '{') {
			cellType = PPodCellType.UNCERTAIN;
			startPos = pos + 1;
			endPos = sequence.indexOf('}', startPos);
			pos = endPos + 1;
			endPos--;
		} else {
			if ('-' == pPodChar) {
				cellType = PPodCellType.INAPPLICABLE;
			} else if ('?' == pPodChar) {
				cellType = PPodCellType.UNASSIGNED;
			} else {
				cellType = PPodCellType.SINGLE;
			}
			startPos = pos;
			endPos = pos;
			pos = endPos + 1;
		}

		final Token token =
				new Token(cellType, sequence.substring(startPos, endPos + 1));
		return token;
	}
}
