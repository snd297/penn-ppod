package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.List;

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
		final Study study = studyProvider.get();
		study.addOTUSet(otuSet);
		final PPodVersionInfo studyPPodVersionInfo = study.getPPodVersionInfo();
		otuSet.resetPPodVersionInfo();
		assertNull(otuSet.getPPodVersionInfo());
		assertEquals(study.getPPodVersionInfo(), studyPPodVersionInfo);
	}

	public void setDescriptionToNull() {
		otuSet.setDescription(null);
		assertNull(otuSet.getDescription());
	}

	public void setDescription() {
		otuSet.setDescription("TEST DESCRIPTION");
		assertEquals(otuSet.getDescription(), "TEST DESCRIPTION");
	}

	public void setDescriptionIfAlreadySetToNotNullValue() {
		otuSet.setDescription("TEST DESCRIPTION");
	}
}
