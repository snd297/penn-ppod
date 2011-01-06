package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

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
	}
}
