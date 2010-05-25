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
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.List;
import java.util.Set;

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

	/**
	 * Matrix must be ready to have a row with one cell added to it.
	 * 
	 * @param matrix
	 * @param elements
	 */
	public void getStatesWhenCellHasMultipleElements(final M matrix,
			final Set<E> elements) {
		checkNotNull(matrix);
		checkNotNull(elements);
		checkArgument(matrix.getColumnVersionInfos().size() == 1,
				"matrix has " + matrix.getColumnVersionInfos()
						+ " column(s), but we need it to have 1 column");
		final OTUSet otuSet = otuSetProvider.get();

		matrix.setOTUSet(otuSet);

		final OTU otu = otuSet.addOTU(otuProvider.get());

		matrix.putRow(otu, rowProvider.get());

		final List<C> cells = newArrayList();
		final C cell = cellProvider.get();
		cells.add(cell);

		final OTUSet matrixOTUSet = matrix.getOTUSet();

		final OTU otu0 = matrixOTUSet.getOTU(0);

		final Row<C> row = matrix.getRow(otu0);
		row.setCells(cells);

		cell.setPolymorphicElements(elements);
		assertEquals((Object) cell.getElements(), (Object) elements);
	}

	/**
	 * If a cell does not belong to a row, it is illegal to add states to it and
	 * should throw an {@code IllegalStateException}.
	 */
	public void setStatesForACellThatDoesNotBelongToARow(final E element) {
		final C cell = cellProvider.get();
		cell.setSingleElement(element);
	}

	/**
	 * {@code beforeMarshal(...)} should throw an {@code IllegalStateException}
	 * if the type has not bee set yet.
	 */
	public void beforeMarshalBeforeTypeHasBeenSet() {
		final C cell = cellProvider.get();
		cell.beforeMarshal(null);
	}

	public void getStatesWhenCellHasOneElement(final M matrix,
			final E element) {
		checkNotNull(matrix);
		checkNotNull(element);
		checkArgument(matrix.getColumnVersionInfos().size() == 1,
				"matrix has " + matrix.getColumnVersionInfos().size()
						+ " column(s), but we need it to have 1 column");

		final OTUSet otuSet = otuSetProvider.get();

		matrix.setOTUSet(otuSet);

		final OTU otu = otuSet.addOTU(otuProvider.get());

		final R row = rowProvider.get();
		matrix.putRow(otu, row);

		final C cell = cellProvider.get();

		row.setCells(ImmutableList.of(cell));

		matrix.getRow(matrix.getOTUSet().getOTU(0));

		cell.setSingleElement(element);
		assertEquals(getOnlyElement(cell.getElements()), element);
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
