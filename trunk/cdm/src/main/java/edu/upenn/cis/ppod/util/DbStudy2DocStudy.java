package edu.upenn.cis.ppod.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaRow;
import edu.upenn.cis.ppod.dto.PPodDnaSequence;
import edu.upenn.cis.ppod.dto.PPodDnaSequenceSet;
import edu.upenn.cis.ppod.dto.PPodOtu;
import edu.upenn.cis.ppod.dto.PPodOtuSet;
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
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.DnaSequence;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
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

public final class DbStudy2DocStudy {

	public PPodDnaMatrix dbDnaMatrix2DocDnaMatrix(
			final DnaMatrix dbMatrix) {
		checkNotNull(dbMatrix);
		final PPodDnaMatrix docMatrix =
				new PPodDnaMatrix(
						dbMatrix.getPPodId(),
						dbMatrix.getLabel());

		for (final Otu dbOtu : dbMatrix.getParent().getOtus()) {
			final DnaRow dbRow = dbMatrix.getRows().get(dbOtu);
			final PPodDnaRow docRow = new PPodDnaRow(
					dbRow.getSequence());
			docMatrix.getRows().add(docRow);
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
				dbSequenceSet.getPPodId(), dbSequenceSet.getLabel());
		for (final Otu dbOtu : dbSequenceSet.getParent().getOtus()) {
			final DnaSequence dbSequence = dbSequenceSet.getSequences().get(
					dbOtu);
			final PPodDnaSequence docSequence =
					new PPodDnaSequence(
							dbSequence.getSequence(),
							dbSequence.getName(),
							dbSequence.getDescription(),
							dbSequence.getAccession());
			docSequenceSet.getSequences().add(docSequence);
		}
		return docSequenceSet;
	}

	public PPodOtu dbOtu2DocOtu(final Otu dbOtu) {
		return new PPodOtu(
				dbOtu.getPPodId(),
				dbOtu.getLabel(),
				dbOtu.getPPodId());
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
				dbOtuSet.getLabel());

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
						dbMatrix.getLabel());

		for (final Otu dbOtu : dbMatrix.getParent().getOtus()) {
			final ProteinRow dbRow = dbMatrix.getRows().get(dbOtu);
			final PPodProteinRow docRow =
					new PPodProteinRow(
							dbRow.getSequence());
			docMatrix.getRows().add(docRow);
		}
		return docMatrix;
	}

	public PPodStandardMatrix dbStandardMatrix2DocStandardMatrix(
						final StandardMatrix dbMatrix) {
		checkNotNull(dbMatrix);
		final PPodStandardMatrix docMatrix =
				new PPodStandardMatrix(
						dbMatrix.getPPodId(),
						dbMatrix.getLabel());
		for (final StandardCharacter dbCharacter : dbMatrix.getCharacters()) {
			final PPodStandardCharacter docCharacter = new PPodStandardCharacter(
					dbCharacter.getPPodId(),
					dbCharacter.getLabel(),
					dbCharacter.getMesquiteId());
			docMatrix.getCharacters().add(docCharacter);
			for (final StandardState dbState : dbCharacter.getStates().values()) {
				final PPodStandardState docState = new PPodStandardState(
						dbState.getStateNumber(), dbState.getLabel());
				docCharacter.getStates().add(docState);
			}
		}

		for (final Otu dbOtu : dbMatrix.getParent().getOtus()) {
			final StandardRow dbRow = dbMatrix.getRows().get(dbOtu);
			final PPodStandardRow docRow = new PPodStandardRow();
			docMatrix.getRows().add(docRow);

			for (final StandardCell dbCell : dbRow.getCells()) {
				final PPodStandardCell docCell =
						new PPodStandardCell(
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
		final PPodStudy docStudy = new PPodStudy(dbStudy.getPPodId(),
				dbStudy.getLabel());
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
						dbTreeSet.getLabel());
		for (final Tree dbTree : dbTreeSet.getTrees()) {
			final PPodTree docTree =
					new PPodTree(
							dbTree.getPPodId(),
							dbTree.getLabel(),
							dbTree.getNewick());
			docTreeSet.getTrees().add(docTree);
		}
		return docTreeSet;
	}
}
