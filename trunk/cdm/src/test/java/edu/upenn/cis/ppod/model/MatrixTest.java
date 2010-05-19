package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class MatrixTest {

	@Inject
	private Provider<DNAMatrix> dnaMatrixProvider;

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	@Test
	public void setColumnPPodVersionInfo() {
		final DNAMatrix matrix = dnaMatrixProvider.get();
		final PPodVersionInfo pPodVersionInfo = pPodVersionInfoProvider.get();
		matrix.setColumnPPodVersionInfos(pPodVersionInfo);
		matrix.setColumnsSize(1);
		matrix.setColumnPPodVersionInfo(0, pPodVersionInfo);
		assertSame(matrix.getColumnPPodVersionInfos().get(0), pPodVersionInfo);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setColumnPPodVersionInfosWTooSmallPos() {
		final Matrix<?> matrix = dnaMatrixProvider.get();
		final PPodVersionInfo pPodVersionInfo = pPodVersionInfoProvider.get();
		matrix.setColumnPPodVersionInfo(0, pPodVersionInfo);
	}
}
