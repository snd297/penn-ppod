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
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class ChromatogramTest {

	@Inject
	private Provider<Chromatogram> chromatogramProvider;

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
	public void setBytesValue() {

		final Chromatogram chromatogram = chromatogramProvider.get();

		assertNull(chromatogram.getChromatogram());

		final byte[] bytes = new byte[] { 1, 3, 5 };

		final Chromatogram chromatogramReturned = chromatogram
				.setChromatogram(bytes);
		assertSame(chromatogramReturned, chromatogram);

		assertEquals(chromatogram.getChromatogram(), bytes);

		assertTrue(chromatogram.isInNeedOfNewPPodVersionInfo());

		chromatogram.unsetInNeedOfNewPPodVersionInfo();

		chromatogram.setChromatogram(bytes);

		assertFalse(chromatogram.isInNeedOfNewPPodVersionInfo());

		chromatogram.unsetInNeedOfNewPPodVersionInfo();

		final byte[] bytes2 = new byte[] { 3, 5, 3 };

		chromatogram.setChromatogram(bytes2);

		assertEquals(chromatogram.getChromatogram(), bytes2);

	}
}
