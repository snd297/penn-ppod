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
		targetMatrix.setDocId(sourceMatrix.getDocId());

		final List<OTU> newDbOTUs = newArrayList();
		for (final OTU incomingOTU : sourceMatrix.getOTUs()) {
			final OTU newDbOTU = mergedOTUsBySourceOTU.get(incomingOTU);
			if (newDbOTU == null) {
				throw new AssertionError(
						"couldn't find incomingOTU in persistentOTUsByIncomingOTU");
			}
			newDbOTUs.add(newDbOTU);
		}
		final List<OTU> previousDbOTUs = newArrayList(targetMatrix.getOTUs());
		targetMatrix.setOTUs(newDbOTUs);

		// Now realign rows to new OTU order
		final List<CharacterStateRow> previousDbRows = newArrayList(targetMatrix
				.getRows());
		for (int i = 0; i < targetMatrix.getOTUs().size(); i++) {
			int previousDbOTUIdx = -1;

			for (int j = 0; j < previousDbOTUs.size(); j++) {
				if (previousDbOTUs.get(j).equals(targetMatrix.getOTUs().get(i))) {
					previousDbOTUIdx = j;
					break;
				}
			}
			if (previousDbOTUIdx == -1) {
				targetMatrix.setRow(i, rowProvider.get());
			} else {
				targetMatrix.setRow(i, previousDbRows.get(previousDbOTUIdx));

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
			Character newDbCharacter;
			if (null == (newDbCharacter = findIf(clearedDbCharacters, compose(
					equalTo(incomingCharacter.getPPodId()),
					IUUPPodEntity.getPPodId)))) {
				newDbCharacter = characterProvider.get();
				newDbCharacter.setPPodId();
			}
			targetMatrix.addCharacter(newDbCharacter);
			newDbCharacter.setLabel(incomingCharacter.getLabel());

			for (final CharacterState incomingState : incomingCharacter
					.getStates().values()) {
				CharacterState dbState;
				if (null == (dbState = newDbCharacter.getStates().get(
						incomingState.getStateNumber()))) {
					dbState = newDbCharacter.addState(stateFactory
							.create(incomingState.getStateNumber()));

				}
				dbState.setLabel(incomingState.getLabel());
			}

			oldCharIdxsByNewCharIdx.put(targetMatrix
					.getCharacterIdx(newDbCharacter), oldIdxsByChararacter
					.get(newDbCharacter));

			for (final Attachment incomingAttachment : incomingCharacter
					.getAttachments()) {
				final Set<Attachment> dbAttachments = newDbCharacter
						.getAttachmentsByStringValue(incomingAttachment
								.getStringValue());
				Attachment dbAttachment = getOnlyElement(dbAttachments, null);
				if (dbAttachment == null) {
					dbAttachment = attachmentProvider.get();
					dbAttachment.setPPodId();
				}
				newDbCharacter.addAttachment(dbAttachment);
				mergeAttachment.merge(dbAttachment, incomingAttachment);
			}
		}

		// Now we get the columns to match the Character ordering
		for (final CharacterStateRow dbRow : targetMatrix.getRows()) {
			final List<CharacterStateCell> clearedDbCells = dbRow.clearCells();

			for (int newCellIdx = 0; newCellIdx < targetMatrix.getCharacters()
					.size(); newCellIdx++) {
				if (null == oldCharIdxsByNewCharIdx.get(newCellIdx)) {
					dbRow.addCell(cellProvider.get());
				} else {
					dbRow.addCell(clearedDbCells.get(oldCharIdxsByNewCharIdx
							.get(newCellIdx)));
				}
			}
			while (dbRow.getCells().size() > targetMatrix.getCharacters()
					.size()) {
				dbRow.removeLastCell();
			}
		}

		// We should now have a matrix with the proper cell dimensions and all
		// OTU's and characters done - now let's fill
		// in the cells
		for (final Iterator<CharacterStateRow> incomingRowItr = sourceMatrix
				.getRows().iterator(), dbRowItr = targetMatrix.getRows()
				.iterator(); incomingRowItr.hasNext();) {
			final CharacterStateRow incomingRow = incomingRowItr.next(), dbRow = dbRowItr
					.next();
			for (final ListIterator<CharacterStateCell> incomingCellItr = incomingRow
					.getCells().listIterator(), dbCellItr = dbRow.getCells()
					.listIterator(); incomingCellItr.hasNext();) {
				final CharacterStateCell incomingCell = incomingCellItr.next(), dbCell = dbCellItr
						.next();
				final Set<CharacterState> newDbStates = newHashSet();
				for (final CharacterState incomingState : incomingCell
						.getStates()) {
					newDbStates.add(targetMatrix.getCharacter(
							dbCellItr.previousIndex()).getStates().get(
							incomingState.getStateNumber()));
				}
				dbCell.setTypeAndStates(incomingCell.getType(), newDbStates);
			}
		}
		return targetMatrix;
	}
}
