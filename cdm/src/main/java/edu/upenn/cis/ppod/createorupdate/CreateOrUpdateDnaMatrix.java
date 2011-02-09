package edu.upenn.cis.ppod.createorupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IDnaRowDAO;
import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.DnaCell;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.util.DnaDocCell2DbCell;
import edu.upenn.cis.ppod.util.PPodSequenceTokenizer;

public class CreateOrUpdateDnaMatrix {
	private final IDnaRowDAO dnaRowDAO;
	private final INewVersionInfo newVersionInfo;
	private final static Logger logger = LoggerFactory
			.getLogger(CreateOrUpdateDnaMatrix.class);

	@Inject
	CreateOrUpdateDnaMatrix(
			final IDnaRowDAO proteinRowDAO,
			final INewVersionInfo newVersionInfo) {
		this.dnaRowDAO = proteinRowDAO;
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

				DnaDocCell2DbCell.docCell2DbCell(dbCell,
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
}
