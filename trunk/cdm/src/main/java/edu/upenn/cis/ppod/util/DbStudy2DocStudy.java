package edu.upenn.cis.ppod.util;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.A_C;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.A_C_G;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.A_C_T;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.A_G;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.A_G_T;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.A_T;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.C_G_T;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.C_T;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.G_C;
import static edu.upenn.cis.ppod.dto.PPodDnaNucleotide.G_T;

import java.util.List;

import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaRow;
import edu.upenn.cis.ppod.dto.PPodDnaSequence;
import edu.upenn.cis.ppod.dto.PPodDnaSequenceSet;
import edu.upenn.cis.ppod.dto.PPodOtu;
import edu.upenn.cis.ppod.dto.PPodOtuSet;
import edu.upenn.cis.ppod.dto.PPodStandardCell;
import edu.upenn.cis.ppod.dto.PPodStandardCharacter;
import edu.upenn.cis.ppod.dto.PPodStandardMatrix;
import edu.upenn.cis.ppod.dto.PPodStandardRow;
import edu.upenn.cis.ppod.dto.PPodStandardState;
import edu.upenn.cis.ppod.dto.PPodStudy;
import edu.upenn.cis.ppod.model.DnaCell;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.DnaSequence;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.VersionInfo;

public class DbStudy2DocStudy {

	public static char dbCell2IupacPlus(final DnaCell dbCell) {
		char iupacPlus = (char) -1;
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
						throw new AssertionError();
				}
				break;
			case POLYMORPHIC:
				if (dbCell.getElements().size() == 2) {
					if (dbCell.getElements().equals(A_G)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '0';
						} else {
							iupacPlus = ')';
						}
					} else if (dbCell.getElements().equals(C_T)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '1';
						} else {
							iupacPlus = '!';
						}
					} else if (dbCell.getElements().equals(G_C)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '2';
						} else {
							iupacPlus = '@';
						}
					} else if (dbCell.getElements().equals(A_T)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '3';
						} else {
							iupacPlus = '#';
						}
					} else if (dbCell.getElements().equals(G_T)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '4';
						} else {
							iupacPlus = '$';
						}
					} else if (dbCell.getElements().equals(A_C)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '5';
						} else {
							iupacPlus = '%';
						}
					}
				} else if (dbCell.getElements().size() == 3) {
					if (dbCell.getElements().equals(C_G_T)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '6';
						} else {
							iupacPlus = '^';
						}
					} else if (dbCell.getElements().equals(A_G_T)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '7';
						} else {
							iupacPlus = '&';
						}
					} else if (dbCell.getElements().equals(A_C_T)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '8';
						} else {
							iupacPlus = '*';
						}
					} else if (dbCell.getElements().equals(A_C_G)) {
						if (dbCell.getLowerCase()) {
							iupacPlus = '9';
						} else {
							iupacPlus = '(';
						}
					} else {
						throw new AssertionError();
					}
				} else if (dbCell.getElements().size() == 4) {
					if (dbCell.getLowerCase()) {
						iupacPlus = '=';
					} else {
						iupacPlus = '+';
					}
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
				break;
			case INAPPLICABLE:
				iupacPlus = '-';
				break;
			default:
				throw new AssertionError();
		}
		if (iupacPlus == (char) -1) {
			throw new AssertionError();
		}
		return iupacPlus;
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
			final List<Long> cellVersions = newArrayList();
			for (final DnaCell dbCell : dbCells) {
				docSeq.append(dbCell2IupacPlus(dbCell));
				cellVersions.add(dbCell.getVersionInfo().getVersion());
			}
			final PPodDnaRow docRow = new PPodDnaRow(
					dbRow.getVersionInfo().getVersion(), docSeq.toString());
			docRow.setCellVersions(cellVersions);
			docMatrix.getRows().add(docRow);
		}
		return docMatrix;
	}

	public static PPodDnaSequenceSet dbDnaSequenceSet2DocDnaSequenceSet(
			final DnaSequenceSet dbSequenceSet) {
		final PPodDnaSequenceSet docSequenceSet = new PPodDnaSequenceSet(
				dbSequenceSet.getPPodId(), dbSequenceSet.getVersionInfo()
						.getVersion(), dbSequenceSet.getLabel());
		for (final Otu dbOtu : dbSequenceSet.getParent().getOtus()) {
			final DnaSequence dbSequence = dbSequenceSet.getSequence(dbOtu);
			final PPodDnaSequence docSequence =
					new PPodDnaSequence(
							dbSequence.getVersionInfo().getVersion(),
							dbSequence.getSequence(),
							dbSequence.getName(),
							dbSequence.getDescription(),
							dbSequence.getAccession());
			docSequenceSet.getSequences().add(docSequence);
		}
		return docSequenceSet;
	}

	public static PPodOtu dbOtu2DocOtu(final Otu dbOtu) {
		final PPodOtu docOtu = new PPodOtu(
				dbOtu.getPPodId(),
				dbOtu.getVersionInfo().getVersion(),
				dbOtu.getPPodId(),
				dbOtu.getLabel());
		return docOtu;
	}

	public static PPodOtuSet dbOtuSet2DocOtuSet(final OtuSet dbOtuSet) {
		final PPodOtuSet docOtuSet = new PPodOtuSet(dbOtuSet.getPPodId(),
				dbOtuSet.getVersionInfo().getVersion(), dbOtuSet.getLabel());

		for (final Otu dbOtu : dbOtuSet.getOtus()) {
			docOtuSet.getOtus().add(dbOtu2DocOtu(dbOtu));
		}

		for (final StandardMatrix dbMatrix : dbOtuSet.getStandardMatrices()) {
			docOtuSet.getStandardMatrices().add(
					dbStandardMatrix2DocStandardMatrix(dbMatrix));
		}

		for (final DnaMatrix dbMatrix : dbOtuSet.getDnaMatrices()) {
			docOtuSet.getDnaMatrices().add(
					dbDnaMatrix2DocDnaMatrix(dbMatrix));
		}

		for (final DnaSequenceSet dbSequenceSet : dbOtuSet.getDnaSequenceSets()) {
			docOtuSet.getDnaSequenceSets().add(
					dbDnaSequenceSet2DocDnaSequenceSet(dbSequenceSet));
		}

		return docOtuSet;
	}

	public static PPodStandardMatrix dbStandardMatrix2DocStandardMatrix(
			final StandardMatrix dbMatrix) {
		final PPodStandardMatrix docMatrix = new PPodStandardMatrix(
				dbMatrix.getPPodId(), dbMatrix.getVersionInfo().getVersion(),
				dbMatrix.getLabel());
		for (final VersionInfo columnVersionInfo : dbMatrix
				.getColumnVersionInfos()) {
			docMatrix.getColumnVersions().add(columnVersionInfo.getVersion());
		}
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
				final PPodStandardCell docCell =
						new PPodStandardCell(
								dbCell.getVersionInfo().getVersion(),
								dbCell.getType(),
								newHashSet(
										transform(dbCell.getElements(),
												StandardState.getStateNumber)));
				docRow.getCells().add(docCell);
			}
		}

		return docMatrix;
	}

	public static PPodStudy dbStudy2DocStudy(final Study dbStudy) {
		final PPodStudy docStudy = new PPodStudy(dbStudy.getPPodId(), dbStudy
				.getVersionInfo().getVersion(), dbStudy.getLabel());
		for (final OtuSet dbOtuSet : dbStudy.getOtuSets()) {
			docStudy.getOtuSets().add(dbOtuSet2DocOtuSet(dbOtuSet));
		}
		return docStudy;
	}

	private DbStudy2DocStudy() {

	}
}
