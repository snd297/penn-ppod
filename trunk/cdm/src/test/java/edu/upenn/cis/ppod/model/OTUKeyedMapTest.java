/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.util.TestVisitor;

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
	private Provider<DNARow> dnaRowProvider;

	@Inject
	private Provider<DNARows> dnaRowsProvider;

	@Test
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

		final DNARows otusToRows = matrix.getOTUKeyedRows();

		otusToRows.put(otu0, row0);
		otusToRows.put(otu1, row1);
		otusToRows.put(otu2, row2);

		final List<DNARow> dnaRows = otusToRows.getValuesInOTUSetOrder();

		assertEquals(dnaRows.get(0), row0);
		assertEquals(dnaRows.get(1), row1);
		assertEquals(dnaRows.get(2), row2);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void getValuesInOTUSetOrderWithNoParentSet() {

		final DNARows otusToRows = dnaRowsProvider.get();
		otusToRows.getValuesInOTUSetOrder();

	}

	@Test
	public void clear() {
		final DNAMatrix matrix = dnaMatrixProvider.get();
		final OTUSet otuSet = matrix.setOTUSet(otuSetProvider.get())
				.getOTUSet();

		final OTU otu0 = otuSet.addOTU(otuProvider.get().setLabel("otu0"));
		final OTU otu1 = otuSet.addOTU(otuProvider.get().setLabel("otu1"));
		final OTU otu2 = otuSet.addOTU(otuProvider.get().setLabel("otu2"));

		final DNARow row0 = dnaRowProvider.get();
		final DNARow row1 = dnaRowProvider.get();
		final DNARow row2 = dnaRowProvider.get();

		final DNARows rows = dnaRowsProvider.get();
		rows.setMatrix(matrix);

		rows.put(otu0, row0);
		rows.put(otu1, row1);
		rows.put(otu2, row2);

		matrix.unsetInNeedOfNewVersion();

		rows.clear();

		assertEquals(rows.getOTUsToValues().size(), 0);
		assertTrue(matrix.isInNeedOfNewVersion());

	}

	/**
	 * If it's empty and we call clear, that should have no affect on
	 * {@link Matrix#isInNeedOfNewPPodVersionInfo()}
	 */
	@Test
	public void clearWhileEmpty() {
		final DNAMatrix matrix = dnaMatrixProvider.get();
		final DNARows otusToRows = matrix.getOTUKeyedRows();
		otusToRows.setMatrix(matrix);
		matrix.unsetInNeedOfNewVersion();
		otusToRows.clear();

		assertFalse(matrix.isInNeedOfNewVersion());
	}

	@Inject
	private Provider<TestVisitor> testVisitorProvider;

	@Test
	public void accept() {
		final DNAMatrix matrix = dnaMatrixProvider.get();

		final OTUSet otuSet = otuSetProvider.get();
		otuSet.addDNAMatrix(matrix);

		final OTU otu0 = otuSet.addOTU(otuProvider.get().setLabel("otu0"));
		final OTU otu1 = otuSet.addOTU(otuProvider.get().setLabel("otu1"));
		final OTU otu2 = otuSet.addOTU(otuProvider.get().setLabel("otu2"));

		final DNARow row0 = dnaRowProvider.get();
		final DNARow row2 = dnaRowProvider.get();

		matrix.putRow(otu0, row0);
		matrix.getOTUKeyedRows().getOTUsToValues().put(otu1, null); // the
		// accept
		// method
		// should
		// handle
		// null
		// values, but we need to sneak around the matrix to get it in there
		matrix.putRow(otu2, row2);

		final TestVisitor visitor = testVisitorProvider.get();

		matrix.getOTUKeyedRows().accept(visitor);

		// Size should be the same as all of the rows minus the null row and
		// plus the
		// OTUKeyedMap
		assertEquals(visitor.getVisited().size(),
				matrix.getRows().values().size() - 1 + 1);

		assertTrue(visitor.getVisited().contains(matrix.getOTUKeyedRows()));
		assertTrue(visitor
				.getVisited()
				.containsAll(ImmutableList
						.of(row0, row2)));
	}
}
