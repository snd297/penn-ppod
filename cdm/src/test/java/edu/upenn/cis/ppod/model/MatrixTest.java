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
	private Provider<VersionInfo> pPodVersionInfoProvider;

	@Test
	public void setColumnPPodVersionInfo() {
		final DNAMatrix matrix = dnaMatrixProvider.get();
		final VersionInfo versionInfo = pPodVersionInfoProvider.get();
		matrix.setColumnsSize(1);
		matrix.setColumnVersionInfos(versionInfo);
		matrix.setColumnVersionInfo(0, versionInfo);
		assertSame(matrix.getColumnVersionInfos().get(0), versionInfo);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setColumnPPodVersionInfosWTooSmallPos() {
		final Matrix<?> matrix = dnaMatrixProvider.get();
		final VersionInfo versionInfo = pPodVersionInfoProvider.get();
		matrix.setColumnVersionInfo(0, versionInfo);
	}
}
