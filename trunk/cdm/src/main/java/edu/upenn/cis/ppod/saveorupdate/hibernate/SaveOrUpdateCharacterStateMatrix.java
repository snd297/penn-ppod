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
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.equalTo;
import static edu.upenn.cis.ppod.util.PPodIterables.findEach;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.DNAState;
import edu.upenn.cis.ppod.model.IUUPPodEntity;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachment;
import edu.upenn.cis.ppod.saveorupdate.IMergeCharacterStateMatrix;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
public class SaveOrUpdateCharacterStateMatrix implements
		IMergeCharacterStateMatrix {

	private final Provider<Character> characterProvider;
	private final Provider<CharacterStateRow> rowProvider;
	private final Provider<CharacterStateCell> cellProvider;
	private final CharacterState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachment mergeAttachment;
	private final Session session;

	@InjectLogger
	private Logger logger;

	@Inject
	SaveOrUpdateCharacterStateMatrix(
			final Provider<Character> characterProvider,
			final Provider<CharacterStateRow> rowProvider,
			final Provider<CharacterStateCell> cellProvider,
			final CharacterState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			@Assisted final IMergeAttachment mergeAttachment,
			@Assisted final Session session) {
		this.characterProvider = characterProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.mergeAttachment = mergeAttachment;
		this.session = session;
	}

	public CharacterStateMatrix merge(final CharacterStateMatrix targetMatrix,
			final CharacterStateMatrix sourceMatrix,
			final OTUSet newTargetMatrixOTUSet,
			final Map<OTU, OTU> mergedOTUsBySourceOTU,
			final DNACharacter dnaCharacter) {
		final String METHOD = "merge(...)";
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

		// Move Characters around - start by removing all characters
		final List<Character> clearedTargetCharacters = targetMatrix
				.clearCharacters();
		final Map<Character, Integer> oldIdxsByChararacter = newHashMap();
		for (final ListIterator<Character> idx = clearedTargetCharacters
				.listIterator(); idx.hasNext();) {
			oldIdxsByChararacter.put(idx.next(), idx.previousIndex());
		}

		// int characterCounter = -1;
		final Map<Integer, Integer> originalCharIdxsByNewCharIdx = newHashMap();
		for (final Character sourceCharacter : sourceMatrix.getCharacters()) {

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
			if (!(newTargetCharacter instanceof DNACharacter)) {
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
				if (!(targetState instanceof DNAState)) {
					targetState.setLabel(sourceState.getLabel());
				}
			}

			originalCharIdxsByNewCharIdx.put(targetMatrix.getCharacterIdx()
					.get(newTargetCharacter), oldIdxsByChararacter
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
				session.saveOrUpdate(newTargetCharacter);
				session.saveOrUpdate(targetAttachment);
			}
		}

		int sourceRowIdx = -1;
		int cellCounter = -1;
		for (final OTU targetOTU : targetMatrix.getOTUs()) {
			sourceRowIdx++;
			CharacterStateRow targetRow = targetMatrix.getRow(targetOTU);
			if (targetRow == null) {
				targetRow = rowProvider.get();
				targetMatrix.setRow(targetOTU, targetRow);
			} else {
				// Since we don't store the row->matrix reference
				targetRow.setMatrix(targetMatrix);
			}

			while (targetRow.getCells().size() > targetMatrix.getCharacters()
					.size()) {
				targetRow.removeLastCell();
			}

			final List<CharacterStateCell> clearedTargetCells = targetRow
					.clearCells();

			for (int newCellIdx = 0; newCellIdx < targetMatrix.getCharacters()
					.size(); newCellIdx++) {
				CharacterStateCell targetCell;
				if (null == originalCharIdxsByNewCharIdx.get(newCellIdx)) {
					targetCell = targetRow.addCell(cellProvider.get());
				} else {
					targetCell = targetRow.addCell(clearedTargetCells
							.get(originalCharIdxsByNewCharIdx.get(newCellIdx)));
					// Because we don't store the cell->row reference.
					targetCell.setRow(targetRow);
				}

				final CharacterStateCell sourceCell = sourceMatrix.getRows()
						.get(sourceRowIdx).getCells().get(newCellIdx);

				final Set<CharacterState> newTargetStates = newHashSet();
				for (final CharacterState sourceState : sourceCell.getStates()) {
					newTargetStates.add(targetMatrix.getCharacters().get(
							newCellIdx).getStates().get(
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
				session.saveOrUpdate(targetCell);
				if (cellCounter++ % 20 == 0) {
					logger.debug("{}: flushing cells, cellCounter: {}", METHOD,
							cellCounter);
					session.flush();
					session.clear();
				}
			}
			session.saveOrUpdate(targetRow);
			logger.debug("{}: flushing row,  sourceRowIdx: {}", METHOD,
					sourceRowIdx);
			session.flush();
			session.clear();
		}

		return targetMatrix;
	}
}
