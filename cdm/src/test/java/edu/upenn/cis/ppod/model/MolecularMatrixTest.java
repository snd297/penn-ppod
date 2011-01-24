package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST)
public class MolecularMatrixTest {
	@Test
	public void setColumnSize() {
		final DnaMatrix matrix = new DnaMatrix();
		matrix.setColumnsSize(2);
		assertEquals(matrix.getColumnsSize().intValue(), 2);

		matrix.setColumnsSize(1);
		assertEquals(matrix.getColumnsSize().intValue(), 1);
	}
	
}
