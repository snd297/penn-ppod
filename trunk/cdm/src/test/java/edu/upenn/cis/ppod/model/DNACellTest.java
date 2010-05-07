package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link DNACell}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class DNACellTest {

	@Inject
	private CellTest<DNAMatrix2, DNARow, DNACell, DNANucleotide> cellTest;

	@Inject
	private Provider<DNAMatrix2> dnaMatrix2Provider;

	@Inject
	private Provider<DNACell> dnaCellProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<DNARow> dnaRowProvider;

	public void getStatesWhenCellHasOneState() {
		final DNAMatrix2 matrix = dnaMatrix2Provider.get();
		matrix.setColumnsSize(1);
		// nothing special about A,C,T.
		cellTest.getStatesWhenCellHasMultipleElements(matrix,
				ImmutableSet.of(
						DNANucleotide.A, DNANucleotide.C,
						DNANucleotide.T));
	}

	public void afterUnmarshal() {
		final DNAMatrix2 matrix = dnaMatrix2Provider.get();
		matrix.setColumnsSize(1);

		// nothing special about A,C,T.
		cellTest.afterUnmarshal(matrix, ImmutableSet.of(
				DNANucleotide.A, DNANucleotide.C,
				DNANucleotide.T));
	}

	/**
	 * {@code beforeMarshal(...)} should throw an exception if the type has not
	 * bee set yet.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void beforeMarshalBeforeTypeHasBeenSet() {
		cellTest.beforeMarshalBeforeTypeHasBeenSet();
	}

	/**
	 * Straight {@link Cell#beforeMarshal(javax.xml.bind.Marshaller)} test.
	 * Makes sure that {@link Cell#getXmlElements()} contains the elements after
	 * {@code beforeMarshal()} is called.
	 */
	public void beforeMarshal() {
		final DNAMatrix2 matrix = dnaMatrix2Provider.get();
		final DNACell cell = dnaCellProvider.get();

		final OTUSet otuSet = otuSetProvider.get();
		otuSet.addOTU(otuProvider.get());

		matrix.setOTUSet(otuSet);

		matrix.putRow(otuSet.getOTU(0), dnaRowProvider.get());

		matrix.getRow(matrix.getOTUSet().getOTU(0)).setCells(
				ImmutableList.of(cell));

		final Set<DNANucleotide> elements = ImmutableSet.of(DNANucleotide.A,
				DNANucleotide.C);

		cell.setPolymorphic(elements);

		cell.beforeMarshal(null);
		final Set<DNANucleotide> xmlStates = cell.getXmlElements();
		assertEquals(xmlStates.size(), elements.size());
		for (final DNANucleotide expectedElement : elements) {
			assertTrue(xmlStates.contains(expectedElement));
		}
	}
}
