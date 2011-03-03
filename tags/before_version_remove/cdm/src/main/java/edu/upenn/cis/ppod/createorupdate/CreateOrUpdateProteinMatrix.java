package edu.upenn.cis.ppod.createorupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IProteinRowDAO;
import edu.upenn.cis.ppod.dto.PPodProteinMatrix;
import edu.upenn.cis.ppod.dto.PPodProteinRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.ProteinRow;

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
			}

			dbRow.setSequence(sourceRow.getSequence());

			proteinRowDao.makePersistent(dbRow);

			logger.debug(
					"{}: finished row number {}",
					METHOD,
					sourceOtuPos);

			// proteinRowDao.flush();
			// proteinRowDao.evict(dbRow);
		}
	}
}
