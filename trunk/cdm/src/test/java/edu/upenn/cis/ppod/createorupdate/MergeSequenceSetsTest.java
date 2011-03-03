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
package edu.upenn.cis.ppod.createorupdate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodDnaSequence;
import edu.upenn.cis.ppod.dto.PPodDnaSequenceSet;
import edu.upenn.cis.ppod.dto.PPodOtu;
import edu.upenn.cis.ppod.dto.PPodOtuSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.VersionInfo;

@Test(groups = TestGroupDefs.FAST)
public class MergeSequenceSetsTest {

	@Test
	public void modifySequencesKeepLength() {

		final PPodDnaSequenceSet srcSeqSet = new PPodDnaSequenceSet(
				"src-seq-set-0");

		final DnaSequenceSet trgSeqSet = new DnaSequenceSet();

		final OtuSet trgOTUSet = new OtuSet();
		trgOTUSet.addOtu(new Otu("otu-0"));
		trgOTUSet.addOtu(new Otu("otu-1"));
		trgOTUSet.addOtu(new Otu("otu-2"));
		trgOTUSet.addDnaSequenceSet(trgSeqSet);

		final PPodOtuSet srcOTUSet = new PPodOtuSet("otu-set-0");
		srcOTUSet.getOtus().add(new PPodOtu("otu-0"));
		srcOTUSet.getOtus().add(new PPodOtu("otu-1"));
		srcOTUSet.getOtus().add(new PPodOtu("otu-2"));

		srcOTUSet.getDnaSequenceSets().add(srcSeqSet);

		final PPodDnaSequence srcSeq0 =
					new PPodDnaSequence("ACT", "jkjkj", "jkejfke", "jkfjeijf");
		final PPodDnaSequence srcSeq1 =
				new PPodDnaSequence("TCA", "jjijk", "jefeji", "ejfiejiji");
		final PPodDnaSequence srcSeq2 =
						new PPodDnaSequence("ATC", "jfje", "jfifjiji",
								"fjijeifji");

		srcSeqSet.getSequences().add(srcSeq0);
		srcSeqSet.getSequences().add(srcSeq1);
		srcSeqSet.getSequences().add(srcSeq2);

		final VersionInfo versionInfo = mock(VersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final MergeDnaSequenceSets mergeSeqSets = new MergeDnaSequenceSets();

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		srcSeq0.setSequence("ACC");
		srcSeq1.setSequence("TCT");
		srcSeq2.setSequence("ATA");

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);
	}

	@Test
	public void shortenSequences() {

		final PPodDnaSequenceSet srcSeqSet =
				new PPodDnaSequenceSet("src-seq-set-0");

		final DnaSequenceSet trgSeqSet = new DnaSequenceSet();

		final OtuSet trgOTUSet = new OtuSet();
		trgOTUSet.addOtu(new Otu("otu-0"));
		trgOTUSet.addOtu(new Otu("otu-1"));
		trgOTUSet.addOtu(new Otu("otu-2"));
		trgOTUSet.addDnaSequenceSet(trgSeqSet);

		final PPodOtuSet srcOTUSet = new PPodOtuSet("otu-set-0");
		srcOTUSet.getOtus().add(new PPodOtu("otu-0"));
		srcOTUSet.getOtus().add(new PPodOtu("otu-1"));
		srcOTUSet.getOtus().add(new PPodOtu("otu-2"));

		srcOTUSet.getDnaSequenceSets().add(srcSeqSet);

		final PPodDnaSequence srcSeq0 =
				new PPodDnaSequence("ACT", "jkjkj", "jkejfke", "jkfjeijf");
		final PPodDnaSequence srcSeq1 =
				new PPodDnaSequence("TCA", "jjijk", "jefeji", "ejfiejiji");
		final PPodDnaSequence srcSeq2 =
				new PPodDnaSequence("ATC", "jfje", "jfifjiji",
						"fjijeifji");

		srcSeqSet.getSequences().add(srcSeq0);
		srcSeqSet.getSequences().add(srcSeq1);
		srcSeqSet.getSequences().add(srcSeq2);

		final VersionInfo versionInfo = mock(VersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final MergeDnaSequenceSets mergeSeqSets = new MergeDnaSequenceSets();

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		srcSeq0.setSequence("AC");
		srcSeq1.setSequence("TC");
		srcSeq2.setSequence("AT");

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);
	}

	@Test
	public void mergeOnBlankTarget() {
		final PPodDnaSequenceSet srcSeqSet =
				new PPodDnaSequenceSet("src-seq-set-0");

		final DnaSequenceSet trgSeqSet = new DnaSequenceSet();

		final OtuSet trgOTUSet = new OtuSet();
		trgOTUSet.addOtu(new Otu("otu-0"));
		trgOTUSet.addOtu(new Otu("otu-1"));
		trgOTUSet.addOtu(new Otu("otu-2"));
		trgOTUSet.addDnaSequenceSet(trgSeqSet);

		final PPodOtuSet srcOTUSet = new PPodOtuSet("otu-set-0");
		srcOTUSet.getOtus().add(new PPodOtu("otu-0"));
		srcOTUSet.getOtus().add(new PPodOtu("otu-1"));
		srcOTUSet.getOtus().add(new PPodOtu("otu-2"));

		srcOTUSet.getDnaSequenceSets().add(srcSeqSet);

		final PPodDnaSequence srcSeq0 =
				new PPodDnaSequence("ACT", "jkjkj", "jkejfke", "jkfjeijf");
		final PPodDnaSequence srcSeq1 =
				new PPodDnaSequence("TCA", "jjijk", "jefeji", "ejfiejiji");
		final PPodDnaSequence srcSeq2 =
				new PPodDnaSequence("ATC", "jfje", "jfifjiji",
						"fjijeifji");

		srcSeqSet.getSequences().add(srcSeq0);
		srcSeqSet.getSequences().add(srcSeq1);
		srcSeqSet.getSequences().add(srcSeq2);

		final VersionInfo versionInfo = mock(VersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final MergeDnaSequenceSets mergeSeqSets = new MergeDnaSequenceSets();

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);

	}
}
