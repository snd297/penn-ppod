package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.upenn.cis.ppod.dto.PPodDnaNucleotide;
import edu.upenn.cis.ppod.model.DnaCell;

public class DocCell2DbCell {

	public static void docCell2DbCell(final DnaCell dbCell, final char docCell) {
		checkNotNull(dbCell);

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
				dbCell.setUncertainElements(PPodDnaNucleotide.A_G);
				break;
			case 'Y':
				dbCell.setUncertainElements(PPodDnaNucleotide.C_T);
				break;
			case 'S':
				dbCell.setUncertainElements(PPodDnaNucleotide.G_C);
				break;
			case 'W':
				dbCell.setUncertainElements(PPodDnaNucleotide.A_T);
				break;
			case 'K':
				dbCell.setUncertainElements(PPodDnaNucleotide.G_T);
				break;
			case 'M':
				dbCell.setUncertainElements(PPodDnaNucleotide.A_C);
				break;
			case 'B':
				dbCell.setUncertainElements(PPodDnaNucleotide.C_G_T);
				break;
			case 'D':
				dbCell.setUncertainElements(PPodDnaNucleotide.A_G_T);
				break;
			case 'H':
				dbCell.setUncertainElements(PPodDnaNucleotide.A_C_T);
				break;
			case 'V':
				dbCell.setUncertainElements(PPodDnaNucleotide.A_C_G);
				break;
			case 'N':
				dbCell.setUncertainElements(PPodDnaNucleotide.A_C_G_T);
				break;
			case '-':
				dbCell.setInapplicable();
				break;
			default:
				throw new AssertionError("can't handle a [" + docCell + "]");
		}
	}
}
