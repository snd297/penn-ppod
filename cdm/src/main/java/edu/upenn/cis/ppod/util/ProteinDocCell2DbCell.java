package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.upenn.cis.ppod.dto.PPodProtein;
import edu.upenn.cis.ppod.model.ProteinCell;

public class ProteinDocCell2DbCell {
	public static void docCell2DbCell(final ProteinCell dbCell,
			final char docCell) {
		checkNotNull(dbCell);

		switch (docCell) {
			case '?':
				dbCell.setUnassigned();
				break;
			case 'A':
				dbCell.setSingleElement(PPodProtein.A);
				break;
			case 'C':
				dbCell.setSingleElement(PPodProtein.C);
				break;
			case 'D':
				dbCell.setSingleElement(PPodProtein.D);
				break;
			case 'E':
				dbCell.setSingleElement(PPodProtein.E);
				break;
			case 'F':
				dbCell.setSingleElement(PPodProtein.F);
				break;
			case 'G':
				dbCell.setSingleElement(PPodProtein.G);
				break;
			case 'H':
				dbCell.setSingleElement(PPodProtein.H);
				break;
			case 'I':
				dbCell.setSingleElement(PPodProtein.I);
				break;
			case 'K':
				dbCell.setSingleElement(PPodProtein.K);
				break;
			case 'L':
				dbCell.setSingleElement(PPodProtein.L);
				break;
			case 'M':
				dbCell.setSingleElement(PPodProtein.M);
				break;
			case 'N':
				dbCell.setSingleElement(PPodProtein.N);
				break;
			case 'P':
				dbCell.setSingleElement(PPodProtein.P);
				break;
			case 'Q':
				dbCell.setSingleElement(PPodProtein.Q);
				break;
			case 'R':
				dbCell.setSingleElement(PPodProtein.R);
				break;
			case 'S':
				dbCell.setSingleElement(PPodProtein.S);
				break;
			case 'T':
				dbCell.setSingleElement(PPodProtein.T);
				break;
			case 'V':
				dbCell.setSingleElement(PPodProtein.V);
				break;
			case 'W':
				dbCell.setSingleElement(PPodProtein.W);
				break;
			case 'X':
				dbCell.setSingleElement(PPodProtein.X);
				break;
			case 'Y':
				dbCell.setSingleElement(PPodProtein.Y);
				break;
			case '*':
				dbCell.setSingleElement(PPodProtein.STOP);
				break;
			case '1':
				dbCell.setSingleElement(PPodProtein.ONE);
				break;
			case '2':
				dbCell.setSingleElement(PPodProtein.TWO);
				break;
			case '3':
				dbCell.setSingleElement(PPodProtein.THREE);
				break;
			case '4':
				dbCell.setSingleElement(PPodProtein.FOUR);
				break;
			case '-':
				dbCell.setInapplicable();
				break;
			default:
				throw new AssertionError("can't handle a [" + docCell + "]");
		}
	}
}
