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
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.ICell;
import edu.upenn.cis.ppod.imodel.IMatrix;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IRow;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

/**
 * @author Sam Donnelly
 */
abstract class CreateOrUpdateMatrix<M extends IMatrix<R, C>, R extends IRow<C, ?>, C extends ICell<E, ?>, E>
		implements ICreateOrUpdateMatrix<M, R, C, E> {

	private final Provider<C> cellProvider;

	private final IDAO<Object, Long> dao;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private final INewVersionInfo newVersionInfo;

	private final Provider<R> rowProvider;

	@Inject
	CreateOrUpdateMatrix(
			final Provider<R> rowProvider,
			final Provider<C> cellProvider,
			final Provider<IAttachment> attachmentProvider,
			@Assisted final INewVersionInfo newVersionInfo,
			@Assisted final IDAO<Object, Long> dao) {
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.newVersionInfo = newVersionInfo;
		this.dao = dao;
	}

	/**
	 * Put the state of the source cell into the target cell.
	 * 
	 * @param targetCell target
	 * @param sourceCell source
	 */
	abstract void handleCell(final C targetCell, final C sourceCell);

	public void createOrUpdateMatrix(
			final M dbMatrix,
			final M sourceMatrix) {

		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

		final String METHOD = "createOrUpdate(...)";

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		if (dbMatrix.getDocId() == null) {
			dbMatrix.setDocId(sourceMatrix.getDocId());
		}

		dbMatrix.setLabel(sourceMatrix.getLabel());
		dbMatrix.setDescription(sourceMatrix.getDescription());

		// So that makePersistenct(dbRow) below has a persistent parent.
		dao.makePersistent(dbMatrix);

		int sourceOTUPos = -1;

		for (final IOTU sourceOTU : sourceMatrix.getParent().getOTUs()) {
			sourceOTUPos++;
			final R sourceRow = sourceMatrix.getRows().get(sourceOTU);

			final IOTU dbOTU =
					dbMatrix.getParent()
							.getOTUs()
							.get(sourceOTUPos);

			// Let's create rows for OTU->null row mappings in the matrix.
			R dbRow = null;

			if (null == (dbRow = dbMatrix.getRows().get(dbOTU))) {
				dbRow = rowProvider.get();
				dbRow.setVersionInfo(
						newVersionInfo.getNewVersionInfo());
				dbMatrix.putRow(dbOTU, dbRow);
				dao.makePersistent(dbRow);
			}

			final List<C> dbCells = newArrayList(dbRow.getCells());

			// Add in cells to dbCells if needed.
			while (dbCells.size() < sourceRow.getCells().size()) {
				final C dbCell = cellProvider.get();
				dbCells.add(dbCell);
				dbCell.setVersionInfo(newVersionInfo.getNewVersionInfo());
			}

			// Get rid of cells from dbCells if needed
			while (dbCells.size() > sourceRow.getCells().size()) {
				dbCells.remove(dbCells.size() - 1);
			}

			dbRow.setCells(dbCells);

			int dbCellPosition = -1;
			for (final C dbCell : dbRow.getCells()) {
				dbCellPosition++;

				final C sourceCell = sourceRow
						.getCells()
						.get(dbCellPosition);

				handleCell(dbCell, sourceCell);

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
