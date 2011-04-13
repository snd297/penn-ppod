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

import edu.upenn.cis.ppod.PPodStandardCell;
import edu.upenn.cis.ppod.PPodStandardMatrix;
import edu.upenn.cis.ppod.PPodStandardRow;
import edu.upenn.cis.ppod.dao.IStandardRowDAO;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;

/**
 * @author Sam Donnelly
 */
abstract class CreateOrUpdateMatrix {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private final IStandardRowDAO rowDao;

	protected CreateOrUpdateMatrix(
				final IStandardRowDAO rowDao) {
		this.rowDao = rowDao;
	}

	public void createOrUpdateMatrixHelper(
			final StandardMatrix dbMatrix,
			final PPodStandardMatrix sourceMatrix,
			final int[] sourceToDbCharPositions) {

		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

		final String METHOD = "createOrUpdateMatrixHelper(...)";

		dbMatrix.setLabel(sourceMatrix.getLabel());

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
				dbRow = new StandardRow();
				dbMatrix.putRow(dbOTU, dbRow);
				rowDao.makePersistent(dbRow);
			}

			final List<StandardCell> dbCells =
					newArrayListWithCapacity(sourceRow.getCells().size());

			for (final int sourceToDbCharPosition : sourceToDbCharPositions) {
				if (sourceToDbCharPosition == -1) {
					final StandardCell newDbCell = new StandardCell();
					dbCells.add(newDbCell);
				} else {
					dbCells.add(
							dbRow.getCells().get(sourceToDbCharPosition));
				}
			}

			dbRow.clearAndAddCells(dbCells);

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
			}

			logger.debug(
					"{}: flushing row number {}",
					METHOD,
					sourceRowPos);

			rowDao.flush();
			rowDao.evict(dbRow);
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
