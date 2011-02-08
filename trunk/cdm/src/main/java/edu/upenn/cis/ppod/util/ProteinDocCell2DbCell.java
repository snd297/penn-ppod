package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodProtein;
import edu.upenn.cis.ppod.dto.PPodProteinRow;
import edu.upenn.cis.ppod.model.ProteinCell;

public class ProteinDocCell2DbCell {
	public static void docCell2DbCell(final ProteinCell dbCell,
			final PPodCellType cellType,
			final String docSequence) {
		checkNotNull(dbCell);
		checkNotNull(cellType);
		checkNotNull(docSequence);

		final Set<PPodProtein> proteins = newHashSet();

		for (int i = 0; i < docSequence.length(); i++) {
			final char docChar = docSequence.charAt(i);
			checkArgument(PPodProteinRow.LEGAL_CHARS.contains(docChar),
					"'" + docChar + "' is not a legal protein char");
			switch (docChar) {
				case '-':
				case '?':
					// don't care, already dealt with by whatever gave us the
					// cell type
					break;
				case 'A':
					proteins.add(PPodProtein.A);
					break;
				case 'C':
					proteins.add(PPodProtein.C);
					break;
				case 'D':
					proteins.add(PPodProtein.D);
					break;
				case 'E':
					proteins.add(PPodProtein.E);
					break;
				case 'F':
					proteins.add(PPodProtein.F);
					break;
				case 'G':
					proteins.add(PPodProtein.G);
					break;
				case 'H':
					proteins.add(PPodProtein.H);
					break;
				case 'I':
					proteins.add(PPodProtein.I);
					break;
				case 'K':
					proteins.add(PPodProtein.K);
					break;
				case 'L':
					proteins.add(PPodProtein.L);
					break;
				case 'M':
					proteins.add(PPodProtein.M);
					break;
				case 'N':
					proteins.add(PPodProtein.N);
					break;
				case 'P':
					proteins.add(PPodProtein.P);
					break;
				case 'Q':
					proteins.add(PPodProtein.Q);
					break;
				case 'R':
					proteins.add(PPodProtein.R);
					break;
				case 'S':
					proteins.add(PPodProtein.S);
					break;
				case 'T':
					proteins.add(PPodProtein.T);
					break;
				case 'V':
					proteins.add(PPodProtein.V);
					break;
				case 'W':
					proteins.add(PPodProtein.W);
					break;
				case 'X':
					proteins.add(PPodProtein.X);
					break;
				case 'Y':
					proteins.add(PPodProtein.Y);
					break;
				case '*':
					proteins.add(PPodProtein.STOP);
					break;
				case '1':
					proteins.add(PPodProtein.ONE);
					break;
				case '2':
					proteins.add(PPodProtein.TWO);
					break;
				case '3':
					proteins.add(PPodProtein.THREE);
					break;
				case '4':
					proteins.add(PPodProtein.FOUR);
					break;
				default:
					throw new AssertionError("can't handle a [" + docChar + "]");
			}
			switch (cellType) {
				case UNASSIGNED:
					dbCell.setUnassigned();
					break;
				case SINGLE:
					dbCell.setSingle(getOnlyElement(proteins));
					break;
				case POLYMORPHIC:
					dbCell.setPolymorphic(proteins);
					break;
				case UNCERTAIN:
					dbCell.setUncertain(proteins);
					break;
				case INAPPLICABLE:
					dbCell.setInapplicable();
					break;
				default:
					throw new AssertionError();
			}
		}
	}
}
