package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class OTUKeyedMapTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<DNAMatrix> dnaMatrixProvider;

	@Inject
	private Provider<OTUsToDNARows> otusToDNARowsProvider;

	@Inject
	private Provider<DNARow> dnaRowProvider;

	public void getValuesInOTUSetOrder() {

		final DNAMatrix matrix = dnaMatrixProvider.get();
		final OTUSet otuSet = matrix.setOTUSet(otuSetProvider.get())
				.getOTUSet();

		final OTU otu0 = otuSet.addOTU(otuProvider.get().setLabel("otu0"));
		final OTU otu1 = otuSet.addOTU(otuProvider.get().setLabel("otu1"));
		final OTU otu2 = otuSet.addOTU(otuProvider.get().setLabel("otu2"));

		final DNARow row0 = dnaRowProvider.get();
		final DNARow row1 = dnaRowProvider.get();
		final DNARow row2 = dnaRowProvider.get();

		final OTUsToDNARows otusToRows = otusToDNARowsProvider.get();
		otusToRows.setMatrix(matrix);

		otusToRows.put(otu0, row0);
		otusToRows.put(otu1, row1);
		otusToRows.put(otu2, row2);

		final List<DNARow> dnaRows = otusToRows.getValuesInOTUSetOrder();

		assertEquals(dnaRows.get(0), row0);
		assertEquals(dnaRows.get(1), row1);
		assertEquals(dnaRows.get(2), row2);
	}

	@Test(groups = TestGroupDefs.SINGLE, expectedExceptions = IllegalStateException.class)
	public void getValuesInOTUSetOrderWithNoParentSet() {

		final OTUsToDNARows otusToRows = otusToDNARowsProvider.get();
		otusToRows.getValuesInOTUSetOrder();

	}
}
