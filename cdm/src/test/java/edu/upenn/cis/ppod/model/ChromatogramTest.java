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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * {@link Chromatogram} tests.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST },
		dependsOnGroups = TestGroupDefs.INIT)
public class ChromatogramTest {

	@Inject
	private Provider<Chromatogram> chromatogramProvider;

	@Inject
	private Provider<DNASequence> dnaSequenceProvider;

	/**
	 * Run {@link Chromatogram#setChromatogram(byte[])} and
	 * {@link Chromatogram#getChromatogram()} through its paces:
	 * <ol>
	 * <li>straight set and verify in need of new pPOD version</li>
	 * <li>set w/ already-value and make sure its not in need of a new pPOD
	 * version</li>
	 * <li>set w/ a new byte array that has the same length as the one it
	 * already has. Because the attachment will reuse the same byte array in
	 * this case and its another branch</li>
	 * </ol>
	 */
	@Test
	public void setBytesValue() {

		final Chromatogram chromatogram = chromatogramProvider.get();

		assertNull(chromatogram.getChromatogram());

		final byte[] bytes = new byte[] { 1, 3, 5 };

		final Chromatogram chromatogramReturned = chromatogram
				.setChromatogram(bytes);
		assertSame(chromatogramReturned, chromatogram);

		assertEquals(chromatogram.getChromatogram(), bytes);

		assertTrue(chromatogram.isInNeedOfNewVersion());

		chromatogram.unsetInNeedOfNewVersion();

		chromatogram.setChromatogram(bytes);

		assertFalse(chromatogram.isInNeedOfNewVersion());

		chromatogram.unsetInNeedOfNewVersion();

		final byte[] bytes2 = new byte[] { 3, 5, 3 };

		chromatogram.setChromatogram(bytes2);

		assertEquals(chromatogram.getChromatogram(), bytes2);

	}

	@Test
	public void setSequence() {
		final Chromatogram chromatogram = chromatogramProvider.get();
		final DNASequence sequence = dnaSequenceProvider.get();

		final Chromatogram returnedChromatogram =
				chromatogram.setSequence(sequence);
		assertSame(returnedChromatogram, chromatogram);
		assertSame(chromatogram.getSequence(), sequence);
		assertTrue(chromatogram.isInNeedOfNewVersion());

		chromatogram.unsetInNeedOfNewVersion();
		chromatogram.setSequence(sequence);
		assertEquals(chromatogram.getSequence(), sequence);
		assertFalse(chromatogram.isInNeedOfNewVersion());
	}
}
