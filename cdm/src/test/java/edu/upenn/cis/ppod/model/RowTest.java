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
		final StandardMatrix matrix = new StandardMatrix();
		otuSet.addStandardMatrix(matrix);
		// matrix.setColumnsSize(3);
		matrix.clearAndAddCharacters(newArrayList(new StandardCharacter(),
				new StandardCharacter(), new StandardCharacter()));

		final StandardRow row = new StandardRow();
		matrix.putRow(otu, row);
		row.clearAndAddCells(newArrayList(new StandardCell(), new StandardCell(),
				new StandardCell()));
		row.clearCells();

		assertTrue(isEmpty(row.getCells()));

		for (final StandardCell cell : row.getCells()) {
			assertNull(cell.getParent());
			assertNull(cell.getPosition());
		}
	}
}
