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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;

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
import edu.upenn.cis.ppod.modelinterfaces.ICell;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
final class CreateOrUpdateStandardMatrix
		extends
		CreateOrUpdateMatrix<StandardMatrix, StandardRow, StandardCell, StandardState>
		implements ICreateOrUpdateStandardMatrix {

	private final Provider<StandardCharacter> standardCharacterProvider;
	private final StandardState.IFactory standardStateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachments mergeAttachments;

	@InjectLogger
	private Logger logger;

	private final INewVersionInfo newVersionInfo;

	@Inject
	CreateOrUpdateStandardMatrix(
			final Provider<StandardRow> rowProvider,
			final Provider<StandardCell> cellProvider,
			final Provider<StandardCharacter> characterProvider,
			final StandardState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			@Assisted final IMergeAttachments mergeAttachments,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final INewVersionInfo newVersionInfo) {
		super(rowProvider, cellProvider, attachmentProvider,
				newVersionInfo, dao);
		this.standardCharacterProvider = characterProvider;
		this.standardStateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.mergeAttachments = mergeAttachments;
		this.newVersionInfo = newVersionInfo;
	}

	@Override
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
				newDbCharacter = standardCharacterProvider.get();
				newDbCharacter.setVersionInfo(
						newVersionInfo.getNewVersionInfo());
				newDbCharacter.setPPodId();
			}

			newDbMatrixCharacters.add(newDbCharacter);

			newDbCharacter.setLabel(sourceCharacter.getLabel());

			for (final StandardState sourceState : sourceCharacter.getStates()) {
				StandardState dbState;
				if (null == (dbState = newDbCharacter.getState(
						sourceState.getStateNumber()))) {
					dbState = standardStateFactory
							.create(sourceState.getStateNumber());
					newDbCharacter.addState(dbState);
					dbState.setVersionInfo(newVersionInfo
							.getNewVersionInfo());
				}

				dbState.setLabel(sourceState.getLabel());

			}

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

				newDbCharacter.addAttachment(dbAttachment);
				mergeAttachments
						.mergeAttachments(dbAttachment, sourceAttachment);
			}
		}

		dbMatrix.setCharacters(newDbMatrixCharacters);

		super.createOrUpdateMatrix(dbMatrix, sourceMatrix);

	}

	@Override
	void handleSingleCell(
			final StandardCell dbCell,
			final StandardCell sourceCell) {
		checkNotNull(dbCell);
		checkNotNull(sourceCell);
		checkArgument(sourceCell.getType() == ICell.Type.SINGLE);
		dbCell.setSingleElement(getOnlyElement(sourceCell.getElements()));
	}

	@Override
	void handlePolymorphicCell(
			final StandardCell dbCell,
			final StandardCell sourceCell) {
		checkNotNull(dbCell);
		checkNotNull(sourceCell);
		checkArgument(sourceCell.getType() == ICell.Type.POLYMORPHIC);
		dbCell.setPolymorphicElements(sourceCell.getElements());
	}
}
