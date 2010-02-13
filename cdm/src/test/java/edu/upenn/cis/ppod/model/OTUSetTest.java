package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 * 
 */
@Test(groups = TestGroupDefs.INIT)
public class OTUSetTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	private OTUSet otuSet;

	private List<OTU> otus;

	@Inject
	private Provider<Study> studyProvider;

	@Inject
	private Provider<TreeSet> treeSetProvider;

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	@Inject
	private Provider<CharacterStateMatrix> matrixProvider;

	private Study study;

	@BeforeMethod
	public void beforeMethod() {
		otuSet = otuSetProvider.get();
		otus = newArrayList();
		otus.add(otuSet.addOTU((OTU) otuProvider.get().setLabel("otu0")
				.setPPodId()));
		otus.add(otuSet.addOTU((OTU) otuProvider.get().setLabel("otu1")
				.setPPodId()));
		otus.add(otuSet.addOTU((OTU) otuProvider.get().setLabel("otu2")
				.setPPodId()));

		// Do this so we can check that version resets are being done.
		study = studyProvider.get();
		study.addOTUSet(otuSet);
	}

	public void getOTUByPPodId() {
		assertEquals(otuSet.getOTUByPPodId(otus.get(1).getPPodId()), otus
				.get(1));
	}

	public void getOTUByNullPPodId() {
		assertNull(otuSet.getOTUByPPodId(null));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addOTUWDuplicateLabel() {
		otuSet.addOTU(otuProvider.get().setLabel(otus.get(0).getLabel()));
	}

	public void addTreeSet() {
		final TreeSet treeSet = treeSetProvider.get();
		otuSet.addTreeSet(treeSet);
		assertEquals(getOnlyElement(otuSet.getTreeSets()), treeSet);
	}

	/**
	 * Call {@code OTUSet.resetPPodVersionInfo()} when {@code
	 * OTUSet.getPPodVersionInfo() == null} and make sure that
	 * {@link OTUSet#getPPodVersionInfo()} stays {@code null} and that the OTU
	 * sets's {@code Study}'s pPOD Version info is unaffected.
	 */
	public void resetPPodVersionInfoWNullVersion() {
		final PPodVersionInfo studyPPodVersionInfo = study.getPPodVersionInfo();
		otuSet.setPPodVersionInfo(null);
		otuSet.resetPPodVersionInfo();
		assertNull(otuSet.getPPodVersionInfo());
		assertEquals(study.getPPodVersionInfo(), studyPPodVersionInfo);
	}

	/**
	 * Add an otu that's already in an otu set into an otu set. The pPOD version
	 * should not be reset when this happens.
	 */
	public void addOTUWAlreadyContainedOTU() {
		final OTU otu = otuSet.addOTU(otuProvider.get().setLabel("OTU-1"));
		assertNull(otuSet.getPPodVersionInfo());
		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());
		assertNotNull(otuSet.getPPodVersionInfo());
		otuSet.addOTU(otu);
		assertNotNull(otuSet.getPPodVersionInfo());
	}

	/**
	 * Remove an otu set and make sure it was removed and the pPOD version
	 * reset.
	 */
	public void removeOTU() {
		for (final OTU otu : otus) {
			otuSet.addOTU(otu);
		}
		final PPodVersionInfo pPodVersionInfo = pPodVersionInfoProvider.get();
		otuSet.setPPodVersionInfo(pPodVersionInfo);
		otuSet.removeOTU(otus.get(1));
		assertFalse(otuSet.getOTUs().contains(otus.get(1)));
		assertNull(otuSet.getPPodVersionInfo());
	}

	/**
	 * Remove an OTU that is not in an otuset. This should cause no change to
	 * the pPOD version info.
	 */
	public void removeOTUThatIsNotThere() {
		for (OTU otu : otus) {
			otuSet.addOTU(otu);
		}
		final PPodVersionInfo pPodVersionInfo = pPodVersionInfoProvider.get();
		otuSet.setPPodVersionInfo(pPodVersionInfo);
		otuSet.removeOTU(otuProvider.get());

		assertEquals((Object) otuSet.getOTUs(), (Object) newHashSet(otus));

		assertEquals(otuSet.getPPodVersionInfo(), pPodVersionInfo);
	}

	/**
	 * Straight test of clear.
	 */
	public void clearOTUs() {
		for (final OTU otu : otus) {
			otuSet.addOTU(otu);
		}
		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());
		study.setPPodVersionInfo(pPodVersionInfoProvider.get());
		otuSet.clearOTUs();
		assertNull(otuSet.getPPodVersionInfo());
		assertNull(study.getPPodVersionInfo());
		assertEquals(otuSet.getOTUs().size(), 0);
	}

	public void removeMatrix() {
		final CharacterStateMatrix matrix0 = matrixProvider.get();
		final CharacterStateMatrix matrix1 = matrixProvider.get();
		final CharacterStateMatrix matrix2 = matrixProvider.get();

		final Set<CharacterStateMatrix> matrices = newHashSet(matrix0, matrix2);

		otuSet.addMatrix(matrix0);
		otuSet.addMatrix(matrix1);
		otuSet.addMatrix(matrix2);
		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());

		study.setPPodVersionInfo(pPodVersionInfoProvider.get());

		otuSet.removeMatrix(matrix1);

		assertNull(study.getPPodVersionInfo());
		assertNull(otuSet.getPPodVersionInfo());
		matrices.remove(matrix1);
		assertEquals((Object) otuSet.getMatrices(), (Object) matrices);
	}

	public void removeTreeSet() {
		final TreeSet treeSet0 = treeSetProvider.get();
		final TreeSet treeSet1 = treeSetProvider.get();
		final TreeSet treeSet2 = treeSetProvider.get();

		otuSet.addTreeSet(treeSet0);
		otuSet.addTreeSet(treeSet1);
		otuSet.addTreeSet(treeSet2);

		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());

		final boolean removeTreeSetReturnValue = otuSet.removeTreeSet(treeSet1);

		assertTrue(removeTreeSetReturnValue);

		assertNull(otuSet.getPPodVersionInfo());

		assertEquals(otuSet.getTreeSets(), newHashSet(treeSet0, treeSet2));

		otuSet.removeTreeSet(treeSet0);
		otuSet.removeTreeSet(treeSet2);
		boolean removeTreeSetValueShouldBeFalse = otuSet.removeTreeSet(treeSet2);

		assertFalse(removeTreeSetValueShouldBeFalse);

		assertEquals(otuSet.getTreeSets(), Collections.emptySet());
	}
}
