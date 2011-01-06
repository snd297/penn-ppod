package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.newHashMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.Map;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.util.OtuDnaRowPair;

/**
 * Test {@link DnaRows}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class DnaRowsTest {
	@Test
	public void beforeMarshal() {
		final DnaRows rows = new DnaRows();
		final Otu otu0 = new Otu();
		final DnaRow dnaRow0 = new DnaRow();

		final Map<Otu, DnaRow> otusToRows = newHashMap();
		otusToRows.put(otu0, dnaRow0);
		rows.setValues(otusToRows);

		rows.beforeMarshal(null);

		assertEquals(rows.getOtuKeyedPairs().size(), 1);
		final OtuDnaRowPair actualOtuRowPair = getOnlyElement(rows
				.getOtuKeyedPairs());
		assertSame(actualOtuRowPair.getFirst(), otu0);
		assertSame(actualOtuRowPair.getSecond(), dnaRow0);
	}
}
