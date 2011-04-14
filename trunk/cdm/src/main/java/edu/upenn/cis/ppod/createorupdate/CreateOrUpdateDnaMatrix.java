/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.createorupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaRow;
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
