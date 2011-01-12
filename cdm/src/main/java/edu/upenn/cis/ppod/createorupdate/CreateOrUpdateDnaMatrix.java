/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
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

import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.dto.PPodDnaMatrix;
import edu.upenn.cis.ppod.dto.PPodDnaRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.DnaCell;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.ModelFactory;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.util.DocCell2DbCell;

class CreateOrUpdateDnaMatrix implements ICreateOrUpdateDNAMatrix {

	private final IObjectWithLongIdDAO dao;
	private final INewVersionInfo newVersionInfo;
	private final static Logger logger = LoggerFactory
			.getLogger(CreateOrUpdateDnaMatrix.class);

	@Inject
	CreateOrUpdateDnaMatrix(
			final IObjectWithLongIdDAO dao,
			final INewVersionInfo newVersionInfo) {
		this.dao = dao;
		this.newVersionInfo = newVersionInfo;
	}

	public void createOrUpdateMatrix(
			final DnaMatrix dbMatrix,
			final PPodDnaMatrix sourceMatrix) {

		final String METHOD = "createOrUpdateMatrix(...)";

		dbMatrix.setColumnsSize(
				sourceMatrix.getRows().get(0).getSequence().length());

		dbMatrix.setLabel(sourceMatrix.getLabel());
		// dbMatrix.setDescription(sourceMatrix.getDescription());

		// So that makePersistenct(dbRow) below has a persistent parent.
		dao.makePersistent(dbMatrix);

		int sourceOTUPos = -1;

		for (final PPodDnaRow sourceRow : sourceMatrix.getRows()) {

			sourceOTUPos++;

			final Otu dbOTU =
					dbMatrix.getParent()
							.getOtus()
							.get(sourceOTUPos);

			// Let's create rows for OTU->null row mappings in the matrix.
			DnaRow dbRow = null;

			if (null == (dbRow = dbMatrix.getRows().get(dbOTU))) {
				dbRow = ModelFactory.newDnaRow(newVersionInfo
						.getNewVersionInfo());
				dbMatrix.putRow(dbOTU, dbRow);
				dao.makePersistent(dbRow);
			}

			final List<DnaCell> dbCells =
					newArrayListWithCapacity(sourceRow.getSequence().length());

			int i = -1;
			while (dbCells.size() < sourceRow.getSequence().length()) {
				i++;
				if (i < dbRow.getCells().size()) {
					dbCells.add(dbRow.getCells().get(i));
				} else {
					dbCells.add(ModelFactory.newDNACell(newVersionInfo
							.getNewVersionInfo()));
				}
			}

			dbRow.setCells(dbCells);

			int dbCellPosition = -1;
			for (final DnaCell dbCell : dbRow.getCells()) {
				dbCellPosition++;

				final char sourceCell = sourceRow
						.getSequence().charAt(dbCellPosition);

				DocCell2DbCell.docCell2DbCell(dbCell, sourceCell);

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
					sourceOTUPos);

			dao.flush();
			dao.evict(dbRow);
		}

	}

}
