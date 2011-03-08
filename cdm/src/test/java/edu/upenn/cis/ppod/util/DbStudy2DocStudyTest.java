package edu.upenn.cis.ppod.util;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodOtu;
import edu.upenn.cis.ppod.model.Otu;

@Test(groups = TestGroupDefs.FAST)
public class DbStudy2DocStudyTest {

	@Test
	public void dbOtu2DocOtu() {
		Otu dbOtu = new Otu();
		dbOtu.setLabel("otu0");
		DbStudy2DocStudy dbStudy2DocStudy = new DbStudy2DocStudy();
		PPodOtu docOtu = dbStudy2DocStudy.dbOtu2DocOtu(dbOtu);
		assertEquals(docOtu.getLabel(), dbOtu.getLabel());
		assertEquals(docOtu.getPPodId(), dbOtu.getPPodId());
		assertEquals(docOtu.getDocId(), dbOtu.getPPodId());
	}
}
