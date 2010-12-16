/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.createorupdate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.imodel.DNANucleotide;
import edu.upenn.cis.ppod.imodel.ICell;
import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IVersionInfo;
import edu.upenn.cis.ppod.model.ModelFactory;

class CreateOrUpdateDNAMatrix
		extends
		CreateOrUpdateMatrix<IDNAMatrix, IDNARow, IDNACell, DNANucleotide>
		implements ICreateOrUpdateDNAMatrix {

	@Inject
	CreateOrUpdateDNAMatrix(
			final IObjectWithLongIdDAO dao,
			final INewVersionInfo newVersionInfo) {
		super(
				dao,
				newVersionInfo);

	}

	public void createOrUpdateMatrix(
			final IDNAMatrix dbMatrix,
			final IDNAMatrix sourceMatrix) {

		final int[] sourceToDbCharPositions =
				new int[sourceMatrix.getColumnsSize()];

		for (int i = 0; i < sourceToDbCharPositions.length; i++) {
			if (i < dbMatrix.getColumnsSize()) {
				sourceToDbCharPositions[i] = i;
			} else {
				sourceToDbCharPositions[i] = -1;
			}
		}

		dbMatrix.setColumnsSize(
				get(sourceMatrix.getRows()
						.values(), 0)
						.getCells()
						.size());

		super.createOrUpdateMatrixHelper(
				dbMatrix,
				sourceMatrix,
				sourceToDbCharPositions);
	}

	@Override
	protected void handlePolymorphicCell(final IDNACell targetCell,
			final IDNACell sourceCell) {
		checkArgument(sourceCell.getType() == ICell.Type.POLYMORPHIC);
		targetCell.setPolymorphicElements(
				sourceCell.getElements(),
				sourceCell.getLowerCase());
	}

	@Override
	protected void handleSingleCell(final IDNACell targetCell,
			final IDNACell sourceCell) {
		checkArgument(sourceCell.getType() == ICell.Type.SINGLE);
		targetCell.setSingleElement(
				getOnlyElement(sourceCell.getElements()),
				sourceCell.getLowerCase());
	}

	@Override
	protected void handleUncertainCell(final IDNACell targetCell,
			final IDNACell sourceCell) {
		checkArgument(sourceCell.getType() == ICell.Type.UNCERTAIN);
		targetCell.setUncertainElements(sourceCell.getElements());
	}

	@Override
	protected IDNACell newC(final IVersionInfo versionInfo) {
		return ModelFactory.newDNACell(versionInfo);
	}

	@Override
	protected IDNARow newR(final IVersionInfo versionInfo) {
		return ModelFactory.newDNARow(versionInfo);
	}
}
