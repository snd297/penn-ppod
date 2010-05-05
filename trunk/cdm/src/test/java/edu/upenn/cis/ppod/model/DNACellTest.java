package edu.upenn.cis.ppod.model;

import org.testng.annotations.Test;

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
	private Provider<DNAMatrix2> dnaMatrix2Provider;

	@Inject
	private Provider<DNACell> dnaCellProvider;

	@Inject
	private CellTest<DNARow, DNACell, DNANucleotide> cellTest;

	public void getStatesWhenCellHasOneState() {
		final DNAMatrix2 matrix = dnaMatrix2Provider.get();
		final DNACell cell = dnaCellProvider.get();

		// nothing special about C
		cellTest.getStatesWhenCellHasOneState(matrix,
				cell, DNANucleotide.C);
	}

	public void setSingleElement() {
		final DNACell cell = dnaCellProvider.get();

		// nothing special about G
		cell.setSingleElement(DNANucleotide.G);
	}
}
