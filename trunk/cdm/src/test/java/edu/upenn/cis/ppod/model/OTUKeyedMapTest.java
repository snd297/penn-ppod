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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class OTUKeyedMapTest {

	@Test
	public void clear() {
		final DnaMatrix matrix = new DnaMatrix();
		final OtuSet otuSet = new OtuSet();
		matrix.setParent(otuSet);

		final Otu otu0 = new Otu();
		otu0.setLabel("otu0");

		final Otu otu1 = new Otu();
		otu1.setLabel("otu1");

		final Otu otu2 = new Otu();
		otu2.setLabel("otu2");

		otuSet.addOTU(otu0);
		otuSet.addOTU(otu1);
		otuSet.addOTU(otu2);

		final DnaRow row0 = new DnaRow();
		final DnaRow row1 = new DnaRow();
		final DnaRow row2 = new DnaRow();

		final DnaRows rows = new DnaRows();
		rows.setParent(matrix);

		rows.put(otu0, row0);
		rows.put(otu1, row1);
		rows.put(otu2, row2);

		matrix.unsetInNeedOfNewVersion();

		rows.clear();

		assertEquals(rows.getValues().size(), 0);
		assertTrue(matrix.isInNeedOfNewVersion());

	}

	/**
	 * If it's empty and we call clear, that should have no affect on
	 * {@link Matrix#isInNeedOfNewPPodVersionInfo()}
	 */
	@Test
	public void clearWhileEmpty() {
		final DnaMatrix matrix = new DnaMatrix();
		final DnaRows otusToRows = matrix.getOTUKeyedRows();
		otusToRows.setParent(matrix);
		matrix.unsetInNeedOfNewVersion();
		otusToRows.clear();

		assertFalse(matrix.isInNeedOfNewVersion());
	}

	@Test
	public void accept() {
		final DnaMatrix matrix = new DnaMatrix();

		final OtuSet otuSet = new OtuSet();
		otuSet.addDnaMatrix(matrix);

		final Otu otu0 = new Otu();
		otu0.setLabel("otu0");

		final Otu otu1 = new Otu();
		otu1.setLabel("otu1");

		final Otu otu2 = new Otu();
		otu2.setLabel("otu2");

		otuSet.addOTU(otu0);
		otuSet.addOTU(otu1);
		otuSet.addOTU(otu2);

		final DnaRow row0 = new DnaRow();
		final DnaRow row2 = new DnaRow();

		matrix.putRow(otu0, row0);
		matrix.getOTUKeyedRows().getValues().put(otu1, null); // the
		// accept
		// method
		// should
		// handle
		// null
		// values, but we need to sneak around the matrix to get it in there
		matrix.putRow(otu2, row2);

		final IVisitor visitor = mock(IVisitor.class);

		matrix.getOTUKeyedRows().accept(visitor);

		// Size should be the same as all of the rows minus the null row
		verify(visitor, times(matrix.getRows().size() - 1))
				.visitDNARow(any(DnaRow.class));

		verify(visitor).visitDNARow(row0);
		verify(visitor).visitDNARow(row2);

	}
}
