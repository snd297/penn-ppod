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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.dto.PPodStandardCell;
import edu.upenn.cis.ppod.dto.PPodStandardMatrix;
import edu.upenn.cis.ppod.dto.PPodStandardRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.ModelFactory;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;

/**
 * @author Sam Donnelly
 */
abstract class CreateOrUpdateMatrix {

	private final IObjectWithLongIdDAO dao;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private final INewVersionInfo newVersionInfo;

	protected CreateOrUpdateMatrix(
			final IObjectWithLongIdDAO dao,
			final INewVersionInfo newVersionInfo) {
		this.newVersionInfo = newVersionInfo;
		this.dao = dao;
	}

	public void createOrUpdateMatrixHelper(
			final StandardMatrix dbMatrix,
			final PPodStandardMatrix sourceMatrix,
			final int[] sourceToDbCharPositions) {

		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

		final String METHOD = "createOrUpdate(...)";

		dbMatrix.setLabel(sourceMatrix.getLabel());
		// dbMatrix.setDescription(sourceMatrix.getDescription());

		// So that makePersistenct(dbRow) below has a persistent parent.
		dao.makePersistent(dbMatrix);

		int sourceRowPos = -1;

		for (final PPodStandardRow sourceRow : sourceMatrix.getRows()) {

			sourceRowPos++;

			final Otu dbOTU =
					dbMatrix.getParent()
							.getOtus()
							.get(sourceRowPos);

			// Let's create rows for OTU->null row mappings in the matrix.
			StandardRow dbRow = null;

			if (null == (dbRow = dbMatrix.getRows().get(dbOTU))) {
				dbRow = ModelFactory.newStandardRow(newVersionInfo
						.getNewVersionInfo());
				dbMatrix.putRow(dbOTU, dbRow);
				dao.makePersistent(dbRow);
			}

			final List<StandardCell> dbCells =
					newArrayListWithCapacity(sourceRow.getCells().size());

			for (final int sourceToDbCharPosition : sourceToDbCharPositions) {
				if (sourceToDbCharPosition == -1) {
					final StandardCell newDbCell = new StandardCell();
					newDbCell
							.setVersionInfo(newVersionInfo.getNewVersionInfo());
					dbCells.add(newDbCell);
				} else {
					dbCells.add(
							dbRow.getCells().get(sourceToDbCharPosition));
				}
			}

			dbRow.setCells(dbCells);

			int dbCellPosition = -1;
			for (final StandardCell dbCell : dbRow.getCells()) {
				dbCellPosition++;

				final PPodStandardCell sourceCell = sourceRow
						.getCells()
						.get(dbCellPosition);

				switch (sourceCell.getType()) {
					case UNASSIGNED:
						dbCell.setUnassigned();
						break;
					case SINGLE:
						handleSingleCell(dbCell, sourceCell);
						break;
					case POLYMORPHIC:
						handlePolymorphicCell(dbCell, sourceCell);
						break;
					case UNCERTAIN:
						handleUncertainCell(dbCell, sourceCell);
						break;
					case INAPPLICABLE:
						dbCell.setInapplicable();
						break;
					default:
						throw new AssertionError("unknown cell type");
				}

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
					sourceRowPos);

			dao.flush();
			dao.evict(dbRow);
		}
	}

	protected abstract void handlePolymorphicCell(
			final StandardCell targetCell,
			final PPodStandardCell sourceCell);

	protected abstract void handleSingleCell(final StandardCell targetCell,
			final PPodStandardCell sourceCell);

	protected abstract void handleUncertainCell(final StandardCell targetCell,
			final PPodStandardCell sourceCell);
}
