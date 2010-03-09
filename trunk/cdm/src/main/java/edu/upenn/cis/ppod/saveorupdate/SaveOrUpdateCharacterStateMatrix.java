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
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findEach;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
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
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
public class SaveOrUpdateCharacterStateMatrix implements ISaveOrUpdateMatrix {

	private final Provider<Character> characterProvider;
	private final Provider<CharacterStateRow> rowProvider;
	private final Provider<CharacterStateCell> cellProvider;
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
			@Assisted final INewPPodVersionInfo newPPodVersionInfo,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final IMergeAttachments mergeAttachments) {
		this.characterProvider = characterProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.dao = dao;
		this.mergeAttachments = mergeAttachments;
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public void saveOrUpdate(CharacterStateMatrix targetMatrix,
			final CharacterStateMatrix sourceMatrix,
			final Map<OTU, OTU> mergedOTUsBySourceOTU,
			final DNACharacter dnaCharacter) {
		final String METHOD = "saveOrUpdate(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(targetMatrix);
		checkNotNull(sourceMatrix);

		targetMatrix.setLabel(sourceMatrix.getLabel());
		targetMatrix.setDescription(sourceMatrix.getDescription());

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		if (targetMatrix.getDocId() == null) {
			targetMatrix.setDocId(sourceMatrix.getDocId());
		}

		final Map<Integer, Integer> newCharIdxsToOriginalCharIdxs = newHashMap();
		final List<Character> newTargetMatrixCharacters = newArrayList();
		int sourceCharacterPosition = -1;
		for (final Character sourceCharacter : sourceMatrix.getCharacters()) {
			sourceCharacterPosition++;
			Character newTargetCharacter;
			if (sourceMatrix instanceof DNAStateMatrix) {
				newTargetCharacter = dnaCharacter;
			} else if (null == (newTargetCharacter = findIf(targetMatrix
					.getCharacters(), compose(equalTo(sourceCharacter
					.getPPodId()), IUUPPodEntity.getPPodId)))) {
				newTargetCharacter = characterProvider.get();
				newTargetCharacter.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				newTargetCharacter.setPPodId();
			}

			newTargetMatrixCharacters.add(newTargetCharacter);

			if (!(sourceMatrix instanceof DNAStateMatrix)) {
				newTargetCharacter.setLabel(sourceCharacter.getLabel());
			}

			for (final CharacterState sourceState : sourceCharacter.getStates()
					.values()) {
				CharacterState targetState;
				if (null == (targetState = newTargetCharacter.getStates().get(
						sourceState.getStateNumber()))) {
					targetState = newTargetCharacter.addState(stateFactory
							.create(sourceState.getStateNumber()));
					targetState.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
				}

				if (!(sourceMatrix instanceof DNAStateMatrix)) {
					targetState.setLabel(sourceState.getLabel());
				}
			}

			if (!(sourceMatrix instanceof DNAStateMatrix)) {
				newCharIdxsToOriginalCharIdxs.put(sourceCharacterPosition,
						targetMatrix.getCharacterIdx().get(newTargetCharacter));
			} else {
				if (targetMatrix.getCharacters().size() <= sourceCharacterPosition) {
					newCharIdxsToOriginalCharIdxs.put(sourceCharacterPosition,
							null);
				} else {
					newCharIdxsToOriginalCharIdxs.put(sourceCharacterPosition,
							sourceCharacterPosition);
				}
			}
			for (final Attachment sourceAttachment : sourceCharacter
					.getAttachments()) {
				final Set<Attachment> targetAttachments = findEach(
						newTargetCharacter.getAttachments(), compose(
								equalTo(sourceAttachment.getStringValue()),
								Attachment.getStringValue));

				Attachment targetAttachment = getOnlyElement(targetAttachments,
						null);
				if (targetAttachment == null) {
					targetAttachment = attachmentProvider.get();
					targetAttachment.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
					targetAttachment.setPPodId();
				}
				newTargetCharacter.addAttachment(targetAttachment);
				mergeAttachments.merge(targetAttachment, sourceAttachment);
				dao.saveOrUpdate(targetAttachment);
			}
			dao.saveOrUpdate(newTargetCharacter);

		}
		final List<Character> removedCharacters = targetMatrix
				.setCharacters(newTargetMatrixCharacters);

		// So the rows have a targetMatrix id
		dao.saveOrUpdate(targetMatrix);

		final Set<CharacterStateCell> cellsToEvict = newHashSet();
		for (final OTU sourceOTU : sourceMatrix.getOTUSet().getOTUs()) {
			final CharacterStateRow sourceRow = sourceMatrix.getRow(sourceOTU);

			final int sourceOTUPosition = sourceMatrix.getOTUSet().getOTUs()
					.indexOf(sourceOTU);

			final OTU targetOTU = targetMatrix.getOTUSet().getOTUs().get(
					sourceOTUPosition);
			CharacterStateRow targetRow = null;
			List<Character> characters = targetMatrix.getCharacters();

			boolean newRow = false;

			if (null == (targetRow = targetMatrix.getRow(targetOTU))) {
				targetRow = rowProvider.get();
				targetRow.setPPodVersionInfo(newPPodVersionInfo
						.getNewPPodVersionInfo());
				targetMatrix.putRow(targetOTU, targetRow);
				dao.saveOrUpdate(targetRow);
				newRow = true;
			} else {
				newRow = false;
			}

// if (!newRow && targetRow.getPPodVersion() == null) {
// throw new AssertionError(
// "existing row has no pPOD version number");
// }

			final ImmutableList<CharacterStateCell> originalTargetCells = ImmutableList
					.copyOf(targetRow.getCells());
			final List<CharacterStateCell> newTargetCells = newArrayListWithCapacity(sourceRow
					.getCells().size());

			// First we fill with empty cells
			for (int newCellIdx = 0; newCellIdx < targetMatrix.getCharacters()
					.size(); newCellIdx++) {
				CharacterStateCell targetCell;
				if (newRow
						|| null == newCharIdxsToOriginalCharIdxs
								.get(newCellIdx)) {
					targetCell = cellProvider.get();
					targetCell.setPPodVersionInfo(newPPodVersionInfo
							.getNewPPodVersionInfo());
				} else {
					targetCell = originalTargetCells
							.get(newCharIdxsToOriginalCharIdxs.get(newCellIdx));
				}
				newTargetCells.add(targetCell);
			}

			final List<CharacterStateCell> clearedCells = targetRow
					.setCells(newTargetCells);

			for (final CharacterStateCell clearedCell : clearedCells) {
				dao.delete(clearedCell);
			}

			for (final CharacterStateCell targetCell : targetRow.getCells()) {

				final Integer targetCellPosition = targetRow.getCellIdx().get(
						targetCell);

				final CharacterStateCell sourceCell = sourceRow.getCells().get(
						targetCellPosition);

				final Set<CharacterState> newTargetStates = newHashSet();
				for (final CharacterState sourceState : sourceCell.getStates()) {
					newTargetStates.add(characters.get(targetCellPosition)
							.getStates().get(sourceState.getStateNumber()));
				}
				switch (sourceCell.getType()) {
					case INAPPLICABLE:
						targetCell.setInapplicable();
						break;
					case POLYMORPHIC:
						targetCell.setPolymorphicStates(newTargetStates);
						break;
					case SINGLE:
						targetCell.setSingleState(get(newTargetStates, 0));
						break;
					case UNASSIGNED:
						targetCell.setUnassigned();
						break;
					case UNCERTAIN:
						targetCell.setUncertainStates(newTargetStates);
						break;
					default:
						throw new AssertionError("unknown type");
				}
				dao.saveOrUpdate(targetCell);
				cellsToEvict.add(targetCell);
			}

			logger.debug("{}: flushing row number {}", METHOD,
					sourceOTUPosition);

			dao.flush();

			dao.evictEntities(cellsToEvict);
			cellsToEvict.clear();
			dao.evict(targetRow);
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
	}
}
