package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.Cell;
import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Row;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;

/**
 * @author Sam Donnelly
 */
class CreateOrUpdateMatrix<M extends Matrix<R>, R extends Row<C>, C extends Cell<E>, E>
		implements ICreateOrUpdateMatrix<M, R, C, E> {

	private final Provider<C> cellProvider;

	private final IDAO<Object, Long> dao;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private final Provider<MatrixInfo> matrixInfoProvider;

	private final INewVersionInfo newVersionInfo;

	private final Provider<R> rowProvider;

	@Inject
	CreateOrUpdateMatrix(
			final Provider<R> rowProvider,
			final Provider<C> cellProvider,
			final Provider<Attachment> attachmentProvider,
			final Provider<MatrixInfo> matrixInfoProvider,
			@Assisted final INewVersionInfo newVersionInfo,
			@Assisted final IDAO<Object, Long> dao) {
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.matrixInfoProvider = matrixInfoProvider;
		this.newVersionInfo = newVersionInfo;
		this.dao = dao;
	}

	public MatrixInfo createOrUpdateMatrix(
			final M dbMatrix,
			final M sourceMatrix) {

		final String METHOD = "createOrUpdate(...)";

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		if (dbMatrix.getDocId() == null) {
			dbMatrix.setDocId(sourceMatrix.getDocId());
		}

		dbMatrix.setLabel(sourceMatrix.getLabel())
				.setDescription(sourceMatrix.getDescription());

		final MatrixInfo matrixInfo = matrixInfoProvider.get();
		matrixInfo.setPPodId(dbMatrix.getPPodId());

		// So the rows have a dbMatrix id
		dao.makePersistent(dbMatrix);

		final Set<C> cellsToEvict = newHashSet();

		int sourceOTUPosition = -1;

		for (final OTU sourceOTU : sourceMatrix.getOTUSet().getOTUs()) {
			sourceOTUPosition++;
			final R sourceRow = sourceMatrix.getRow(sourceOTU);

			final OTU dbOTU = dbMatrix
					.getOTUSet()
					.getOTU(sourceOTUPosition);

			// Let's create rows for OTU->null row mappings in the matrix.
			R dbRow = null;

			if (null == (dbRow = dbMatrix.getRow(dbOTU))) {
				dbRow = rowProvider.get();
				dbRow
						.setVersionInfo(
								newVersionInfo.getNewVersionInfo());
				dbMatrix.putRow(dbOTU, dbRow);
				dao.makePersistent(dbRow);
			}

			// if (!newRow && targetRow.getPPodVersion() == null) {
			// throw new AssertionError(
			// "existing row has no pPOD version number");
			// }

			final List<C> dbCells = newArrayList(dbRow.getCells());

			// Add in cells to dbCells if needed.
			while (dbCells.size() < sourceRow.getCells().size()) {
				dbCells.add(cellProvider.get());
			}

			// Get rid of cells from dbCells if needed
			while (dbCells.size() > sourceRow.getCells().size()) {
				dbCells.remove(dbCells.size() - 1);
			}

			final List<C> clearedDbCells = dbRow.setCells(dbCells);

			for (final C clearedDbCell : clearedDbCells) {
				dao.makeTransient(clearedDbCell);
			}

			int dbCellPosition = -1;
			for (final C dbCell : dbRow.getCells()) {
				dbCellPosition++;

				final C sourceCell = sourceRow
								.getCells()
								.get(dbCellPosition);

				switch (sourceCell.getType()) {
					case INAPPLICABLE:
						dbCell.setInapplicable();
						break;
					case POLYMORPHIC:
						dbCell.setPolymorphicElements(sourceCell.getElements());
						break;
					case SINGLE:
						dbCell.setSingleElement(
								getOnlyElement(sourceCell.getElements()));
						break;
					case UNASSIGNED:
						dbCell.setUnassigned();
						break;
					case UNCERTAIN:
						dbCell.setUncertainElements(sourceCell.getElements());
						break;
					default:
						throw new AssertionError("unknown type");
				}

				// We need to do this here since we're removing the cell from
				// the persistence context (with evict). So it won't get handled
				// higher up in the application when it does for most entities.
				if (dbCell.isInNeedOfNewVersion()) {
					dbCell.setVersionInfo(
							newVersionInfo.getNewVersionInfo());
				}
				dao.makePersistent(dbCell);

				cellsToEvict.add(dbCell);
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
					sourceOTUPosition);

			dao.flush();

			dao.evictEntities(cellsToEvict);

			cellsToEvict.clear();

			fillInCellInfo(matrixInfo, dbRow, sourceOTUPosition);

			// This is to free up the cells for garbage collection - but depends
			// on dao.evict(targetRow) to be safe!!!!!
			dbRow.clearCells();

			// Again to free up cells for garbage collection
			sourceRow.clearCells();
		}
		return matrixInfo;
	}

	private void fillInCellInfo(final MatrixInfo matrixInfo, final R row,
			final int rowPosition) {
		int cellPosition = -1;
		for (final C cell : row.getCells()) {
			cellPosition++;
			matrixInfo.setCellPPodIdAndVersion(
					rowPosition,
					cellPosition,
					cell.getVersionInfo()
							.getVersion());
		}
	}
}
