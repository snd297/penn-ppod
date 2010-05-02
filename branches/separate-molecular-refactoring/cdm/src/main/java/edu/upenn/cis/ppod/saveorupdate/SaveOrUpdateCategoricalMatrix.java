/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.CategoricalMatrix;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CategoricalState;
import edu.upenn.cis.ppod.model.CategoricalCell;
import edu.upenn.cis.ppod.model.CategoricalRow;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;
import edu.upenn.cis.ppod.services.ppodentity.CharacterStateMatrixInfo;
import edu.upenn.cis.ppod.services.ppodentity.PPodEntityInfo;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
final class SaveOrUpdateCategoricalMatrix implements
		ISaveOrUpdateCategoricalMatrix {

	private final Provider<Character> characterProvider;
	private final Provider<CategoricalRow> rowProvider;
	private final Provider<CategoricalCell> cellProvider;
	private final Provider<CharacterStateMatrixInfo> matrixInfoProvider;
	// private final Provider<PPodEntityInfo> pPodEntityInfoProvider;
	private final CategoricalState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachments mergeAttachments;
	private final IDAO<Object, Long> dao;

	@InjectLogger
	private Logger logger;

	private final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	SaveOrUpdateCategoricalMatrix(
			final Provider<Character> characterProvider,
			final Provider<CategoricalRow> rowProvider,
			final Provider<CategoricalCell> cellProvider,
			final CategoricalState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			final Provider<CharacterStateMatrixInfo> matrixInfoProvider,
			final Provider<PPodEntityInfo> pPodEntityInfoProvider,
			@Assisted final INewPPodVersionInfo newPPodVersionInfo,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final IMergeAttachments mergeAttachments) {
		this.characterProvider = characterProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.matrixInfoProvider = matrixInfoProvider;
		// this.pPodEntityInfoProvider = pPodEntityInfoProvider;
		this.dao = dao;
		this.mergeAttachments = mergeAttachments;
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public CharacterStateMatrixInfo saveOrUpdate(
			final CategoricalMatrix dbMatrix,
			final CategoricalMatrix sourceMatrix) {
		final String METHOD = "saveOrUpdate(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

		final CharacterStateMatrixInfo matrixInfo = matrixInfoProvider.get();

		dbMatrix.setLabel(sourceMatrix.getLabel());
		dbMatrix.setDescription(sourceMatrix.getDescription());

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		if (dbMatrix.getDocId() == null) {
			dbMatrix.setDocId(sourceMatrix.getDocId());
		}

		final Map<Integer, Integer> newCharPositionsToOriginalCharPositions = newHashMap();
		final List<Character> newDbMatrixCharacters = newArrayList();
		int sourceCharacterPosition = -1;
		for (final Iterator<Character> sourceCharactersItr = sourceMatrix
				.getCharactersIterator(); sourceCharactersItr.hasNext();) {
			final Character sourceCharacter = sourceCharactersItr
					.next();
			sourceCharacterPosition++;
			Character newDbCharacter;
			if (null == (newDbCharacter =
					findIf(dbMatrix
							.getCharactersIterator(),
							compose(equalTo(sourceCharacter
									.getPPodId()),
									IWithPPodId.getPPodId)))) {
				newDbCharacter = characterProvider.get();
				newDbCharacter.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				newDbCharacter.setPPodId();
			}

			newDbMatrixCharacters.add(newDbCharacter);

			newDbCharacter.setLabel(sourceCharacter.getLabel());

			for (final Iterator<CategoricalState> sourceStatesItr = sourceCharacter
					.getStatesIterator(); sourceStatesItr.hasNext();) {
				final CategoricalState sourceState = sourceStatesItr.next();
				CategoricalState dbState;
				if (null == (dbState = newDbCharacter.getState(
						sourceState.getStateNumber()))) {
					dbState = stateFactory
							.create(sourceState.getStateNumber());
					newDbCharacter.addState(dbState);
					dbState.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
				}

				dbState.setLabel(sourceState.getLabel());

			}

			newCharPositionsToOriginalCharPositions.put(
						sourceCharacterPosition,
						dbMatrix.getCharacterPosition(newDbCharacter));

			for (final Iterator<Attachment> sourceAttachmentsItr = sourceCharacter
					.getAttachmentsIterator(); sourceAttachmentsItr.hasNext();) {
				final Attachment sourceAttachment = sourceAttachmentsItr.next();
				final ImmutableSet<Attachment> newDbCharacterAttachments = ImmutableSet
						.copyOf(newDbCharacter.getAttachmentsIterator());
				final Set<Attachment> targetAttachments = filter(
						newDbCharacterAttachments, compose(
								equalTo(sourceAttachment.getStringValue()),
								Attachment.getStringValue));

				Attachment dbAttachment = getOnlyElement(targetAttachments,
						null);
				if (dbAttachment == null) {
					dbAttachment = attachmentProvider.get();
					dbAttachment.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
					dbAttachment.setPPodId();
				}
				newDbCharacter.addAttachment(dbAttachment);
				mergeAttachments.merge(dbAttachment, sourceAttachment);
				dao.saveOrUpdate(dbAttachment);
			}
			dao.saveOrUpdate(newDbCharacter);

		}

		// So the rows have a dbMatrix id
		dao.saveOrUpdate(dbMatrix);

		final Set<CategoricalCell> cellsToEvict = newHashSet();
		int sourceOTUPosition = -1;

		for (final OTU sourceOTU : sourceMatrix.getOTUSet()) {
			sourceOTUPosition++;
			final CategoricalRow sourceRow = sourceMatrix.getRow(sourceOTU);

			final OTU dbOTU = dbMatrix.getOTUSet().getOTU(
					sourceOTUPosition);
			CategoricalRow dbRow = null;
			final List<Character> abstractCharacters = newArrayList(dbMatrix
					.getCharactersIterator());

			boolean newRow = false;

			if (null == (dbRow = dbMatrix.getRow(dbOTU))) {
				dbRow = rowProvider.get();
				dbRow.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				dbMatrix.putRow(dbOTU, dbRow);
				dao.saveOrUpdate(dbRow);
				newRow = true;
			} else {
				newRow = false;
			}

// if (!newRow && targetRow.getPPodVersion() == null) {
// throw new AssertionError(
// "existing row has no pPOD version number");
// }

			final ImmutableList<CategoricalCell> originalDbCells = ImmutableList
					.copyOf(dbRow.iterator());
			final List<CategoricalCell> newDbCells = newArrayListWithCapacity(sourceRow
					.getCellsSize());

			// First we fill with empty cells
			for (int newCellPosition = 0; newCellPosition < dbMatrix
					.getColumnsSize(); newCellPosition++) {
				CategoricalCell dbCell;
				if (newRow
						|| null == newCharPositionsToOriginalCharPositions
								.get(newCellPosition)) {
					dbCell = cellProvider.get();
					dbCell.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
				} else {
					dbCell = originalDbCells
							.get(newCharPositionsToOriginalCharPositions
									.get(newCellPosition));
				}
				newDbCells.add(dbCell);
			}

			final List<CategoricalCell> clearedCells = dbRow
					.setCells(newDbCells);

			for (final CategoricalCell clearedCell : clearedCells) {
				dao.delete(clearedCell);
			}

			int targetCellPosition = -1;
			for (final CategoricalCell dbCell : dbRow) {
				targetCellPosition++;

				final CategoricalCell sourceCell = sourceRow.getCell(
						targetCellPosition);

				final Set<CategoricalState> newTargetStates = newHashSet();
				for (final CategoricalState sourceState : sourceCell) {
					newTargetStates.add(abstractCharacters.get(
							targetCellPosition)
							.getState(sourceState.getStateNumber()));
				}
				switch (sourceCell.getType()) {
					case INAPPLICABLE:
						dbCell.setInapplicable();
						break;
					case POLYMORPHIC:
						dbCell.setPolymorphicStates(newTargetStates);
						break;
					case SINGLE:
						dbCell
								.setSingleState(getOnlyElement(newTargetStates));
						break;
					case UNASSIGNED:
						dbCell.setUnassigned();
						break;
					case UNCERTAIN:
						dbCell.setUncertainStates(newTargetStates);
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

			fillInCellInfo(matrixInfo, dbRow, sourceOTUPosition);

			// This is to free up the cells for garbage collection - but depends
			// on dao.evict(targetRow) to be safe!!!!!
			dbRow.clearCells();

			// Again to free up cells for garbage collection
			sourceRow.clearCells();
		}

		matrixInfo.setPPodId(dbMatrix.getPPodId());
		return matrixInfo;
	}

	private void fillInCellInfo(final CharacterStateMatrixInfo matrixInfo,
			final CategoricalRow row, final int rowPosition) {
		int cellPosition = -1;
		for (final CategoricalCell cell : row) {
			cellPosition++;
			matrixInfo.setCellPPodIdAndVersion(rowPosition,
					cellPosition,
					cell.getPPodVersionInfo().getPPodVersion());
		}
	}

// private void fillInMatrixInfo(
// final CharacterStateMatrixInfo matrixInfo,
// final CharacterStateMatrix matrix) {
// matrixInfo.setEntityId(matrix.getId());
// matrixInfo.setPPodId(matrix.getPPodId());
// matrixInfo.setPPodVersion(matrix.getPPodVersionInfo()
// .getPPodVersion());
// matrixInfo.setDocId(matrix.getDocId());
//
// int characterIdx = -1;
// for (final Iterator<AbstractCharacter> charactersItr = matrix
// .getCharactersIterator(); charactersItr.hasNext();) {
// characterIdx++;
// final AbstractCharacter character = charactersItr.next();
// PPodEntityInfo characterInfo = pPodEntityInfoProvider.get();
// characterInfo.setPPodId(character.getPPodId());
// characterInfo.setEntityId(character.getId());
// characterInfo.setPPodVersion(character.getPPodVersionInfo()
// .getPPodVersion());
// matrixInfo.getCharacterInfosByIdx().put(characterIdx,
// characterInfo);
// }
//
// int columnIdx = -1;
// for (final Iterator<PPodVersionInfo> columnPPodVersionInfosItr = matrix
// .getColumnPPodVersionInfosIterator(); columnPPodVersionInfosItr
// .hasNext();) {
// columnIdx++;
// final PPodVersionInfo columnPPodVersionInfo = columnPPodVersionInfosItr
// .next();
// matrixInfo.getColumnHeaderVersionsByIdx().put(columnIdx,
// columnPPodVersionInfo.getPPodVersion());
// }
//
// int rowIdx = -1;
// for (final CharacterStateRow row : matrix) {
// rowIdx++;
// matrixInfo.getRowHeaderVersionsByIdx().put(rowIdx,
// row.getPPodVersionInfo().getPPodVersion());
// }
// }
}
