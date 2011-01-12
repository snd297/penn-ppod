package edu.upenn.cis.ppod.util;

import java.util.EnumSet;

import edu.upenn.cis.ppod.dto.PPodDnaNucleotide;
import edu.upenn.cis.ppod.model.DnaCell;

public class DocCell2DbCell {

	public static void docCell2DbCell(final DnaCell dbCell, final char docCell) {

		switch (docCell) {
			case '?':
				dbCell.setUnassigned();
				break;
			case 'A':
				dbCell.setSingleElement(PPodDnaNucleotide.A, false);
				break;
			case 'a':
				dbCell.setSingleElement(PPodDnaNucleotide.A, true);
				break;
			case 'C':
				dbCell.setSingleElement(PPodDnaNucleotide.C, false);
				break;
			case 'c':
				dbCell.setSingleElement(PPodDnaNucleotide.C, true);
				break;
			case 'G':
				dbCell.setSingleElement(PPodDnaNucleotide.G, false);
				break;
			case 'g':
				dbCell.setSingleElement(PPodDnaNucleotide.G, true);
				break;
			case 'T':
				dbCell.setSingleElement(PPodDnaNucleotide.T, false);
				break;
			case 't':
				dbCell.setSingleElement(PPodDnaNucleotide.T, true);
				break;
			case 'R':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.A,
						PPodDnaNucleotide.G));
				break;
			case 'Y':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.C,
						PPodDnaNucleotide.T));
				break;
			case 'S':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.G,
						PPodDnaNucleotide.C));
				break;
			case 'W':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.A,
						PPodDnaNucleotide.T));
				break;
			case 'K':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.G,
						PPodDnaNucleotide.T));
				break;
			case 'M':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.A,
						PPodDnaNucleotide.C));
				break;
			case 'B':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.C,
						PPodDnaNucleotide.G, PPodDnaNucleotide.T));
				break;
			case 'D':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.A,
						PPodDnaNucleotide.G, PPodDnaNucleotide.T));
				break;
			case 'H':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.A,
						PPodDnaNucleotide.C, PPodDnaNucleotide.T));
				break;
			case 'V':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.A,
						PPodDnaNucleotide.C, PPodDnaNucleotide.G));
				break;
			case 'N':
				dbCell.setUncertainElements(EnumSet.of(PPodDnaNucleotide.A,
						PPodDnaNucleotide.C, PPodDnaNucleotide.G,
						PPodDnaNucleotide.T));
				break;
			case '-':
				dbCell.setInapplicable();
				break;
			default:
				throw new AssertionError("can't handle a [" + docCell + "]");
		}
	}
}
