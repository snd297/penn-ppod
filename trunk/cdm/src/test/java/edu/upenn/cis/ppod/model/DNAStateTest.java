package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class DNAStateTest {

	/**
	 * Make sure that the state numbers that Mesquite gives us map onto the
	 * correct nucleotides.
	 */
	public void of() {
		assertEquals(DNAState.Nucleotide.of(0), DNAState.Nucleotide.A);
		assertEquals(DNAState.Nucleotide.of(1), DNAState.Nucleotide.C);
		assertEquals(DNAState.Nucleotide.of(2), DNAState.Nucleotide.G);
		assertEquals(DNAState.Nucleotide.of(3), DNAState.Nucleotide.T);
	}
}
