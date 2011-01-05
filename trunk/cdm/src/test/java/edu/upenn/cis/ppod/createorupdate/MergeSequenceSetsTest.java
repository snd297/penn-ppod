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
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.model.DnaSequence;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.VersionInfo;

@Test(groups = TestGroupDefs.FAST)
public class MergeSequenceSetsTest {

	@Test
	public void modifySequencesKeepLength() {

		final DnaSequenceSet srcSeqSet = new DnaSequenceSet();

		srcSeqSet.setLabel("src-seq-set-0");
		final DnaSequenceSet trgSeqSet = new DnaSequenceSet();

		final OtuSet trgOTUSet = new OtuSet();
		trgOTUSet.addOtu(new Otu("otu-0"));
		trgOTUSet.addOtu(new Otu("otu-1"));
		trgOTUSet.addOtu(new Otu("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final OtuSet srcOTUSet = new OtuSet();
		srcOTUSet.addOtu(new Otu("otu-0"));
		srcOTUSet.addOtu(new Otu("otu-1"));
		srcOTUSet.addOtu(new Otu("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final DnaSequence srcSeq0 =
					(DnaSequence) new DnaSequence()
							.setSequence("ACT")
																	.setAccession(
																			"jkjkj")
																	.setName(
																			"jkejfke")
																	.setDescription(
																			"jkfjeijf");
		final DnaSequence srcSeq1 =
					(DnaSequence) new DnaSequence()
							.setSequence("TCA")
							.setAccession("jjijk")
							.setName("jefeji")
							.setDescription("ejfiejiji");
		final DnaSequence srcSeq2 =
					(DnaSequence) new DnaSequence()
							.setSequence("ATC")
							.setAccession("jfje")
							.setName("jfifjiji")
							.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOtus().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(2), srcSeq2);

		final VersionInfo versionInfo = mock(VersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final IMergeDNASequenceSets mergeSeqSets = new MergeDnaSequenceSets(
				newVersionInfo);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		srcSeq0.setSequence("ACC");
		srcSeq1.setSequence("TCT");
		srcSeq2.setSequence("ATA");

		srcSeqSet.putSequence(srcOTUSet.getOtus().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(2), srcSeq2);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);
	}

	@Test
	public void shortenSequences() {

		final DnaSequenceSet srcSeqSet =
				new DnaSequenceSet();

		srcSeqSet.setLabel("src-seq-set-0");
		final DnaSequenceSet trgSeqSet = new DnaSequenceSet();

		final OtuSet trgOTUSet = new OtuSet();
		trgOTUSet.addOtu(new Otu("otu-0"));
		trgOTUSet.addOtu(new Otu("otu-1"));
		trgOTUSet.addOtu(new Otu("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final OtuSet srcOTUSet = new OtuSet();
		srcOTUSet.addOtu(new Otu("otu-0"));
		srcOTUSet.addOtu(new Otu("otu-1"));
		srcOTUSet.addOtu(new Otu("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final DnaSequence srcSeq0 =
				(DnaSequence) new DnaSequence()
							.setSequence("ACT")
							.setAccession("jkjkj")
							.setName("jkejfke")
							.setDescription("jkfjeijf");
		final DnaSequence srcSeq1 =
				(DnaSequence) new DnaSequence()
							.setSequence("TCA")
							.setAccession("jjijk")
							.setName("jefeji")
							.setDescription("ejfiejiji");
		final DnaSequence srcSeq2 =
				(DnaSequence) new DnaSequence()
							.setSequence("ATC")
							.setAccession("jfje")
							.setName("jfifjiji")
							.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOtus().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(2), srcSeq2);

		final VersionInfo versionInfo = mock(VersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final IMergeDNASequenceSets mergeSeqSets = new MergeDnaSequenceSets(
				newVersionInfo);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		srcSeqSet.clearSequences();

		srcSeq0.setSequence("AC");
		srcSeq1.setSequence("TC");
		srcSeq2.setSequence("AT");

		srcSeqSet.putSequence(srcOTUSet.getOtus().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(2), srcSeq2);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);
	}

	@Test
	public void mergeOnBlankTarget() {
		final DnaSequenceSet srcSeqSet =
				new DnaSequenceSet();
		srcSeqSet.setLabel("src-seq-set-0");
		final DnaSequenceSet trgSeqSet = new DnaSequenceSet();

		final OtuSet trgOTUSet = new OtuSet();
		trgOTUSet.addOtu(new Otu("otu-0"));
		trgOTUSet.addOtu(new Otu("otu-1"));
		trgOTUSet.addOtu(new Otu("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final OtuSet srcOTUSet = new OtuSet();
		srcOTUSet.addOtu(new Otu("otu-0"));
		srcOTUSet.addOtu(new Otu("otu-1"));
		srcOTUSet.addOtu(new Otu("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final DnaSequence srcSeq0 =
				(DnaSequence) new DnaSequence()
								.setSequence("ACT")
								.setAccession("jkjkj")
								.setName("jkejfke")
								.setDescription("jkfjeijf");
		final DnaSequence srcSeq1 =
				(DnaSequence) new DnaSequence()
						.setSequence("TCA")
						.setAccession("jjijk")
						.setName("jefeji")
						.setDescription("ejfiejiji");
		final DnaSequence srcSeq2 =
				(DnaSequence) new DnaSequence()
						.setSequence("ATC")
						.setAccession("jfje")
						.setName("jfifjiji")
						.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOtus().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOtus().get(2), srcSeq2);

		final VersionInfo versionInfo = mock(VersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final IMergeDNASequenceSets mergeSeqSets = new MergeDnaSequenceSets(
				newVersionInfo);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);

	}
}
