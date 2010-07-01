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
package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
final class CreateOrUpdateStandardMatrix
		implements ICreateOrUpdateStandardMatrix {

	private final Provider<StandardCharacter> characterProvider;
	private final StandardState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachments mergeAttachments;
	private final IDAO<Object, Long> dao;

	@InjectLogger
	private Logger logger;

	private final INewVersionInfo newVersionInfo;
	private final ICreateOrUpdateMatrix.IFactory<StandardMatrix, StandardRow, StandardCell, StandardState> createOrUpdatMatrixFactory;

	@Inject
	CreateOrUpdateStandardMatrix(
			final Provider<StandardCharacter> characterProvider,
			final StandardState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			final ICreateOrUpdateMatrix.IFactory<StandardMatrix, StandardRow, StandardCell, StandardState> createOrUpdatMatrixFactory,
			@Assisted final INewVersionInfo newVersionInfo,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final IMergeAttachments mergeAttachments) {
		this.characterProvider = characterProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.createOrUpdatMatrixFactory = createOrUpdatMatrixFactory;
		this.dao = dao;
		this.mergeAttachments = mergeAttachments;
		this.newVersionInfo = newVersionInfo;
	}

	public void createOrUpdateMatrix(
			final StandardMatrix dbMatrix,
			final StandardMatrix sourceMatrix) {
		final String METHOD = "createOrUpdate(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

		final List<StandardCharacter> newDbMatrixCharacters = newArrayList();
		int sourceCharacterPosition = -1;
		for (final StandardCharacter sourceCharacter : sourceMatrix
				.getCharacters()) {
			sourceCharacterPosition++;
			StandardCharacter newDbCharacter;

			if (null == (newDbCharacter =
					findIf(dbMatrix
							.getCharacters(),
							compose(
									equalTo(
									sourceCharacter.getPPodId()),
									IWithPPodId.getPPodId)))) {
				newDbCharacter = characterProvider.get();
				newDbCharacter
						.setVersionInfo(newVersionInfo.getNewVersionInfo());
				newDbCharacter.setPPodId();
			}

			newDbMatrixCharacters.add(newDbCharacter);

			newDbCharacter.setLabel(sourceCharacter.getLabel());

			for (final StandardState sourceState : sourceCharacter.getStates()) {
				StandardState dbState;
				if (null == (dbState = newDbCharacter.getState(
						sourceState.getStateNumber()))) {
					dbState = stateFactory
							.create(sourceState.getStateNumber());
					newDbCharacter.addState(dbState);
					dbState.setVersionInfo(newVersionInfo
							.getNewVersionInfo());
				}

				dbState.setLabel(sourceState.getLabel());

			}

			final Set<Attachment> newDbAttachments = newHashSet();

			for (final Attachment sourceAttachment : sourceCharacter
					.getAttachments()) {
				final ImmutableSet<Attachment> newDbCharacterAttachments =
						ImmutableSet.copyOf(
								newDbCharacter.getAttachments());
				Attachment dbAttachment =
						findIf(newDbCharacterAttachments,
										compose(
												equalTo(sourceAttachment
														.getStringValue()),
												Attachment.getStringValue));

				if (dbAttachment == null) {
					dbAttachment = attachmentProvider.get();
					dbAttachment
							.setVersionInfo(
									newVersionInfo.getNewVersionInfo());
					dbAttachment.setPPodId();
				}

				newDbAttachments.add(dbAttachment);
				mergeAttachments
						.mergeAttachments(dbAttachment, sourceAttachment);
			}
			newDbCharacter.setAttachments(newDbAttachments);
		}

		dbMatrix.setCharacters(newDbMatrixCharacters);

		final ICreateOrUpdateMatrix<StandardMatrix, StandardRow, StandardCell, StandardState> createOrUpdatMatrix =
				createOrUpdatMatrixFactory
						.create(newVersionInfo, dao);

		createOrUpdatMatrix
						.createOrUpdateMatrix(
								dbMatrix,
								sourceMatrix);

	}
}
