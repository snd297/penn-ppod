package edu.upenn.cis.ppod.model;

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
}
