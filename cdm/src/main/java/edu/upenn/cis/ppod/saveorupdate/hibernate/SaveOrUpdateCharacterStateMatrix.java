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
import static edu.upenn.cis.ppod.util.PPodIterables.equalTo;
import static edu.upenn.cis.ppod.util.PPodIterables.findEach;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;
import java.util.ListIterator;
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
import edu.upenn.cis.ppod.model.IUUPPodEntity;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachment;
import edu.upenn.cis.ppod.saveorupdate.ISaveOrUpdateCharacterStateMatrix;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

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

	@Inject
	SaveOrUpdateCharacterStateMatrix(
			final Provider<Character> characterProvider,
			final Provider<CharacterStateRow> rowProvider,
			final Provider<CharacterStateCell> cellProvider,
			final CharacterState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final IMergeAttachment mergeAttachment) {
		this.characterProvider = characterProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.dao = dao;
		this.mergeAttachment = mergeAttachment;

	}

	public CharacterStateMatrix saveOrUpdate(
			final CharacterStateMatrix targetMatrix,
			final CharacterStateMatrix sourceMatrix,
			final OTUSet newTargetMatrixOTUSet,
			final Map<OTU, OTU> mergedOTUsBySourceOTU,
			final DNACharacter dnaCharacter) {
		final String METHOD = "saveOrUpdate(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(targetMatrix);
		checkNotNull(sourceMatrix);
		checkNotNull(newTargetMatrixOTUSet);

		// dao.evict(newTargetMatrixOTUSet.getStudy());
		// dao.evict(newTargetMatrixOTUSet);

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

		// Move Characters around - start by removing all characters
		final List<Character> clearedTargetCharacters = targetMatrix
				.clearCharacters();
		// Force the clearing of the characters otherwise reorderings will mess
		// us up - it's a hibernate bug
		dao.flush();

		final Map<Character, Integer> oldIdxsByChararacter = newHashMap();
		for (final ListIterator<Character> idx = clearedTargetCharacters
				.listIterator(); idx.hasNext();) {
			oldIdxsByChararacter.put(idx.next(), idx.previousIndex());
		}

		final Map<Integer, Integer> originalCharIdxsByNewCharIdx = newHashMap();
		int sourceCharacterPosition = -1;
		for (final Character sourceCharacter : sourceMatrix.getCharacters()) {
			sourceCharacterPosition++;
			Character newTargetCharacter;
			if (sourceMatrix.getType() == CharacterStateMatrix.Type.DNA) {
				newTargetCharacter = dnaCharacter;
			} else if (null == (newTargetCharacter = findIf(
					clearedTargetCharacters, equalTo(sourceCharacter
							.getPPodId(), IUUPPodEntity.getPPodId)))) {
				newTargetCharacter = characterProvider.get();
				newTargetCharacter.setPPodId();
			}
			targetMatrix.addCharacter(newTargetCharacter);

			if (targetMatrix.getType() == CharacterStateMatrix.Type.STANDARD) {
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
				if (targetMatrix.getType() == CharacterStateMatrix.Type.STANDARD) {
					targetState.setLabel(sourceState.getLabel());
				}
			}

			if (targetMatrix.getType() == CharacterStateMatrix.Type.STANDARD) {
				originalCharIdxsByNewCharIdx.put(targetMatrix.getCharacterIdx()
						.get(newTargetCharacter), oldIdxsByChararacter
						.get(newTargetCharacter));
			} else {
				if (clearedTargetCharacters.size() <= sourceCharacterPosition) {
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
						newTargetCharacter.getAttachments(), equalTo(
								sourceAttachment.getStringValue(),
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
				newRow = true;
			} else {
				// Since we don't store the row->matrix reference
				targetRow.setMatrix(targetMatrix);
				newRow = false;
			}

			if (!newRow && targetRow.getPPodVersion() == null) {
				throw new AssertionError(
						"existing row has now pPOD version number");
			}

			final List<CharacterStateCell> originalTargetCells = targetRow
					.getCells();
			final List<CharacterStateCell> newTargetCells = newArrayListWithCapacity(sourceRow
					.getCells().size());

			for (int newCellIdx = 0; newCellIdx < targetMatrix.getCharacters()
					.size(); newCellIdx++) {
				CharacterStateCell targetCell;
				if (null == originalCharIdxsByNewCharIdx.get(newCellIdx)) {
					targetCell = cellProvider.get();
				} else {
					targetCell = originalTargetCells
							.get(originalCharIdxsByNewCharIdx.get(newCellIdx));
				}
				newTargetCells.add(targetCell);
			}
			targetRow.setCells(newTargetCells);
			for (final CharacterStateCell targetCell : targetRow.getCells()) {

				final CharacterStateCell sourceCell = sourceRow.getCells().get(
						targetCell.getPosition());

				final Set<CharacterState> newTargetStates = newHashSet();
				for (final CharacterState sourceState : sourceCell.getStates()) {
					newTargetStates.add(characters
							.get(targetCell.getPosition()).getStates().get(
									sourceState.getStateNumber()));
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
				cellsToEvict.add(targetCell);
				dao.saveOrUpdate(targetCell);
			}

			dao.saveOrUpdate(targetRow);
			logger.debug("{}: flushing row,  sourceRowIdx: {}", METHOD,
					sourceRowIdx);
			dao.flush();
			dao.evictEntities(cellsToEvict).clear();
			dao.evict(targetRow);
		}
		// dao.saveOrUpdate(newTargetMatrixOTUSet.getStudy());
		// dao.saveOrUpdate(newTargetMatrixOTUSet);
		return targetMatrix;
	}
}
