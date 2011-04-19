package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.NoSuchElementException;

import edu.upenn.cis.ppod.dto.PPodCellType;

public final class PPodSequenceTokenizer {

	public static class Token {
		public final PPodCellType cellType;
		public final String sequence;

		public Token(final PPodCellType cellType, final String sequence) {
			this.cellType = checkNotNull(cellType);
			this.sequence = checkNotNull(sequence);
		}
	}

	private final String sequence;

	private int pos = 0;

	private int tokenCount = -1;

	public PPodSequenceTokenizer(final String sequence) {
		this.sequence = checkNotNull(sequence);
	}

	public int countTokens() {
		if (tokenCount == -1) {
			tokenCount = 0;
			while (hasMoreTokens()) {
				tokenCount++;
				nextToken();
			}
			pos = 0;
		}
		return tokenCount;
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
			checkState(endPos != -1);
			pos = endPos + 1;
			endPos--;
		} else if (pPodChar == '{') {
			cellType = PPodCellType.UNCERTAIN;
			startPos = pos + 1;
			endPos = sequence.indexOf('}', startPos);
			checkState(endPos != -1);
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

		switch (cellType) {
			case UNASSIGNED:
				if (!token.sequence.equals("?")) {
					throw new AssertionError();
				}
				break;
			case SINGLE:
				checkState(token.sequence.length() == 1);
				break;
			case POLYMORPHIC:
				checkState(token.sequence.length() > 1);
				break;
			case UNCERTAIN:
				checkState(token.sequence.length() > 1);
				break;
			case INAPPLICABLE:
				if (!token.sequence.equals("-")) {
					throw new AssertionError();
				}
				break;
			default:
				throw new AssertionError();
		}

		return token;
	}
}
