package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IDnaRowDAO;
import edu.upenn.cis.ppod.dao.IProteinRowDAO;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaNucleotide;
import edu.upenn.cis.ppod.dto.PPodDnaRow;
import edu.upenn.cis.ppod.dto.PPodDnaSequence;
import edu.upenn.cis.ppod.dto.PPodDnaSequenceSet;
import edu.upenn.cis.ppod.dto.PPodOtu;
import edu.upenn.cis.ppod.dto.PPodOtuSet;
import edu.upenn.cis.ppod.dto.PPodProtein;
import edu.upenn.cis.ppod.dto.PPodProteinMatrix;
import edu.upenn.cis.ppod.dto.PPodProteinRow;
import edu.upenn.cis.ppod.dto.PPodStandardCell;
import edu.upenn.cis.ppod.dto.PPodStandardCharacter;
import edu.upenn.cis.ppod.dto.PPodStandardMatrix;
import edu.upenn.cis.ppod.dto.PPodStandardRow;
import edu.upenn.cis.ppod.dto.PPodStandardState;
import edu.upenn.cis.ppod.dto.PPodStudy;
import edu.upenn.cis.ppod.dto.PPodTree;
import edu.upenn.cis.ppod.dto.PPodTreeSet;
import edu.upenn.cis.ppod.model.DnaCell;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.DnaSequence;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.ProteinCell;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.ProteinRow;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.model.VersionInfo;

public final class DbStudy2DocStudy {

	public static void dbDnaCell2Sequence(final DnaCell dnaCell,
			final StringBuilder sequence) {
		checkNotNull(dnaCell);
		checkNotNull(sequence);

		switch (dnaCell.getType()) {
			case UNASSIGNED:
				sequence.append('?');
				break;
			case INAPPLICABLE:
				sequence.append('-');
				break;
			case SINGLE:
				break;
			case POLYMORPHIC:
				sequence.append('(');
				break;
			case UNCERTAIN:
				sequence.append('{');
				break;
			default:
				throw new AssertionError();
		}

		for (final PPodDnaNucleotide nucleotide : dnaCell.getElements()) {
			char nucleotideChar = nucleotide.toString().charAt(0);
			if ((dnaCell.getType() == PPodCellType.POLYMORPHIC
					|| dnaCell.getType() == PPodCellType.SINGLE)
					&& dnaCell.getLowerCase()) {
				nucleotideChar = Character.toLowerCase(nucleotideChar);
			}
			sequence.append(nucleotideChar);
		}

		switch (dnaCell.getType()) {
			case UNASSIGNED:
			case INAPPLICABLE:
			case SINGLE:
				break;
			case POLYMORPHIC:
				sequence.append(')');
				break;
			case UNCERTAIN:
				sequence.append('}');
				break;
			default:
				throw new AssertionError();
		}
	}

	public static void dbProteinCell2Sequence(final ProteinCell proteinCell,
			final StringBuilder sequence) {
		checkNotNull(proteinCell);
		checkNotNull(sequence);

		switch (proteinCell.getType()) {
			case UNASSIGNED:
				sequence.append('?');
				break;
			case INAPPLICABLE:
				sequence.append('-');
				break;
			case SINGLE:
				break;
			case POLYMORPHIC:
				sequence.append('(');
				break;
			case UNCERTAIN:
				sequence.append('{');
				break;
			default:
				throw new AssertionError();
		}

		for (final PPodProtein protein : proteinCell.getElements()) {
			sequence.append(protein.toString());
		}

		switch (proteinCell.getType()) {
			case UNASSIGNED:
			case INAPPLICABLE:
			case SINGLE:
				break;
			case POLYMORPHIC:
				sequence.append(')');
				break;
			case UNCERTAIN:
				sequence.append('}');
				break;
			default:
				throw new AssertionError();
		}
	}

	private final IDnaRowDAO dnaRowDao;

	private final IProteinRowDAO proteinRowDao;

	@Inject
	DbStudy2DocStudy(final IDnaRowDAO dnaRowDao,
			final IProteinRowDAO proteinRowDao) {
		this.dnaRowDao = dnaRowDao;
		this.proteinRowDao = proteinRowDao;
	}

	public PPodDnaMatrix dbDnaMatrix2DocDnaMatrix(
			final DnaMatrix dbMatrix) {
		checkNotNull(dbMatrix);
		final PPodDnaMatrix docMatrix =
				new PPodDnaMatrix(
						dbMatrix.getPPodId(),
						dbMatrix.getVersionInfo().getVersion(),
						dbMatrix.getLabel());

		for (final Otu dbOtu : dbMatrix.getParent().getOtus()) {
			final DnaRow dbRow = dbMatrix.getRows().get(dbOtu);
			final StringBuilder docSeq = new StringBuilder();
			final List<Long> cellVersions = newArrayList();
			for (final DnaCell dbCell : dbRow.getCells()) {
				dbDnaCell2Sequence(dbCell, docSeq);
				cellVersions.add(dbCell.getVersionInfo().getVersion());
			}
			final PPodDnaRow docRow = new PPodDnaRow(
					dbRow.getVersionInfo().getVersion(),
					docSeq.toString());
			docMatrix.getRows().add(docRow);
			docRow.setCellVersions(cellVersions);
			dnaRowDao.evict(dbRow);
		}
		return docMatrix;
	}

	/**
	 * This method evicts rows from the persistence context after they are
	 * processed.
	 * 
	 * @param dbMatrix to be converted
	 * 
	 * @return a doc version of {@code dbMatrix}
	 */
	public PPodDnaSequenceSet dbDnaSequenceSet2DocDnaSequenceSet(
			final DnaSequenceSet dbSequenceSet) {
		checkNotNull(dbSequenceSet);
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

	public PPodOtu dbOtu2DocOtu(final Otu dbOtu) {
		final PPodOtu docOtu = new PPodOtu(
				dbOtu.getPPodId(),
				dbOtu.getVersionInfo().getVersion(),
				dbOtu.getPPodId(),
				dbOtu.getLabel());
		return docOtu;
	}

	public PPodOtuSet dbOtuSet2DocOtuSet(final OtuSet dbOtuSet) {
		final PPodOtuSet docOtuSet = dbOtuSet2DocOtuSetJustOtus(dbOtuSet);

		for (final ProteinMatrix dbMatrix : dbOtuSet.getProteinMatrices()) {
			docOtuSet.getProteinMatrices().add(
					dbProteinMatrix2DocProteinMatrix(dbMatrix));
		}

		for (final DnaMatrix dbMatrix : dbOtuSet.getDnaMatrices()) {
			docOtuSet.getDnaMatrices().add(
					dbDnaMatrix2DocDnaMatrix(dbMatrix));
		}

		for (final StandardMatrix dbMatrix : dbOtuSet.getStandardMatrices()) {
			docOtuSet.getStandardMatrices().add(
					dbStandardMatrix2DocStandardMatrix(dbMatrix));
		}

		for (final DnaSequenceSet dbSequenceSet : dbOtuSet.getDnaSequenceSets()) {
			docOtuSet.getDnaSequenceSets().add(
					dbDnaSequenceSet2DocDnaSequenceSet(dbSequenceSet));
		}

		for (final TreeSet dbTreeSet : dbOtuSet.getTreeSets()) {
			docOtuSet.getTreeSets().add(dbTreeSet2DocTreeSet(dbTreeSet));
		}

		return docOtuSet;
	}

	public PPodOtuSet dbOtuSet2DocOtuSetJustOtus(final OtuSet dbOtuSet) {
		final PPodOtuSet docOtuSet = new PPodOtuSet(dbOtuSet.getPPodId(),
				dbOtuSet.getVersionInfo().getVersion(), dbOtuSet.getLabel());

		for (final Otu dbOtu : dbOtuSet.getOtus()) {
			docOtuSet.getOtus().add(dbOtu2DocOtu(dbOtu));
		}
		return docOtuSet;
	}

	public PPodProteinMatrix dbProteinMatrix2DocProteinMatrix(
			final ProteinMatrix dbMatrix) {
		checkNotNull(dbMatrix);
		final PPodProteinMatrix docMatrix =
				new PPodProteinMatrix(
						dbMatrix.getPPodId(),
						dbMatrix.getVersionInfo().getVersion(),
						dbMatrix.getLabel());

		for (final Otu dbOtu : dbMatrix.getParent().getOtus()) {
			final ProteinRow dbRow = dbMatrix.getRows().get(dbOtu);
			final StringBuilder docSeq = new StringBuilder();
			for (final ProteinCell dbCell : dbRow.getCells()) {
				dbProteinCell2Sequence(dbCell, docSeq);
			}
			final PPodProteinRow docRow =
					new PPodProteinRow(
							dbRow.getVersionInfo().getVersion(),
							docSeq.toString());
			docMatrix.getRows().add(docRow);
			proteinRowDao.evict(dbRow);
		}
		return docMatrix;
	}

	public PPodStandardMatrix dbStandardMatrix2DocStandardMatrix(
						final StandardMatrix dbMatrix) {
		checkNotNull(dbMatrix);
		final PPodStandardMatrix docMatrix =
				new PPodStandardMatrix(
						dbMatrix.getPPodId(),
						dbMatrix.getVersionInfo().getVersion(),
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

			for (final StandardCell dbCell : dbRow.getCells()) {
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

	public PPodStudy dbStudy2DocStudy(final Study dbStudy) {
		checkNotNull(dbStudy);
		final PPodStudy docStudy = new PPodStudy(dbStudy.getPPodId(), dbStudy
				.getVersionInfo().getVersion(), dbStudy.getLabel());
		for (final OtuSet dbOtuSet : dbStudy.getOtuSets()) {
			docStudy.getOtuSets().add(dbOtuSet2DocOtuSet(dbOtuSet));
		}
		return docStudy;
	}

	public PPodTreeSet dbTreeSet2DocTreeSet(final TreeSet dbTreeSet) {
		checkNotNull(dbTreeSet);
		final PPodTreeSet docTreeSet =
				new PPodTreeSet(
						dbTreeSet.getPPodId(),
						dbTreeSet.getVersionInfo().getVersion(),
						dbTreeSet.getLabel());
		for (final Tree dbTree : dbTreeSet.getTrees()) {
			final PPodTree docTree =
					new PPodTree(
							dbTree.getPPodId(),
							dbTree.getVersionInfo().getVersion(),
							dbTree.getLabel(),
							dbTree.getNewick());
			docTreeSet.getTrees().add(docTree);
		}
		return docTreeSet;
	}
}
