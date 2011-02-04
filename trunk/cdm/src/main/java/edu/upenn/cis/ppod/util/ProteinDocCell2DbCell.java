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
				dbCell.setSingle(PPodProtein.A);
				break;
			case 'C':
				dbCell.setSingle(PPodProtein.C);
				break;
			case 'D':
				dbCell.setSingle(PPodProtein.D);
				break;
			case 'E':
				dbCell.setSingle(PPodProtein.E);
				break;
			case 'F':
				dbCell.setSingle(PPodProtein.F);
				break;
			case 'G':
				dbCell.setSingle(PPodProtein.G);
				break;
			case 'H':
				dbCell.setSingle(PPodProtein.H);
				break;
			case 'I':
				dbCell.setSingle(PPodProtein.I);
				break;
			case 'K':
				dbCell.setSingle(PPodProtein.K);
				break;
			case 'L':
				dbCell.setSingle(PPodProtein.L);
				break;
			case 'M':
				dbCell.setSingle(PPodProtein.M);
				break;
			case 'N':
				dbCell.setSingle(PPodProtein.N);
				break;
			case 'P':
				dbCell.setSingle(PPodProtein.P);
				break;
			case 'Q':
				dbCell.setSingle(PPodProtein.Q);
				break;
			case 'R':
				dbCell.setSingle(PPodProtein.R);
				break;
			case 'S':
				dbCell.setSingle(PPodProtein.S);
				break;
			case 'T':
				dbCell.setSingle(PPodProtein.T);
				break;
			case 'V':
				dbCell.setSingle(PPodProtein.V);
				break;
			case 'W':
				dbCell.setSingle(PPodProtein.W);
				break;
			case 'X':
				dbCell.setSingle(PPodProtein.X);
				break;
			case 'Y':
				dbCell.setSingle(PPodProtein.Y);
				break;
			case '*':
				dbCell.setSingle(PPodProtein.STOP);
				break;
			case '1':
				dbCell.setSingle(PPodProtein.ONE);
				break;
			case '2':
				dbCell.setSingle(PPodProtein.TWO);
				break;
			case '3':
				dbCell.setSingle(PPodProtein.THREE);
				break;
			case '4':
				dbCell.setSingle(PPodProtein.FOUR);
				break;
			case '-':
				dbCell.setInapplicable();
				break;
			default:
				throw new AssertionError("can't handle a [" + docCell + "]");
		}
	}
}
