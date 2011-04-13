package edu.upenn.cis.ppod.createorupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.upenn.cis.ppod.PPodProteinMatrix;
import edu.upenn.cis.ppod.PPodProteinRow;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.model.ProteinRow;

public class CreateOrUpdateProteinMatrix {

	private final static Logger logger = LoggerFactory
			.getLogger(CreateOrUpdateProteinMatrix.class);

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
				dbMatrix.putRow(dbOtu, dbRow);
			}

			dbRow.setSequence(sourceRow.getSequence());

			logger.debug(
					"{}: finished row number {}",
					METHOD,
					sourceOtuPos);

			// proteinRowDao.flush();
			// proteinRowDao.evict(dbRow);
		}
	}
}
