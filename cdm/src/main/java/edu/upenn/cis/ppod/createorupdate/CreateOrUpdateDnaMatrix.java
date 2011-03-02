package edu.upenn.cis.ppod.createorupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IDnaRowDAO;
import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.Otu;

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
			}

			dbRow.setSequence(sourceRow.getSequence());

			dnaRowDAO.makePersistent(dbRow);

			logger.debug(
					"{}: finishing row number {}",
					METHOD,
					sourceOtuPos);
		}
	}
}
