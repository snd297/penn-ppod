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
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findIf;

import java.util.List;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.ICell;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IStandardCell;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStandardRow;
import edu.upenn.cis.ppod.imodel.IStandardState;
import edu.upenn.cis.ppod.imodel.IWithPPodId;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
final class CreateOrUpdateStandardMatrix
		extends
		CreateOrUpdateMatrix<IStandardMatrix, IStandardRow, IStandardCell, IStandardState>
		implements ICreateOrUpdateStandardMatrix {

	private final Provider<IStandardCharacter> standardCharacterProvider;
	private final IStandardState.IFactory standardStateFactory;

	@InjectLogger
	private Logger logger;

	private final INewVersionInfo newVersionInfo;

	@Inject
	CreateOrUpdateStandardMatrix(
			final Provider<IStandardRow> rowProvider,
			final Provider<IStandardCharacter> characterProvider,
			final Provider<IStandardCell> cellProvider,
			final IStandardState.IFactory stateFactory,
			final Provider<IAttachment> attachmentProvider,
			@Assisted final IMergeAttachments mergeAttachments,
			@Assisted final IDAO<Object, Long> dao,
			@Assisted final INewVersionInfo newVersionInfo) {
		super(rowProvider, cellProvider, attachmentProvider,
				newVersionInfo, dao);
		this.standardCharacterProvider = characterProvider;
		this.standardStateFactory = stateFactory;
		this.newVersionInfo = newVersionInfo;
	}

	@Override
	public void createOrUpdateMatrix(
			final IStandardMatrix dbMatrix,
			final IStandardMatrix sourceMatrix) {
		final String METHOD = "createOrUpdate(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

		final List<IStandardCharacter> newDbMatrixCharacters = newArrayList();
		int sourceCharacterPosition = -1;
		for (final IStandardCharacter sourceCharacter : sourceMatrix
				.getCharacters()) {
			sourceCharacterPosition++;
			IStandardCharacter newDbCharacter;

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
			newDbCharacter.setMesquiteId(sourceCharacter.getMesquiteId());

			for (final IStandardState sourceState : sourceCharacter.getStates()) {
				IStandardState dbState;
				if (null == (dbState =
						newDbCharacter.getState(sourceState.getStateNumber()))) {
					dbState = standardStateFactory
							.create(sourceState.getStateNumber());
					newDbCharacter.addState(dbState);
					dbState.setVersionInfo(
							newVersionInfo.getNewVersionInfo());
				}
				dbState.setLabel(sourceState.getLabel());
			}
		}

		dbMatrix.setCharacters(newDbMatrixCharacters);

		super.createOrUpdateMatrix(dbMatrix, sourceMatrix);

	}

	@Override
	void handlePolymorphicCell(
			final IStandardCell dbCell,
			final IStandardCell sourceCell) {
		checkNotNull(dbCell);
		checkNotNull(sourceCell);
		checkArgument(sourceCell.getType() == ICell.Type.POLYMORPHIC);
		dbCell.setPolymorphicElements(
				newHashSet(
						transform(
								sourceCell.getElements(),
								IStandardState.getStateNumber)));
	}

	@Override
	void handleSingleCell(
			final IStandardCell dbCell,
			final IStandardCell sourceCell) {
		checkNotNull(dbCell);
		checkNotNull(sourceCell);
		checkArgument(sourceCell.getType() == ICell.Type.SINGLE);
		dbCell.setSingleElement(
				getOnlyElement(
						sourceCell.getElements())
						.getStateNumber());
	}

	@Override
	void handleUncertainCell(
			final IStandardCell dbCell,
			final IStandardCell sourceCell) {
		checkNotNull(dbCell);
		checkNotNull(sourceCell);
		checkArgument(sourceCell.getType() == ICell.Type.UNCERTAIN);
		dbCell.setUncertainElements(
				newHashSet(
						transform(
								sourceCell.getElements(),
								IStandardState.getStateNumber)));
	}
}
