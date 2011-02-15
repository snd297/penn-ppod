package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.frequency;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST)
public class ProteinRowTest {

	@Test
	public void addCell() {
		final ProteinRow row = new ProteinRow();

		final ProteinCell cell = new ProteinCell();
		row.addCell(cell);

		assertTrue(row.getCells().contains(cell));
		assertEquals(frequency(row.getCells(), cell), 1);
		assertEquals(cell.getParent(), row);
		assertEquals(cell.getPosition().intValue(), 0);

	}
}
