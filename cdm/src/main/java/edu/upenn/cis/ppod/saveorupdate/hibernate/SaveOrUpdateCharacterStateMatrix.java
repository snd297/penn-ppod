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
package edu.upenn.cis.ppod.saveorupdate.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;
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
import edu.upenn.cis.ppod.model.ICharacterStateMatrix;
import edu.upenn.cis.ppod.model.IUUPPodEntity;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.SetPPodVersionInfoVisitor;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachment;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateCharacterStateMatrix;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;
import edu.upenn.cis.ppod.util.PPodPredicates;

/**
 * @author Sam Donnelly
 */
public class SaveOrUpdateCharacterStateMatrix implements
		ISaveOrUpdateCharacterStateMatrix {

	private final Provider<Character> characterProvider;
	private final Provider<CharacterStateRow> rowProvider;
	private final Provider<CharacterStateCell> cellProvider;
	private final CharacterState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachment mergeAttachment;
	private final IDAO<Object, Long> dao;

	@InjectLogger
	private Logger logger;
	final private SetPPodVersionInfoVisitor setPPodVersionInfoVisitor;

	@Inject
	SaveOrUpdateCharacterStateMatrix(
			final Provider<Character> characterProvider,
			final Provider<CharacterStateRow> rowProvider,
			final Provider<CharacterStateCell> cellProvider,
			final CharacterState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final IMergeAttachment mergeAttachment,
			@Assisted final SetPPodVersionInfoVisitor setPPodVersionInfoVisitor) {
		this.characterProvider = characterProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.dao = dao;
		this.mergeAttachment = mergeAttachment;
		this.setPPodVersionInfoVisitor = setPPodVersionInfoVisitor;
	}

	public void saveOrUpdate(CharacterStateMatrix targetMatrix,
			final CharacterStateMatrix sourceMatrix,
			final OTUSet newTargetMatrixOTUSet,
			final Map<OTU, OTU> mergedOTUsBySourceOTU,
			final DNACharacter dnaCharacter) {
		final String METHOD = "saveOrUpdate(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(targetMatrix);
		checkNotNull(sourceMatrix);
		checkNotNull(newTargetMatrixOTUSet);

		newTargetMatrixOTUSet.addMatrix(targetMatrix);

		targetMatrix.setLabel(sourceMatrix.getLabel());
		targetMatrix.setDescription(sourceMatrix.getDescription());

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		if (targetMatrix.getDocId() == null) {
			targetMatrix.setDocId(sourceMatrix.getDocId());
		}

		final List<OTU> newTargetOTUs = newArrayList();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			final OTU newTargetOTU = mergedOTUsBySourceOTU.get(sourceOTU);
			if (newTargetOTU == null) {
				throw new AssertionError(
						"couldn't find incomingOTU in persistentOTUsByIncomingOTU");
			}
			newTargetOTUs.add(newTargetOTU);
		}

		targetMatrix.setOTUs(newTargetOTUs);

		final Map<Integer, Integer> originalCharIdxsByNewCharIdx = newHashMap();
		final List<Character> newTargetMatrixCharacters = newArrayList();
		int sourceCharacterPosition = -1;
		for (final Character sourceCharacter : sourceMatrix.getCharacters()) {
			sourceCharacterPosition++;
			Character newTargetCharacter;
			if (sourceMatrix.getType() == ICharacterStateMatrix.Type.DNA) {
				newTargetCharacter = dnaCharacter;
			} else if (null == (newTargetCharacter = findIf(targetMatrix
					.getCharacters(), PPodPredicates.equalTo(sourceCharacter
					.getPPodId(), IUUPPodEntity.getPPodId)))) {
				newTargetCharacter = characterProvider.get();
				newTargetCharacter.setPPodId();
			}
			newTargetMatrixCharacters.add(newTargetCharacter);

			if (sourceMatrix.getType() == ICharacterStateMatrix.Type.STANDARD) {
				newTargetCharacter.setLabel(sourceCharacter.getLabel());
			}

			for (final CharacterState sourceState : sourceCharacter.getStates()
					.values()) {
				CharacterState targetState;
				if (null == (targetState = newTargetCharacter.getStates().get(
						sourceState.getStateNumber()))) {
					targetState = newTargetCharacter.addState(stateFactory
							.create(sourceState.getStateNumber()));
				}
				if (sourceMatrix.getType() == ICharacterStateMatrix.Type.STANDARD) {
					targetState.setLabel(sourceState.getLabel());
				}
			}

			if (sourceMatrix.getType() == ICharacterStateMatrix.Type.STANDARD) {
				originalCharIdxsByNewCharIdx.put(sourceCharacterPosition,
						targetMatrix.getCharacterIdx().get(newTargetCharacter));
			} else {
				if (targetMatrix.getCharacters().size() <= sourceCharacterPosition) {
					originalCharIdxsByNewCharIdx.put(sourceCharacterPosition,
							null);
				} else {
					originalCharIdxsByNewCharIdx.put(sourceCharacterPosition,
							sourceCharacterPosition);
				}
			}
			for (final Attachment sourceAttachment : sourceCharacter
					.getAttachments()) {
				final Set<Attachment> targetAttachments = findEach(
						newTargetCharacter.getAttachments(), PPodPredicates
								.equalTo(sourceAttachment.getStringValue(),
										Attachment.getStringValue));

				Attachment targetAttachment = getOnlyElement(targetAttachments,
						null);
				if (targetAttachment == null) {
					targetAttachment = attachmentProvider.get();
					targetAttachment.setPPodId();
				}
				newTargetCharacter.addAttachment(targetAttachment);
				mergeAttachment.merge(targetAttachment, sourceAttachment);
				dao.saveOrUpdate(targetAttachment);
			}
			dao.saveOrUpdate(newTargetCharacter);
		}
		targetMatrix.setCharacters(newTargetMatrixCharacters);

		final Set<CharacterStateCell> cellsToEvict = newHashSet();
		for (final CharacterStateRow sourceRow : sourceMatrix.getRows()) {

			final int sourceRowIdx = sourceRow.getPosition();

			final OTU targetOTU = targetMatrix.getOTUs().get(sourceRowIdx);
			CharacterStateRow targetRow = null;
			List<Character> characters = targetMatrix.getCharacters();

			boolean newRow = false;

			if (null == (targetRow = targetMatrix.getRow(targetOTU))) {
				targetRow = rowProvider.get();
				targetMatrix.setRow(targetOTU, targetRow);
				dao.saveOrUpdate(targetRow);
				newRow = true;
			} else {
				newRow = false;
			}

// if (!newRow && targetRow.getPPodVersion() == null) {
// throw new AssertionError(
// "existing row has no pPOD version number");
// }

			final List<CharacterStateCell> originalTargetCells = newArrayList(targetRow
					.getCells());
			final List<CharacterStateCell> newTargetCells = newArrayListWithCapacity(sourceRow
					.getCells().size());

			// First we fill with empty cells
			for (int newCellIdx = 0; newCellIdx < targetMatrix.getCharacters()
					.size(); newCellIdx++) {
				CharacterStateCell targetCell;
				if (newRow
						|| null == originalCharIdxsByNewCharIdx.get(newCellIdx)) {
					targetCell = cellProvider.get();
				} else {
					targetCell = originalTargetCells
							.get(originalCharIdxsByNewCharIdx.get(newCellIdx));
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

			logger.debug("{}: flushing row,  sourceRowIdx: {}", METHOD,
					sourceRowIdx);
			targetRow.accept(setPPodVersionInfoVisitor);
			dao.flush();

			dao.evictEntities(cellsToEvict);
			cellsToEvict.clear();
			dao.evict(targetRow);
		}
	}
}
