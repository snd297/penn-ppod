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
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findEach;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.IUUPPodEntity;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;

/**
 * @author Sam Donnelly
 */
public class MergeCharacterStateMatrix implements IMergeCharacterStateMatrix {

	private final Provider<Character> characterProvider;
	private final Provider<CharacterStateRow> rowProvider;
	private final Provider<CharacterStateCell> cellProvider;
	private final CharacterState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachment mergeAttachment;

	@Inject
	MergeCharacterStateMatrix(final Provider<Character> characterProvider,
			final Provider<CharacterStateRow> rowProvider,
			final Provider<CharacterStateCell> cellProvider,
			final CharacterState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			@Assisted final IMergeAttachment mergeAttachment) {
		this.characterProvider = characterProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.mergeAttachment = mergeAttachment;
	}

	public CharacterStateMatrix merge(final CharacterStateMatrix targetMatrix,
			final CharacterStateMatrix sourceMatrix,
			final OTUSet newTargetMatrixOTUSet,
			final Map<OTU, OTU> mergedOTUsBySourceOTU) {
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

		// Move Characters around - start by removing all characters
		final List<Character> clearedTargetCharacters = targetMatrix
				.clearCharacters();
		final Map<Character, Integer> oldIdxsByChararacter = newHashMap();
		for (final ListIterator<Character> idx = clearedTargetCharacters
				.listIterator(); idx.hasNext();) {
			oldIdxsByChararacter.put(idx.next(), idx.previousIndex());
		}

		final Map<Integer, Integer> oldCharIdxsByNewCharIdx = newHashMap();
		for (final Character sourceCharacter : sourceMatrix.getCharacters()) {
			Character newTargetCharacter;
			if (null == (newTargetCharacter = findIf(clearedTargetCharacters,
					compose(equalTo(sourceCharacter.getPPodId()),
							IUUPPodEntity.getPPodId)))) {
				newTargetCharacter = characterProvider.get();
				newTargetCharacter.setPPodId();
			}
			targetMatrix.addCharacter(newTargetCharacter);
			newTargetCharacter.setLabel(sourceCharacter.getLabel());

			for (final CharacterState sourceState : sourceCharacter.getStates()
					.values()) {
				CharacterState targetState;
				if (null == (targetState = newTargetCharacter.getStates().get(
						sourceState.getStateNumber()))) {
					targetState = newTargetCharacter.addState(stateFactory
							.create(sourceState.getStateNumber()));

				}
				targetState.setLabel(sourceState.getLabel());
			}

			oldCharIdxsByNewCharIdx.put(targetMatrix.getCharacterIdx().get(
					newTargetCharacter), oldIdxsByChararacter
					.get(newTargetCharacter));

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
					targetAttachment.setPPodId();
				}
				newTargetCharacter.addAttachment(targetAttachment);
				mergeAttachment.merge(targetAttachment, sourceAttachment);
			}
		}

		for (final OTU targetOTU : targetMatrix.getOTUs()) {
			CharacterStateRow targetRow = targetMatrix.getRow(targetOTU);
			if (targetRow == null) {
				targetRow = rowProvider.get();
				targetMatrix.setRow(targetOTU, targetRow);
			}
			final List<CharacterStateCell> clearedTargetCells = targetRow
					.clearCells();

			for (int newCellIdx = 0; newCellIdx < targetMatrix.getCharacters()
					.size(); newCellIdx++) {
				if (null == oldCharIdxsByNewCharIdx.get(newCellIdx)) {
					targetRow.addCell(cellProvider.get());
				} else {
					targetRow.addCell(clearedTargetCells
							.get(oldCharIdxsByNewCharIdx.get(newCellIdx)));
				}
			}
			while (targetRow.getCells().size() > targetMatrix.getCharacters()
					.size()) {
				targetRow.removeLastCell();
			}
		}

		// We should now have a matrix with the proper cell dimensions and all
		// OTU's and characters done - now let's fill
		// in the cells
		for (final Iterator<CharacterStateRow> sourceRowItr = sourceMatrix
				.getRows().iterator(), targetRowItr = targetMatrix.getRows()
				.iterator(); sourceRowItr.hasNext();) {
			final CharacterStateRow sourceRow = sourceRowItr.next(), targetRow = targetRowItr
					.next();
			for (final ListIterator<CharacterStateCell> sourceCellItr = sourceRow
					.getCells().listIterator(), targetCellItr = targetRow
					.getCells().listIterator(); sourceCellItr.hasNext();) {
				final CharacterStateCell sourceCell = sourceCellItr.next(), targetCell = targetCellItr
						.next();
				final Set<CharacterState> newTargetStates = newHashSet();
				for (final CharacterState sourceState : sourceCell.getStates()) {
					newTargetStates.add(targetMatrix.getCharacter(
							targetCellItr.previousIndex()).getStates().get(
							sourceState.getStateNumber()));
				}
				targetCell.setTypeAndStates(sourceCell.getType(),
						newTargetStates);
			}
		}
		return targetMatrix;
	}
}
