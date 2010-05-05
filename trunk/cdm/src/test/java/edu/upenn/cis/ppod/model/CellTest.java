package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Methods for testing {@link Cell}.
 * 
 * @author Sam Donnelly
 */
public class CellTest<R extends Row<C>, C extends Cell<E>, E> {

	private final Provider<OTUSet> otuSetProvider;
	private final Provider<OTU> otuProvider;

	@Inject
	CellTest(final Provider<OTUSet> otuSetProvider,
			final Provider<OTU> otuProvider) {
		this.otuSetProvider = otuSetProvider;
		this.otuProvider = otuProvider;
	}

	public void getStatesWhenCellHasOneState(
			final Matrix<R> matrix,
			final C cell,
			final E element) {

		checkNotNull(matrix);
		checkNotNull(cell);
		checkNotNull(element);

		final OTUSet otuSet = otuSetProvider.get();

		matrix.setOTUSet(otuSet);

		otuSet.addOTU(otuProvider.get());

		final Set<E> elements = newHashSet();
		elements.add(element);

		final List<C> cells = newArrayList();
		cells.add(cell);

		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(cells);

		cell.setSingleElement(element);
		assertEquals((Object) cell.getElements(), (Object) elements);
	}

	/**
	 * If a cell does not belong to a row, it is illegal to add states to it and
	 * should throw an {@code IllegalStateException}.
	 */
	public void setSingleElement(final C cell, final E element) {
		checkNotNull(cell);
		checkNotNull(element);
		cell.setSingleElement(element);
	}
}
