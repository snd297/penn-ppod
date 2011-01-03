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
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class OTUKeyedMapTest {

	@Test
	public void clear() {
		final DNAMatrix matrix = new DNAMatrix();
		final OTUSet otuSet = new OTUSet();
		matrix.setParent(otuSet);

		final IOtu otu0 = new OTU();
		otu0.setLabel("otu0");

		final IOtu otu1 = new OTU();
		otu1.setLabel("otu1");

		final IOtu otu2 = new OTU();
		otu2.setLabel("otu2");

		otuSet.addOTU(otu0);
		otuSet.addOTU(otu1);
		otuSet.addOTU(otu2);

		final IDNARow row0 = new DNARow();
		final IDNARow row1 = new DNARow();
		final IDNARow row2 = new DNARow();

		final DNARows rows = new DNARows();
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
		final DNAMatrix matrix = new DNAMatrix();
		final DNARows otusToRows = matrix.getOTUKeyedRows();
		otusToRows.setParent(matrix);
		matrix.unsetInNeedOfNewVersion();
		otusToRows.clear();

		assertFalse(matrix.isInNeedOfNewVersion());
	}

	@Test
	public void accept() {
		final DNAMatrix matrix = new DNAMatrix();

		final IOTUSet otuSet = new OTUSet();
		otuSet.addDNAMatrix(matrix);

		final IOtu otu0 = new OTU();
		otu0.setLabel("otu0");

		final IOtu otu1 = new OTU();
		otu1.setLabel("otu1");

		final IOtu otu2 = new OTU();
		otu2.setLabel("otu2");

		otuSet.addOTU(otu0);
		otuSet.addOTU(otu1);
		otuSet.addOTU(otu2);

		final DNARow row0 = new DNARow();
		final DNARow row2 = new DNARow();

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
				.visitDNARow(any(DNARow.class));

		verify(visitor).visitDNARow(row0);
		verify(visitor).visitDNARow(row2);

	}
}
