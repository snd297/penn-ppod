package edu.upenn.cis.ppod.util;

import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import edu.upenn.cis.ppod.domain.PPodDnaMatrix;
import edu.upenn.cis.ppod.domain.PPodDnaNucleotide;
import edu.upenn.cis.ppod.domain.PPodDnaRow;
import edu.upenn.cis.ppod.domain.PPodOtu;
import edu.upenn.cis.ppod.domain.PPodOtuSet;
import edu.upenn.cis.ppod.domain.PPodStandardCell;
import edu.upenn.cis.ppod.domain.PPodStandardCharacter;
import edu.upenn.cis.ppod.domain.PPodStandardMatrix;
import edu.upenn.cis.ppod.domain.PPodStandardRow;
import edu.upenn.cis.ppod.domain.PPodStandardState;
import edu.upenn.cis.ppod.domain.PPodStudy;
import edu.upenn.cis.ppod.model.DnaCell;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Study;

public class DbStudy2DocStudy {

	public static PPodStudy dbStudy2DocStudy(final Study dbStudy) {
		final PPodStudy docStudy = new PPodStudy(dbStudy.getPPodId(), dbStudy
				.getVersionInfo().getVersion(), dbStudy.getLabel());
		for (final OtuSet dbOtuSet : dbStudy.getOtuSets()) {
			docStudy.getOtuSets().add(dbOtuSet2DocOtuSet(dbOtuSet));
		}
		return docStudy;
	}

	public static PPodOtuSet dbOtuSet2DocOtuSet(final OtuSet dbOtuSet) {
		final PPodOtuSet docOtuSet = new PPodOtuSet(dbOtuSet.getPPodId(),
				dbOtuSet.getVersionInfo().getVersion(), dbOtuSet.getLabel());

		for (final Otu dbOtu : dbOtuSet.getOtus()) {
			docOtuSet.getOtus().add(dbOtu2DocOtu(dbOtu));
		}

		return docOtuSet;
	}

	public static PPodOtu dbOtu2DocOtu(final Otu dbOtu) {
		final PPodOtu docOtu = new PPodOtu(dbOtu.getPPodId(), dbOtu
				.getVersionInfo().getVersion(), dbOtu.getLabel());
		return docOtu;
	}

	public static Set<PPodDnaNucleotide> A_G =
			EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.G);

	public static Set<PPodDnaNucleotide> C_T =
			EnumSet.of(PPodDnaNucleotide.C, PPodDnaNucleotide.T);

	public static Set<PPodDnaNucleotide> G_C =
			EnumSet.of(PPodDnaNucleotide.G, PPodDnaNucleotide.C);

	public static Set<PPodDnaNucleotide> A_T =
			EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.T);

	public static Set<PPodDnaNucleotide> G_T =
			EnumSet.of(PPodDnaNucleotide.G, PPodDnaNucleotide.T);

	public static Set<PPodDnaNucleotide> A_C =
			EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C);

	public static Set<PPodDnaNucleotide> C_G_T =
			EnumSet.of(PPodDnaNucleotide.C, PPodDnaNucleotide.G,
					PPodDnaNucleotide.T);

	public static Set<PPodDnaNucleotide> A_G_T =
			EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.G,
					PPodDnaNucleotide.T);

	public static Set<PPodDnaNucleotide> A_C_T =
			EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C,
					PPodDnaNucleotide.T);

	public static Set<PPodDnaNucleotide> A_C_G =
			EnumSet.of(PPodDnaNucleotide.A, PPodDnaNucleotide.C,
					PPodDnaNucleotide.G);

	public static char dbCell2IupacPlus(final DnaCell dbCell) {
		char iupacPlus;
		switch (dbCell.getType()) {
			case UNASSIGNED:
				iupacPlus = '?';
				break;
			case SINGLE:
				switch (getOnlyElement(dbCell.getElements())) {
					case A:
						if (dbCell.getLowerCase()) {
							iupacPlus = 'a';
						} else {
							iupacPlus = 'A';
						}
						break;
					case C:
						if (dbCell.getLowerCase()) {
							iupacPlus = 'c';
						} else {
							iupacPlus = 'C';
						}
						break;
					case G:
						if (dbCell.getLowerCase()) {
							iupacPlus = 'g';
						} else {
							iupacPlus = 'G';
						}
						break;
					case T:
						if (dbCell.getLowerCase()) {
							iupacPlus = 't';
						} else {
							iupacPlus = 'T';
						}
						break;
					default:
						break;
				}
			case POLYMORPHIC:
				if (dbCell.getElements().size() == 2) {
					if (dbCell.getElements().equals(A_G)) {
						iupacPlus = '!';
					} else if (dbCell.getElements().equals(C_T)) {
						iupacPlus = '@';
					} else if (dbCell.getElements().equals(G_C)) {
						iupacPlus = '#';
					} else if (dbCell.getElements().equals(A_T)) {
						iupacPlus = '$';
					} else if (dbCell.getElements().equals(G_T)) {
						iupacPlus = '%';
					} else if (dbCell.getElements().equals(A_C)) {
						iupacPlus = '^';
					}
				} else if (dbCell.getElements().size() == 3) {
					if (dbCell.getElements().equals(C_G_T)) {
						iupacPlus = '&';
					} else if (dbCell.getElements().equals(A_G_T)) {
						iupacPlus = '*';
					} else if (dbCell.getElements().equals(A_C_T)) {
						iupacPlus = '(';
					} else if (dbCell.getElements().equals(A_C_G)) {
						iupacPlus = ')';
					}
				} else if (dbCell.getElements().size() == 4) {
					iupacPlus = '_';
				} else {
					throw new AssertionError();
				}
			case UNCERTAIN:
				if (dbCell.getElements().size() == 2) {
					if (dbCell.getElements().equals(A_G)) {
						iupacPlus = 'R';
					} else if (dbCell.getElements().equals(C_T)) {
						iupacPlus = 'Y';
					} else if (dbCell.getElements().equals(G_C)) {
						iupacPlus = 'S';
					} else if (dbCell.getElements().equals(A_T)) {
						iupacPlus = 'W';
					} else if (dbCell.getElements().equals(G_T)) {
						iupacPlus = 'K';
					} else if (dbCell.getElements().equals(A_C)) {
						iupacPlus = 'M';
					}
				} else if (dbCell.getElements().size() == 3) {
					if (dbCell.getElements().equals(C_G_T)) {
						iupacPlus = 'B';
					} else if (dbCell.getElements().equals(A_G_T)) {
						iupacPlus = 'D';
					} else if (dbCell.getElements().equals(A_C_T)) {
						iupacPlus = 'H';
					} else if (dbCell.getElements().equals(A_C_G)) {
						iupacPlus = 'V';
					}
				} else if (dbCell.getElements().size() == 4) {
					iupacPlus = 'N';
				} else {
					throw new AssertionError();
				}
			case INAPPLICABLE:
				iupacPlus = '-';
				break;
			default:
				throw new AssertionError();
		}
		return iupacPlus;
	}

	public static PPodStandardMatrix dbStandardMatrix2DocStandardMatrix(
			final StandardMatrix dbMatrix) {
		final PPodStandardMatrix docMatrix = new PPodStandardMatrix(
				dbMatrix.getPPodId(), dbMatrix.getVersionInfo().getVersion(),
				dbMatrix.getLabel());

		for (final StandardCharacter dbCharacter : dbMatrix.getCharacters()) {
			final PPodStandardCharacter docCharacter = new PPodStandardCharacter(
					dbCharacter.getPPodId(),
					dbCharacter.getVersionInfo().getVersion(),
					dbCharacter.getLabel(),
					dbCharacter.getMesquiteId());
			docMatrix.getCharacters().add(docCharacter);
			for (final StandardState dbState : dbCharacter.getStates()) {
				final PPodStandardState docState = new PPodStandardState(
						dbState.getStateNumber(), dbState.getLabel());
				docCharacter.getStates().add(docState);
			}
		}

		for (final Otu dbOtu : dbMatrix.getParent().getOtus()) {
			final StandardRow dbRow = dbMatrix.getRows().get(dbOtu);
			final PPodStandardRow docRow = new PPodStandardRow(dbRow
					.getVersionInfo().getVersion());
			docMatrix.getRows().add(docRow);

			final List<StandardCell> dbCells = dbRow.getCells();
			for (final StandardCell dbCell : dbCells) {
				final PPodStandardCell docCell = new PPodStandardCell(
						dbCell.getVersion(), dbCell.getType());
				docRow.getCells().add(docCell);
				for (final StandardState state : dbCell.getElements()) {
					docCell.getStates().add(state.getStateNumber());
				}
			}
		}

		return docMatrix;
	}

	public static PPodDnaMatrix dbDnaMatrix2DocDnaMatrix(
			final DnaMatrix dbMatrix) {
		final PPodDnaMatrix docMatrix = new PPodDnaMatrix(
				dbMatrix.getPPodId(),
				dbMatrix.getVersionInfo().getVersion(), dbMatrix.getLabel());
		for (final Otu dbOtu : dbMatrix.getParent().getOtus()) {
			final DnaRow dbRow = dbMatrix.getRows().get(dbOtu);
			final List<DnaCell> dbCells = dbRow.getCells();
			final StringBuilder docSeq = new StringBuilder();
			for (final DnaCell dbCell : dbCells) {
				docSeq.append(dbCell2IupacPlus(dbCell));
			}
			final PPodDnaRow docRow = new PPodDnaRow(
					dbRow.getVersionInfo().getVersion(), docSeq.toString());
			docMatrix.getRows().add(docRow);
		}
		return docMatrix;
	}

	private DbStudy2DocStudy() {

	}
}
