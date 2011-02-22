package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.EnumSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IDnaRowDAO;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaNucleotide;
import edu.upenn.cis.ppod.dto.PPodDnaRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.DnaCell;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.util.PPodSequenceTokenizer;

public class CreateOrUpdateDnaMatrix {
	private final IDnaRowDAO dnaRowDAO;
	private final INewVersionInfo newVersionInfo;
	private final static Logger logger = LoggerFactory
			.getLogger(CreateOrUpdateDnaMatrix.class);

	@Inject
	CreateOrUpdateDnaMatrix(
			final IDnaRowDAO dnaRowDAO,
			final INewVersionInfo newVersionInfo) {
		this.dnaRowDAO = dnaRowDAO;
		this.newVersionInfo = newVersionInfo;
	}

	public void createOrUpdateMatrix(
			final DnaMatrix dbMatrix,
			final PPodDnaMatrix sourceMatrix) {

		final String METHOD = "createOrUpdateMatrix(...)";

		dbMatrix.setLabel(sourceMatrix.getLabel());

		int sourceOtuPos = -1;

		for (final PPodDnaRow sourceRow : sourceMatrix.getRows()) {

			sourceOtuPos++;

			final Otu dbOtu = dbMatrix.getParent()
							.getOtus()
							.get(sourceOtuPos);

			// Let's create rows for OTU->null row mappings in the matrix.
			DnaRow dbRow = null;

			if (null == (dbRow = dbMatrix.getRows().get(dbOtu))) {
				dbRow = new DnaRow();
				dbRow.setVersionInfo(newVersionInfo.getNewVersionInfo());
				dbMatrix.putRow(dbOtu, dbRow);
				dnaRowDAO.makePersistent(dbRow);
			}

			final PPodSequenceTokenizer seqTokenizer = new PPodSequenceTokenizer(
					sourceRow.getSequence());

			int dbCellPosition = -1;

			while (seqTokenizer.hasMoreTokens()) {
				dbCellPosition++;
				DnaCell dbCell = null;
				if (dbCellPosition < dbRow.getCells().size()) {
					dbCell = dbRow.getCells().get(dbCellPosition);
				} else {
					dbCell = new DnaCell();
					dbRow.addCell(dbCell);
				}

				final PPodSequenceTokenizer.Token seqToken = seqTokenizer
						.nextToken();

				CreateOrUpdateDnaMatrix.docCell2DbCell(dbCell,
						seqToken.cellType,
						seqToken.sequence);

				// We need to do this here since we're removing the cell from
				// the persistence context (with evict). So it won't get handled
				// higher up in the application when it does for most entities.
				if (dbCell.isInNeedOfNewVersion()) {
					dbCell.setVersionInfo(
							newVersionInfo.getNewVersionInfo());
				}
			}

			// We need to do this here since we're removing the row from
			// the persistence context (with evict)
			if (dbRow.isInNeedOfNewVersion()) {
				dbRow.setVersionInfo(
						newVersionInfo.getNewVersionInfo());
			}

			logger.debug(
					"{}: flushing row number {}",
					METHOD,
					sourceOtuPos);

			dnaRowDAO.flush();
			dnaRowDAO.evict(dbRow);
		}
	}

	@VisibleForTesting
	static void docCell2DbCell(final DnaCell dbCell,
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
			} else {
				// if one is lower case, all of them should be
				checkArgument(lowerCase == false);
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
							"illegal char in sequence [" + docChar + "]");
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
