package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST)
public class RowTest {
	@Test
	void clearCells() {
		final OtuSet otuSet = new OtuSet();
		final Otu otu = new Otu();
		otuSet.addOtu(otu);
		final DnaMatrix matrix = new DnaMatrix();
		otuSet.addDnaMatrix(matrix);
		matrix.setColumnsSize(3);
		final DnaRow row = new DnaRow();
		matrix.putRow(otu, row);
		row.setCells(newArrayList(new DnaCell(), new DnaCell(), new DnaCell()));
		row.clearCells();

		assertTrue(isEmpty(row.getCells()));

		for (final DnaCell cell : row.getCells()) {
			assertNull(cell.getParent());
			assertNull(cell.getPosition());
		}
	}
}
