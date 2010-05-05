package edu.upenn.cis.ppod.model;

import org.testng.annotations.Test;

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

	public void getStatesWhenCellHasOneState() {

		// nothing special about A,C,T.
		cellTest.getStatesWhenCellHasMultipleElements(dnaMatrix2Provider.get(),
				ImmutableSet.of(
						DNANucleotide.A, DNANucleotide.C,
						DNANucleotide.T));
	}

}
