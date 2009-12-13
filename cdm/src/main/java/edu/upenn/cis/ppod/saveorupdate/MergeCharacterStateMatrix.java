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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
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
			final Map<OTU, OTU> mergedOTUsBySourceOTU) {
		checkArgument(targetMatrix.getPPodId() != null,
				"targetMatrix must have its pPOD ID set");
		checkArgument(targetMatrix.getOTUSet() != null,
				"targetMatrix must be attached to an OTU set");
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
		final List<OTU> previousTargetOTUs = newArrayList(targetMatrix
				.getOTUs());
		targetMatrix.setOTUs(newTargetOTUs);

		// Now realign rows to new OTU order
		final List<CharacterStateRow> previousTargetRows = newArrayList(targetMatrix
				.getRows());
		for (int i = 0; i < targetMatrix.getOTUs().size(); i++) {
			int previousTargetOTUIdx = -1;

			for (int j = 0; j < previousTargetOTUs.size(); j++) {
				if (previousTargetOTUs.get(j).equals(
						targetMatrix.getOTUs().get(i))) {
					previousTargetOTUIdx = j;
					break;
				}
			}
			if (previousTargetOTUIdx == -1) {
				targetMatrix.setRow(i, rowProvider.get());
			} else {
				targetMatrix.setRow(i, previousTargetRows
						.get(previousTargetOTUIdx));

			}
		}

		// Get rid of deleted rows
		while (targetMatrix.getRows().size() > targetMatrix.getOTUs().size()) {
			targetMatrix.removeLastRow();
		}

		final List<Character> clearedDbCharacters = targetMatrix
				.clearCharacters();
		final Map<Character, Integer> oldIdxsByChararacter = newHashMap();
		for (final ListIterator<Character> idx = clearedDbCharacters
				.listIterator(); idx.hasNext();) {
			oldIdxsByChararacter.put(idx.next(), idx.previousIndex());
		}

		// Move Characters around
		final Map<Integer, Integer> oldCharIdxsByNewCharIdx = newHashMap();
		for (final Character incomingCharacter : sourceMatrix.getCharacters()) {
			Character newTargetCharacter;
			if (null == (newTargetCharacter = findIf(clearedDbCharacters,
					compose(equalTo(incomingCharacter.getPPodId()),
							IUUPPodEntity.getPPodId)))) {
				newTargetCharacter = characterProvider.get();
				newTargetCharacter.setPPodId();
			}
			targetMatrix.addCharacter(newTargetCharacter);
			newTargetCharacter.setLabel(incomingCharacter.getLabel());

			for (final CharacterState sourceState : incomingCharacter
					.getStates().values()) {
				CharacterState targetState;
				if (null == (targetState = newTargetCharacter.getStates().get(
						sourceState.getStateNumber()))) {
					targetState = newTargetCharacter.addState(stateFactory
							.create(sourceState.getStateNumber()));

				}
				targetState.setLabel(sourceState.getLabel());
			}

			oldCharIdxsByNewCharIdx.put(targetMatrix
					.getCharacterIdx(newTargetCharacter), oldIdxsByChararacter
					.get(newTargetCharacter));

			for (final Attachment sourceAttachment : incomingCharacter
					.getAttachments()) {
				final Set<Attachment> targetAttachments = newTargetCharacter
						.getAttachmentsByStringValue(sourceAttachment
								.getStringValue());
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

		// Now we get the columns to match the Character ordering
		for (final CharacterStateRow targetRow : targetMatrix.getRows()) {
			final List<CharacterStateCell> clearedDbCells = targetRow
					.clearCells();

			for (int newCellIdx = 0; newCellIdx < targetMatrix.getCharacters()
					.size(); newCellIdx++) {
				if (null == oldCharIdxsByNewCharIdx.get(newCellIdx)) {
					targetRow.addCell(cellProvider.get());
				} else {
					targetRow.addCell(clearedDbCells
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
