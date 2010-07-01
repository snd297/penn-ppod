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

import static com.google.common.collect.Iterables.get;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.DNACell;
import edu.upenn.cis.ppod.model.DNAMatrix;
import edu.upenn.cis.ppod.model.DNANucleotide;
import edu.upenn.cis.ppod.model.DNARow;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;

final class CreateOrUpdateDNAMatrix
		extends CreateOrUpdateMatrix<DNAMatrix, DNARow, DNACell, DNANucleotide>
		implements ICreateOrUpdateDNAMatrix {

	@Inject
	CreateOrUpdateDNAMatrix(Provider<DNARow> rowProvider,
			Provider<DNACell> cellProvider,
			Provider<Attachment> attachmentProvider,
			Provider<MatrixInfo> matrixInfoProvider,
			@Assisted INewVersionInfo newVersionInfo,
			@Assisted IDAO<Object, Long> dao) {
		super(rowProvider, cellProvider, attachmentProvider,
				newVersionInfo, dao);
	}

	@Override
	public void createOrUpdateMatrix(
			final DNAMatrix dbMatrix,
			final DNAMatrix sourceMatrix) {
		dbMatrix.setColumnsSize(
				get(sourceMatrix.getRows()
								.values(), 0)
						.getCells()
						.size());
		super.createOrUpdateMatrix(dbMatrix, sourceMatrix);
	}

}
