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

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Cell;
import edu.upenn.cis.ppod.model.Matrix;
import edu.upenn.cis.ppod.model.Row;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;
import edu.upenn.cis.ppod.services.ppodentity.MatrixInfo;

public interface ICreateOrUpdateMatrix<M extends Matrix<R>, R extends Row<C>, C extends Cell<E>, E> {

	/**
	 * Assumes {@code dbMatrix} is in a persistent state.
	 * 
	 * @param dbMatrix
	 * @param sourceMatrix
	 * @return
	 */
	MatrixInfo createOrUpdateMatrix(M dbMatrix, M sourceMatrix);

	interface IFactory<M extends Matrix<R>, R extends Row<C>, C extends Cell<E>, E> {
		ICreateOrUpdateMatrix<M, R, C, E> create(
				INewVersionInfo newVersionInfo,
				IDAO<Object, Long> dao);
	}
}