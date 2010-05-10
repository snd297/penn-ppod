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
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.DNAStateMatrix;
import edu.upenn.cis.ppod.model.MolecularStateMatrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;
import edu.upenn.cis.ppod.services.ppodentity.CharacterStateMatrixInfo;
import edu.upenn.cis.ppod.services.ppodentity.PPodEntityInfo;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
final class SaveOrUpdateCharacterStateMatrix implements ISaveOrUpdateMatrix {

	private final Provider<Character> characterProvider;
	private final Provider<CharacterStateRow> rowProvider;
	private final Provider<CharacterStateCell> cellProvider;
	private final Provider<CharacterStateMatrixInfo> matrixInfoProvider;
	// private final Provider<PPodEntityInfo> pPodEntityInfoProvider;
	private final CharacterState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachments mergeAttachments;
	private final IDAO<Object, Long> dao;

	@InjectLogger
	private Logger logger;

	private final INewPPodVersionInfo newPPodVersionInfo;

	@Inject
	SaveOrUpdateCharacterStateMatrix(
			final Provider<Character> characterProvider,
			final Provider<CharacterStateRow> rowProvider,
			final Provider<CharacterStateCell> cellProvider,
			final CharacterState.IFactory stateFactory,
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
			final CharacterStateMatrix dbMatrix,
			final CharacterStateMatrix sourceMatrix,
			final DNACharacter dnaCharacter) {
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
				.charactersIterator(); sourceCharactersItr.hasNext();) {
			final Character sourceCharacter = sourceCharactersItr.next();
			sourceCharacterPosition++;
			Character newDbCharacter;
			if (sourceMatrix instanceof DNAStateMatrix) {
				newDbCharacter = dnaCharacter;
			} else if (null == (newDbCharacter =
					findIf(dbMatrix
							.charactersIterator(),
							compose(equalTo(sourceCharacter
									.getPPodId()),
									IWithPPodId.getPPodId)))) {
				newDbCharacter = characterProvider.get();
				newDbCharacter.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				newDbCharacter.setPPodId();
			}

			newDbMatrixCharacters.add(newDbCharacter);

			if (!(sourceMatrix instanceof DNAStateMatrix)) {
				newDbCharacter.setLabel(sourceCharacter.getLabel());
			}

			for (final Iterator<CharacterState> sourceStatesItr = sourceCharacter
					.getStatesIterator(); sourceStatesItr.hasNext();) {
				final CharacterState sourceState = sourceStatesItr.next();
				CharacterState dbState;
				if (null == (dbState = newDbCharacter.getState(
						sourceState.getStateNumber()))) {
					dbState = stateFactory
							.create(sourceState.getStateNumber());
					newDbCharacter.putState(dbState);
					dbState.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
				}

				if (!(sourceMatrix instanceof DNAStateMatrix)) {
					dbState.setLabel(sourceState.getLabel());
				}
			}

			if (!(sourceMatrix instanceof MolecularStateMatrix)) {
				newCharPositionsToOriginalCharPositions.put(
						sourceCharacterPosition,
						dbMatrix.getCharacterPosition(newDbCharacter));
			} else {
				if (dbMatrix.getColumnsSize() <= sourceCharacterPosition) {
					newCharPositionsToOriginalCharPositions.put(
							sourceCharacterPosition,
							null);
				} else {
					newCharPositionsToOriginalCharPositions.put(
							sourceCharacterPosition,
							sourceCharacterPosition);
				}
			}
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
		final List<Character> removedCharacters = dbMatrix
				.setCharacters(newDbMatrixCharacters);

		// So the rows have a dbMatrix id
		dao.saveOrUpdate(dbMatrix);

		final Set<CharacterStateCell> cellsToEvict = newHashSet();
		int sourceOTUPosition = -1;

		for (final OTU sourceOTU : sourceMatrix.getOTUSet()) {
			sourceOTUPosition++;
			final CharacterStateRow sourceRow = sourceMatrix.getRow(sourceOTU);

			final OTU dbOTU = dbMatrix.getOTUSet().getOTU(
					sourceOTUPosition);
			CharacterStateRow dbRow = null;
			final List<Character> characters = newArrayList(dbMatrix
					.charactersIterator());

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

			final ImmutableList<CharacterStateCell> originalDbCells = ImmutableList
					.copyOf(dbRow.iterator());
			final List<CharacterStateCell> newDbCells = newArrayListWithCapacity(sourceRow
					.getCellsSize());

			// First we fill with empty cells
			for (int newCellPosition = 0; newCellPosition < dbMatrix
					.getColumnsSize(); newCellPosition++) {
				CharacterStateCell dbCell;
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

			final List<CharacterStateCell> clearedCells = dbRow
					.setCells(newDbCells);

			for (final CharacterStateCell clearedCell : clearedCells) {
				dao.delete(clearedCell);
			}

			int targetCellPosition = -1;
			for (final CharacterStateCell dbCell : dbRow) {
				targetCellPosition++;

				final CharacterStateCell sourceCell = sourceRow.getCell(
						targetCellPosition);

				final Set<CharacterState> newTargetStates = newHashSet();
				for (final CharacterState sourceState : sourceCell) {
					newTargetStates.add(characters.get(targetCellPosition)
							.getState(sourceState.getStateNumber()));
				}
				switch (sourceCell.getType()) {
					case INAPPLICABLE:
						dbCell.setInapplicable();
						break;
					case POLYMORPHIC:
						dbCell.setPolymorphicElements(newTargetStates);
						break;
					case SINGLE:
						dbCell
								.setSingleElement(getOnlyElement(newTargetStates));
						break;
					case UNASSIGNED:
						dbCell.setUnassigned();
						break;
					case UNCERTAIN:
						dbCell.setUncertainElements(newTargetStates);
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

		// Do this down here because it's after any cells that reference the
		// characters are deleted.
		for (final Character character : removedCharacters) {
			// We only want to delete STANDARD characters because other kinds
			// are shared by matrices. This is less than ideal.
			if (dao.getEntityName(character).equals(
					dao.getEntityName(Character.class))) {
				if (character.getMatrices().size() != 0) {
					logger.warn("standard character " + character.toString()
								+ " belonged to multiple matrices: "
								+ character.getMatrices());
				} else {
					logger.debug("deleting character: " + character.getLabel());
					dao.delete(character);
				}
			}
		}

		matrixInfo.setPPodId(dbMatrix.getPPodId());
		return matrixInfo;
	}

	private void fillInCellInfo(final CharacterStateMatrixInfo matrixInfo,
			final CharacterStateRow row, final int rowPosition) {
		int cellPosition = -1;
		for (final CharacterStateCell cell : row) {
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
// for (final Iterator<Character> charactersItr = matrix
// .getCharactersIterator(); charactersItr.hasNext();) {
// characterIdx++;
// final Character character = charactersItr.next();
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
