package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.EnumSet;
import java.util.Set;

import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodDnaNucleotide;
import edu.upenn.cis.ppod.model.DnaCell;

public class DnaDocCell2DbCell {

	public static void docCell2DbCell(final DnaCell dbCell,
			final PPodCellType cellType,
			final String docSequence) {
		checkNotNull(dbCell);
		checkNotNull(cellType);
		checkNotNull(docSequence);

		final Set<PPodDnaNucleotide> nucleotides = EnumSet
				.noneOf(PPodDnaNucleotide.class);
		boolean lowerCase = false;

		for (int i = 0; i < docSequence.length(); i++) {
			final char docChar = docSequence.charAt(i);
			if (Character.isLowerCase(docChar)) {
				lowerCase = true;
			}
			final char upCasedDocChar = Character.toUpperCase(docChar);
			switch (upCasedDocChar) {
				case '-':
				case '?':
					// don't care, already dealt with by whatever gave us the
					// cell type
					break;
				case 'A':
					nucleotides.add(PPodDnaNucleotide.A);
					break;
				case 'C':
					nucleotides.add(PPodDnaNucleotide.C);
					break;
				case 'G':
					nucleotides.add(PPodDnaNucleotide.G);
					break;
				case 'T':
					nucleotides.add(PPodDnaNucleotide.T);
					break;
				default:
					throw new IllegalArgumentException(
							"illegacl char in sequence [" + docChar + "]");
			}
		}
		switch (cellType) {
			case UNASSIGNED:
				checkArgument(docSequence.equals("?"));
				dbCell.setUnassigned();
				break;
			case SINGLE:
				checkArgument(docSequence.length() == 1);
				dbCell.setSingle(getOnlyElement(nucleotides), lowerCase);
				break;
			case POLYMORPHIC:
				checkArgument(docSequence.length() > 1);
				dbCell.setPolymorphic(nucleotides, lowerCase);
				break;
			case UNCERTAIN:
				checkArgument(docSequence.length() > 1);
				dbCell.setUncertain(nucleotides);
				break;
			case INAPPLICABLE:
				checkArgument(docSequence.equals("-"));
				dbCell.setInapplicable();
				break;
			default:
				throw new AssertionError();
		}
	}
}
