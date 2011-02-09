package edu.upenn.cis.ppod.util;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodCellType;
import edu.upenn.cis.ppod.model.ProteinCell;

@Test(groups = TestGroupDefs.FAST)
public class ProteinDocCell2DbCellTest {
	@Test
	public void docCell2DbCell() {

		final ProteinCell cell = new ProteinCell();

		ProteinDocCell2DbCell.docCell2DbCell(cell, PPodCellType.SINGLE, "A");
	}
}
