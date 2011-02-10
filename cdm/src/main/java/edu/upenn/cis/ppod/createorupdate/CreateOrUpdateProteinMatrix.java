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

import edu.upenn.cis.ppod.dao.IProteinRowDAO;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodProtein;
import edu.upenn.cis.ppod.dto.PPodProteinMatrix;
import edu.upenn.cis.ppod.dto.PPodProteinRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.ProteinCell;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.ProteinRow;
import edu.upenn.cis.ppod.util.PPodSequenceTokenizer;

public class CreateOrUpdateProteinMatrix {
	private final IProteinRowDAO proteinRowDao;
	private final INewVersionInfo newVersionInfo;
	private final static Logger logger = LoggerFactory
			.getLogger(CreateOrUpdateProteinMatrix.class);

	@Inject
	CreateOrUpdateProteinMatrix(
			final IProteinRowDAO dao,
			final INewVersionInfo newVersionInfo) {
		this.proteinRowDao = dao;
		this.newVersionInfo = newVersionInfo;
	}

	public void createOrUpdateMatrix(
			final ProteinMatrix dbMatrix,
			final PPodProteinMatrix sourceMatrix) {

		final String METHOD = "createOrUpdateMatrix(...)";

		dbMatrix.setLabel(sourceMatrix.getLabel());

		int sourceOtuPos = -1;

		for (final PPodProteinRow sourceRow : sourceMatrix.getRows()) {

			sourceOtuPos++;

			final Otu dbOtu = dbMatrix.getParent()
							.getOtus()
							.get(sourceOtuPos);

			// Let's create rows for OTU->null row mappings in the matrix.
			ProteinRow dbRow = null;

			if (null == (dbRow = dbMatrix.getRows().get(dbOtu))) {
				dbRow = new ProteinRow();
				dbRow.setVersionInfo(newVersionInfo.getNewVersionInfo());
				dbMatrix.putRow(dbOtu, dbRow);
				proteinRowDao.makePersistent(dbRow);
			}

			final PPodSequenceTokenizer seqTokenizer = new PPodSequenceTokenizer(
					sourceRow.getSequence());

			int dbCellPosition = -1;

			while (seqTokenizer.hasMoreTokens()) {
				dbCellPosition++;
				ProteinCell dbCell = null;
				if (dbCellPosition < dbRow.getCells().size()) {
					dbCell = dbRow.getCells().get(dbCellPosition);
				} else {
					dbCell = new ProteinCell();
					dbRow.addCell(dbCell);
				}

				final PPodSequenceTokenizer.Token seqToken = seqTokenizer
						.nextToken();

				CreateOrUpdateProteinMatrix.docCell2DbCell(dbCell,
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

			proteinRowDao.flush();
			proteinRowDao.evict(dbRow);
		}
	}

	@VisibleForTesting
	static void docCell2DbCell(final ProteinCell dbCell,
			final PPodCellType cellType,
			final String docSequence) {
		checkNotNull(dbCell);
		checkNotNull(cellType);
		checkNotNull(docSequence);

		final Set<PPodProtein> proteins = EnumSet.noneOf(PPodProtein.class);

		for (int i = 0; i < docSequence.length(); i++) {
			final char docChar = docSequence.charAt(i);
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
				dbCell.setSingle(getOnlyElement(proteins));
				break;
			case POLYMORPHIC:
				checkArgument(docSequence.length() > 1);
				dbCell.setPolymorphic(proteins);
				break;
			case UNCERTAIN:
				checkArgument(docSequence.length() > 1);
				dbCell.setUncertain(proteins);
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
