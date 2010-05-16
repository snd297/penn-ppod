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
import static com.google.common.collect.Sets.filter;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

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
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
final class SaveOrUpdateCharacterStateMatrix implements
		ISaveOrUpdateCharacterStateMatrix {

	private final Provider<Character> characterProvider;
	private final CharacterState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachments mergeAttachments;
	private final IDAO<Object, Long> dao;

	@InjectLogger
	private Logger logger;

	private final INewPPodVersionInfo newPPodVersionInfo;
	private final ISaveOrUpdateMatrix.IFactory<CharacterStateRow, CharacterStateCell, CharacterState> saveOrUpdateMatrixFactory;

	@Inject
	SaveOrUpdateCharacterStateMatrix(
			final Provider<Character> characterProvider,
			final CharacterState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			final ISaveOrUpdateMatrix.IFactory<CharacterStateRow, CharacterStateCell, CharacterState> saveOrUpdateMatrixFactory,
			@Assisted final INewPPodVersionInfo newPPodVersionInfo,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final IMergeAttachments mergeAttachments) {
		this.characterProvider = characterProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.saveOrUpdateMatrixFactory = saveOrUpdateMatrixFactory;
		this.dao = dao;
		this.mergeAttachments = mergeAttachments;
		this.newPPodVersionInfo = newPPodVersionInfo;
	}

	public MatrixInfo saveOrUpdate(
			final CharacterStateMatrix dbMatrix,
			final CharacterStateMatrix sourceMatrix,
			final DNACharacter dnaCharacter) {
		final String METHOD = "saveOrUpdate(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

// final MatrixInfo matrixInfo = matrixInfoProvider.get();

		dbMatrix.setLabel(sourceMatrix.getLabel());
		dbMatrix.setDescription(sourceMatrix.getDescription());

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		if (dbMatrix.getDocId() == null) {
			dbMatrix.setDocId(sourceMatrix.getDocId());
		}

		final List<Character> newDbMatrixCharacters = newArrayList();
		int sourceCharacterPosition = -1;
		for (final Iterator<Character> sourceCharactersItr = sourceMatrix
				.getCharactersIterator(); sourceCharactersItr.hasNext();) {
			final Character sourceCharacter = sourceCharactersItr.next();
			sourceCharacterPosition++;
			Character newDbCharacter;
			if (sourceMatrix instanceof DNAStateMatrix) {
				newDbCharacter = dnaCharacter;
			} else if (null == (newDbCharacter =
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

		final ISaveOrUpdateMatrix<CharacterStateRow, CharacterStateCell, CharacterState> saveOrUpdateMatrix =
				saveOrUpdateMatrixFactory
						.create(newPPodVersionInfo, dao);

		final MatrixInfo matrixInfo = saveOrUpdateMatrix.saveOrUpdate(dbMatrix,
				sourceMatrix);

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

		// matrixInfo.setPPodId(dbMatrix.getPPodId());
		return matrixInfo;
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
