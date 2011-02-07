package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IProteinRowDAO;
import edu.upenn.cis.ppod.dto.PPodProteinMatrix;
import edu.upenn.cis.ppod.dto.PPodProteinRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.ProteinCell;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.ProteinRow;
import edu.upenn.cis.ppod.util.ProteinDocCell2DbCell;

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

			final List<ProteinCell> dbCells =
					newArrayListWithCapacity(sourceRow.getSequence().length());

			int i = -1;
			while (dbCells.size() < sourceRow.getSequence().length()) {
				i++;
				if (i < dbRow.getCells().size()) {
					dbCells.add(dbRow.getCells().get(i));
				} else {
					final ProteinCell dbCell = new ProteinCell();
					dbCell.setVersionInfo(newVersionInfo
							.getNewVersionInfo());
					dbCells.add(dbCell);
				}
			}

			dbRow.setCells(dbCells);

			int dbCellPosition = -1;
			for (final ProteinCell dbCell : dbRow.getCells()) {
				dbCellPosition++;

				final char sourceCell = sourceRow
						.getSequence().charAt(dbCellPosition);

				ProteinDocCell2DbCell.docCell2DbCell(dbCell, sourceCell);

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
}
