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
import edu.upenn.cis.ppod.domain.PPodStandardCell;
import edu.upenn.cis.ppod.domain.PPodStandardMatrix;
import edu.upenn.cis.ppod.domain.PPodStandardRow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.Cell;
import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.Row;
import edu.upenn.cis.ppod.model.VersionInfo;

/**
 * @author Sam Donnelly
 */
abstract class CreateOrUpdateMatrix<M extends Matrix<R, C>, R extends Row<C, ?>, C extends Cell<E, ?>, E> {

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
			final M dbMatrix,
			final PPodStandardMatrix sourceMatrix,
			final int[] sourceToDbCharPositions) {

		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

		final String METHOD = "createOrUpdate(...)";

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		if (dbMatrix.getDocId() == null) {
			dbMatrix.setDocId(sourceMatrix.getDocId());
		}

		dbMatrix.setLabel(sourceMatrix.getLabel());
		// dbMatrix.setDescription(sourceMatrix.getDescription());

		// So that makePersistenct(dbRow) below has a persistent parent.
		dao.makePersistent(dbMatrix);

		int sourceOTUPos = -1;

		for (final PPodStandardRow sourceRow : sourceMatrix.getRows()) {

			sourceOTUPos++;

			final Otu dbOTU =
					dbMatrix.getParent()
							.getOtus()
							.get(sourceOTUPos);

			// Let's create rows for OTU->null row mappings in the matrix.
			R dbRow = null;

			if (null == (dbRow = dbMatrix.getRows().get(dbOTU))) {
				dbRow = newR(newVersionInfo.getNewVersionInfo());
				dbMatrix.putRow(dbOTU, dbRow);
				dao.makePersistent(dbRow);
			}

			final List<C> dbCells =
					newArrayListWithCapacity(sourceRow.getCells().size());

			for (final int sourceToDbCharPosition : sourceToDbCharPositions) {
				if (sourceToDbCharPosition == -1) {
					final C newDbCell = newC(newVersionInfo.getNewVersionInfo());
					dbCells.add(newDbCell);
				} else {
					dbCells.add(
							dbRow.getCells().get(sourceToDbCharPosition));
				}
			}

			dbRow.setCells(dbCells);

			int dbCellPosition = -1;
			for (final C dbCell : dbRow.getCells()) {
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
					sourceOTUPos);

			dao.flush();
			dao.evict(dbRow);
		}
	}

	protected abstract C newC(VersionInfo newVersionInfo);

	protected abstract R newR(VersionInfo newVersionInfo);

	protected abstract void handlePolymorphicCell(final C targetCell,
			final PPodStandardCell sourceCell);

	protected abstract void handleSingleCell(final C targetCell,
			final PPodStandardCell sourceCell);

	protected abstract void handleUncertainCell(final C targetCell,
			final PPodStandardCell sourceCell);
}
