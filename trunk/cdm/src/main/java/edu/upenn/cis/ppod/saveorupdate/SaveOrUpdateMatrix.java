package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.Cell;
import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Row;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
final class SaveOrUpdateMatrix<R extends Row<C>, C extends Cell<E>, E> {

	private final Provider<R> rowProvider;
	private final Provider<C> cellProvider;
	private final Provider<MatrixInfo> matrixInfoProvider;
	private final IDAO<Object, Long> dao;

	@InjectLogger
	private Logger logger;

	private final INewPPodVersionInfo newPPodVersionInfo;
	private final Set<E> newTargetElements;

	@Inject
	SaveOrUpdateMatrix(
				final Provider<R> rowProvider,
				final Provider<C> cellProvider,
				final Provider<Attachment> attachmentProvider,
				final Provider<MatrixInfo> matrixInfoProvider,
				final Provider<E> cellElementProvider,
				@Assisted final INewPPodVersionInfo newPPodVersionInfo,
				@Assisted final IDAO<Object, Long> dao,
				@Assisted final IMergeAttachments mergeAttachments,
				@Assisted final Set<E> newTargetElements) {
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.matrixInfoProvider = matrixInfoProvider;
		this.dao = dao;
		this.newPPodVersionInfo = newPPodVersionInfo;
		this.newTargetElements = newTargetElements;
	}

	public void saveOrUpdate(final Matrix<R> dbMatrix,
			final Matrix<R> sourceMatrix) {

		final String METHOD = "saveOrUpdate(...)";

		// So the rows have a dbMatrix id
		dao.saveOrUpdate(dbMatrix);

		final Set<C> cellsToEvict = newHashSet();

		int sourceOTUPosition = -1;

		for (final OTU sourceOTU : sourceMatrix.getOTUSet()) {
			sourceOTUPosition++;
			final R sourceRow = sourceMatrix.getRow(sourceOTU);

			final OTU dbOTU =
					dbMatrix.getOTUSet().getOTU(sourceOTUPosition);

			// Let's create rows for OTU->null row mappings in the matrix.
			R dbRow = null;

			if (null == (dbRow = dbMatrix.getRow(dbOTU))) {
				dbRow = rowProvider.get();
				dbRow.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				dbMatrix.putRow(dbOTU, dbRow);
				dao.saveOrUpdate(dbRow);
			}

			// if (!newRow && targetRow.getPPodVersion() == null) {
			// throw new AssertionError(
			// "existing row has no pPOD version number");
			// }

			final List<C> dbCells = newArrayList(dbRow);

			// Add in cells to dbCells if needed.
			while (dbCells.size() < sourceRow.getCellsSize()) {
				dbCells.add(cellProvider.get());
			}

			// Get rid of cells from dbCells if needed
			while (dbCells.size() > sourceRow.getCellsSize()) {
				dbCells.remove(dbCells.size() - 1);
			}

			final List<C> clearedDbCells = dbRow.setCells(dbCells);

			for (final C clearedDbCell : clearedDbCells) {
				dao.delete(clearedDbCell);
			}

			int targetCellPosition = -1;
			for (final C dbCell : dbRow) {
				targetCellPosition++;

				newTargetElements.clear();

				final C sourceCell = sourceRow.getCell(
						targetCellPosition);

				for (final E sourceCellElement : sourceCell) {
					newTargetElements.add(sourceCellElement);
				}
				switch (sourceCell.getType()) {
					case INAPPLICABLE:
						dbCell.setInapplicable();
						break;
					case POLYMORPHIC:
						dbCell.setPolymorphicElements(newTargetElements);
						break;
					case SINGLE:
						dbCell
								.setSingleElement(getOnlyElement(newTargetElements));
						break;
					case UNASSIGNED:
						dbCell.setUnassigned();
						break;
					case UNCERTAIN:
						dbCell.setUncertainElements(newTargetElements);
						break;
					default:
						throw new AssertionError("unknown type");
				}

				// We need to do this here since we're removing the cell from
				// the persistence context (with evict). So it won't get handled
				// higher up in the application when it does for most entities.
				if (dbCell.isInNeedOfNewPPodVersionInfo()) {
					dbCell.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
				}
				dao.saveOrUpdate(dbCell);

				cellsToEvict.add(dbCell);
			}

			// We need to do this here since we're removing the cell from
			// the persistence context (with evict)
			if (dbRow.isInNeedOfNewPPodVersionInfo()) {
				dbRow.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
			}

			logger.debug("{}: flushing row number {}", METHOD,
					sourceOTUPosition);

			dao.flush();

			dao.evictEntities(cellsToEvict);
			cellsToEvict.clear();

			dao.evict(dbRow);

			final MatrixInfo matrixInfo = matrixInfoProvider.get();

			fillInCellInfo(matrixInfo, dbRow, sourceOTUPosition);

			// This is to free up the cells for garbage collection - but depends
			// on dao.evict(targetRow) to be safe!!!!!
			dbRow.clearCells();

			// Again to free up cells for garbage collection
			sourceRow.clearCells();
		}
	}

	private void fillInCellInfo(final MatrixInfo matrixInfo,
			final R row, final int rowPosition) {
		int cellPosition = -1;
		for (final C cell : row) {
			cellPosition++;
			matrixInfo.setCellPPodIdAndVersion(rowPosition,
					cellPosition,
					cell.getPPodVersionInfo().getPPodVersion());
		}
	}
}
