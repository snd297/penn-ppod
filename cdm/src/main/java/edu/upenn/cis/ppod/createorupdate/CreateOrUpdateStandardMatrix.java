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
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.dto.IHasPPodId;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.dto.PPodStandardCell;
import edu.upenn.cis.ppod.dto.PPodStandardCharacter;
import edu.upenn.cis.ppod.dto.PPodStandardMatrix;
import edu.upenn.cis.ppod.dto.PPodStandardState;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.ModelFactory;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.VersionInfo;

/**
 * @author Sam Donnelly
 */
final class CreateOrUpdateStandardMatrix
		extends
		CreateOrUpdateMatrix<StandardMatrix, StandardRow, StandardCell, StandardState>
		implements ICreateOrUpdateStandardMatrix {

	private static Logger logger =
			LoggerFactory.getLogger(CreateOrUpdateMatrix.class);

	private final INewVersionInfo newVersionInfo;

	@Inject
	CreateOrUpdateStandardMatrix(
			final IObjectWithLongIdDAO dao,
			final INewVersionInfo newVersionInfo) {
		super(dao, newVersionInfo);
		this.newVersionInfo = newVersionInfo;
	}

	public void createOrUpdateMatrix(
			final StandardMatrix dbMatrix,
			final PPodStandardMatrix sourceMatrix) {
		final String METHOD = "createOrUpdate(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(dbMatrix);
		checkNotNull(sourceMatrix);

		final int[] sourceToDbCharPositions =
				new int[sourceMatrix.getCharacters().size()];

		final List<StandardCharacter> newDbMatrixCharacters = newArrayList();
		int sourceCharacterPosition = -1;
		for (final PPodStandardCharacter sourceCharacter : sourceMatrix
				.getCharacters()) {
			sourceCharacterPosition++;
			StandardCharacter newDbCharacter;

			if (null == (newDbCharacter =
									find(dbMatrix
											.getCharacters(),
											compose(
													equalTo(
													sourceCharacter.getPPodId()),
													IHasPPodId.getPPodId),
													null))) {
				newDbCharacter = ModelFactory
						.newStandardCharacter(newVersionInfo
								.getNewVersionInfo());
				sourceToDbCharPositions[sourceCharacterPosition] = -1;
			}

			// Will be setting it to -1 if it's not present
			sourceToDbCharPositions[sourceCharacterPosition] =
					dbMatrix.getCharacters().indexOf(newDbCharacter);

			newDbMatrixCharacters.add(newDbCharacter);

			newDbCharacter.setLabel(sourceCharacter.getLabel());
			newDbCharacter.setMesquiteId(sourceCharacter.getMesquiteId());

			for (final PPodStandardState sourceState : sourceCharacter
					.getStates()) {
				StandardState dbState;
				if (null == (dbState =
						newDbCharacter.getState(sourceState.getStateNumber()))) {
					dbState = new StandardState(sourceState.getStateNumber());
					newDbCharacter.addState(dbState);
					dbState.setVersionInfo(
							newVersionInfo.getNewVersionInfo());
				}
				dbState.setLabel(sourceState.getLabel());
			}
		}

		dbMatrix.setCharacters(newDbMatrixCharacters);

		super.createOrUpdateMatrixHelper(dbMatrix, sourceMatrix,
				sourceToDbCharPositions);

	}

	@Override
	protected void handlePolymorphicCell(
			final StandardCell targetCell,
			final PPodStandardCell sourceCell) {
		checkArgument(sourceCell.getType() == PPodCellType.POLYMORPHIC);
		final Set<Integer> sourceStateNumbers = sourceCell.getStates();
		targetCell.setPolymorphicWithStateNos(sourceStateNumbers);
	}

	@Override
	protected void handleSingleCell(final StandardCell targetCell,
			final PPodStandardCell sourceCell) {
		checkArgument(sourceCell.getType() == PPodCellType.SINGLE);
		targetCell.setSingleWithStateNo(getOnlyElement(sourceCell.getStates()));
	}

	@Override
	protected void handleUncertainCell(final StandardCell targetCell,
			final PPodStandardCell sourceCell) {
		checkArgument(sourceCell.getType() == PPodCellType.UNCERTAIN);
		targetCell.setUncertainWithStateNos(sourceCell.getStates());
	}

	@Override
	protected StandardCell newC(final VersionInfo versionInfo) {
		return ModelFactory.newStandardCell(versionInfo);
	}

	@Override
	protected StandardRow newR(final VersionInfo versionInfo) {
		return ModelFactory.newStandardRow(versionInfo);
	}
}
