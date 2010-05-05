package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Methods for testing {@link Cell}.
 * 
 * @author Sam Donnelly
 */
public class CellTest<M extends Matrix<R>, R extends Row<C>, C extends Cell<E>, E> {

	private final Provider<M> matrixProvider;
	private final Provider<OTUSet> otuSetProvider;
	private final Provider<OTU> otuProvider;
	private final Provider<R> rowProvider;
	private final Provider<C> cellProvider;

	@Inject
	CellTest(final Provider<M> matrixProvider,
			final Provider<OTUSet> otuSetProvider,
			final Provider<OTU> otuProvider,
			final Provider<R> rowProvider,
			final Provider<C> cellProvider) {
		this.matrixProvider = matrixProvider;
		this.otuSetProvider = otuSetProvider;
		this.otuProvider = otuProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
	}

	public void getStatesWhenCellHasMultipleElements(final M matrix,
			final Set<E> elements) {

		checkNotNull(elements);

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

	public void afterUnmarshal(final Set<E> elements) {

		final M matrix = matrixProvider.get();
		final C cell = cellProvider.get();
		cell.setXmlStatesNeedsToBePutIntoStates(true);

		final List<C> cells = newArrayList();
		cells.add(cell);
		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(cells);

		cell.setTypeAndXmlElements(CharacterStateCell.Type.UNCERTAIN, elements);
		cell.afterUnmarshal();
		assertEquals((Object) cell.getElements(), (Object) elements);
		assertFalse(cell.getXmlStatesNeedsToBePutIntoStates());
	}

}
