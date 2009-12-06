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
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.saveorupdate.hibernate.ISaveOrUpdateAttachmentHibernateFactory;

/**
 * @author Sam Donnelly
 */
public class SaveOrUpdateMatrix implements ISaveOrUpdateMatrix {

	private final Provider<Character> characterProvider;
	private final Provider<CharacterStateRow> rowProvider;
	private final Provider<CharacterStateCell> cellProvider;
	private final CharacterState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final ISaveOrUpdateAttachment saveOrUpdateAttachment;

	@Inject
	SaveOrUpdateMatrix(
			final Provider<Character> characterProvider,
			final Provider<CharacterStateRow> rowProvider,
			final Provider<CharacterStateCell> cellProvider,
			final CharacterState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			final ISaveOrUpdateAttachmentHibernateFactory saveOrUpdateAttachmentFactory,
			@Assisted final ISaveOrUpdateAttachment saveOrUpdateAttachment) {
		this.characterProvider = characterProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.saveOrUpdateAttachment = saveOrUpdateAttachment;
	}

	public CharacterStateMatrix saveOrUpdate(
			final CharacterStateMatrix incomingMatrix,
			final CharacterStateMatrix dbMatrix, final OTUSet dbOTUSet,
			final Map<OTU, OTU> dbOTUsByIncomingOTU) {
		checkArgument(dbMatrix.getPPodId() != null,
				"dbMatrix must have its pPOD ID set");
		dbMatrix.setLabel(incomingMatrix.getLabel());
		dbMatrix.setDescription(incomingMatrix.getDescription());
		dbOTUSet.addMatrix(dbMatrix);

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		dbMatrix.setDocId(incomingMatrix.getDocId());

		final List<OTU> newDbOTUs = newArrayList();
		for (final OTU incomingOTU : incomingMatrix.getOTUs()) {
			final OTU newDbOTU = dbOTUsByIncomingOTU.get(incomingOTU);
			if (newDbOTU == null) {
				throw new AssertionError(
						"couldn't find incomingOTU in persistentOTUsByIncomingOTU");
			}
			newDbOTUs.add(newDbOTU);
		}
		final List<OTU> previousDbOTUs = newArrayList(dbMatrix.getOTUs());
		dbMatrix.setOTUs(newDbOTUs);

		// Now realign rows to new OTU order
		final List<CharacterStateRow> previousDbRows = newArrayList(dbMatrix
				.getRows());
		for (int i = 0; i < dbMatrix.getOTUs().size(); i++) {
			int previousDbOTUIdx = -1;

			for (int j = 0; j < previousDbOTUs.size(); j++) {
				if (previousDbOTUs.get(j).equals(dbMatrix.getOTUs().get(i))) {
					previousDbOTUIdx = j;
					break;
				}
			}
			if (previousDbOTUIdx == -1) {
				dbMatrix.setRow(i, rowProvider.get());
			} else {
				dbMatrix.setRow(i, previousDbRows.get(previousDbOTUIdx));

			}
		}

		// Get rid of deleted rows
		while (dbMatrix.getRows().size() > dbMatrix.getOTUs().size()) {
			dbMatrix.removeLastRow();
		}

		final List<Character> clearedDbCharacters = dbMatrix.clearCharacters();
		final Map<Character, Integer> oldIdxsByChararacter = newHashMap();
		for (final ListIterator<Character> idx = clearedDbCharacters
				.listIterator(); idx.hasNext();) {
			oldIdxsByChararacter.put(idx.next(), idx.previousIndex());
		}

		// Move Characters around
		final Map<Integer, Integer> oldCharIdxsByNewCharIdx = newHashMap();
		for (final Character incomingCharacter : incomingMatrix.getCharacters()) {
			Character newDbCharacter;
			if (null == (newDbCharacter = findIf(clearedDbCharacters, compose(
					equalTo(incomingCharacter.getPPodId()),
					IUUPPodEntity.getPPodId)))) {
				newDbCharacter = characterProvider.get();
				newDbCharacter.setPPodId();
			}
			dbMatrix.addCharacter(newDbCharacter);
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

			oldCharIdxsByNewCharIdx.put(dbMatrix
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
				saveOrUpdateAttachment.saveOrUpdate(incomingAttachment,
						dbAttachment);
			}
		}

		// Now we get the columns to match the Character ordering
		for (final CharacterStateRow dbRow : dbMatrix.getRows()) {
			final List<CharacterStateCell> clearedDbCells = dbRow.clearCells();

			for (int newCellIdx = 0; newCellIdx < dbMatrix.getCharacters()
					.size(); newCellIdx++) {
				if (null == oldCharIdxsByNewCharIdx.get(newCellIdx)) {
					dbRow.addCell(cellProvider.get());
				} else {
					dbRow.addCell(clearedDbCells.get(oldCharIdxsByNewCharIdx
							.get(newCellIdx)));
				}
			}
			while (dbRow.getCells().size() > dbMatrix.getCharacters().size()) {
				dbRow.removeLastCell();
			}
		}

		// We should now have a matrix with the proper cell dimensions and all
		// OTU's and characters done - now let's fill
		// in the cells
		for (final Iterator<CharacterStateRow> incomingRowItr = incomingMatrix
				.getRows().iterator(), dbRowItr = dbMatrix.getRows().iterator(); incomingRowItr
				.hasNext();) {
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
					newDbStates.add(dbMatrix.getCharacter(
							dbCellItr.previousIndex()).getStates().get(
							incomingState.getStateNumber()));
				}
				dbCell.setTypeAndStates(incomingCell.getType(), newDbStates);
			}
		}
		return dbMatrix;
	}
}
