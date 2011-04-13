package edu.upenn.cis.ppod.createorupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.upenn.cis.ppod.PPodDnaMatrix;
import edu.upenn.cis.ppod.PPodDnaRow;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.Otu;

public class CreateOrUpdateDnaMatrix {
	private final static Logger logger = LoggerFactory
			.getLogger(CreateOrUpdateDnaMatrix.class);

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
				dbMatrix.putRow(dbOtu, dbRow);
			}

			dbRow.setSequence(sourceRow.getSequence());

			logger.debug(
					"{}: finishing row number {}",
					METHOD,
					sourceOtuPos);
		}
	}
}
