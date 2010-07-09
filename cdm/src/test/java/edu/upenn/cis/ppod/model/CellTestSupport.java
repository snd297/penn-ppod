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
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNull;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Methods for testing {@link Cell}.
 * 
 * @author Sam Donnelly
 */
public class CellTestSupport<M extends Matrix<R>, R extends Row<C>, C extends Cell<E>, E> {

	private final Provider<OTUSet> otuSetProvider;
	private final Provider<OTU> otuProvider;
	private final Provider<R> rowProvider;
	private final Provider<C> cellProvider;

	@Inject
	CellTestSupport(final Provider<M> matrixProvider,
			final Provider<OTUSet> otuSetProvider,
			final Provider<OTU> otuProvider,
			final Provider<R> rowProvider,
			final Provider<C> cellProvider) {
		this.otuSetProvider = otuSetProvider;
		this.otuProvider = otuProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
	}


	public void unsetRow(final M matrix) {
		checkNotNull(matrix);
		checkArgument(matrix.getColumnVersionInfos().size() == 1,
				"matrix has " + matrix.getColumnVersionInfos().size()
						+ " column(s), but we need it to have 1 column");
		final C cell = cellProvider.get();
		final R row = rowProvider.get();
		final OTUSet otuSet = otuSetProvider.get();
		final OTU otu = otuSet.addOTU(otuProvider.get());
		matrix.setOTUSet(otuSet);
		matrix.putRow(otu, row);
		row.setCells(ImmutableList.of(cell));
		cell.unsetRow();
		assertNull(cell.getRow());
	}

}
