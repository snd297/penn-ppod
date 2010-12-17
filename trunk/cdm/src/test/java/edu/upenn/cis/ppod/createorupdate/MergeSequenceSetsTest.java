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
import edu.upenn.cis.ppod.imodel.IDNASequence;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IVersionInfo;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;

@Test(groups = TestGroupDefs.FAST)
public class MergeSequenceSetsTest {

	@Test
	public void modifySequencesKeepLength() {

		final IDNASequenceSet srcSeqSet = new DNASequenceSet();

		srcSeqSet.setLabel("src-seq-set-0");
		final IDNASequenceSet trgSeqSet = new DNASequenceSet();

		final IOTUSet trgOTUSet = new OTUSet();
		trgOTUSet.addOTU(new OTU("otu-0"));
		trgOTUSet.addOTU(new OTU("otu-1"));
		trgOTUSet.addOTU(new OTU("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final IOTUSet srcOTUSet = new OTUSet();
		srcOTUSet.addOTU(new OTU("otu-0"));
		srcOTUSet.addOTU(new OTU("otu-1"));
		srcOTUSet.addOTU(new OTU("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final IDNASequence srcSeq0 =
					(IDNASequence) new DNASequence()
							.setSequence("ACT")
																	.setAccession(
																			"jkjkj")
																	.setName(
																			"jkejfke")
																	.setDescription(
																			"jkfjeijf");
		final IDNASequence srcSeq1 =
					(IDNASequence) new DNASequence()
							.setSequence("TCA")
							.setAccession("jjijk")
							.setName("jefeji")
							.setDescription("ejfiejiji");
		final IDNASequence srcSeq2 =
					(IDNASequence) new DNASequence()
							.setSequence("ATC")
							.setAccession("jfje")
							.setName("jfifjiji")
							.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), srcSeq2);

		final IVersionInfo versionInfo = mock(IVersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final IMergeDNASequenceSets mergeSeqSets = new MergeDNASequenceSets(
				newVersionInfo);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		srcSeq0.setSequence("ACC");
		srcSeq1.setSequence("TCT");
		srcSeq2.setSequence("ATA");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), srcSeq2);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);
	}

	@Test
	public void shortenSequences() {

		final IDNASequenceSet srcSeqSet =
				new DNASequenceSet();

		srcSeqSet.setLabel("src-seq-set-0");
		final IDNASequenceSet trgSeqSet = new DNASequenceSet();

		final IOTUSet trgOTUSet = new OTUSet();
		trgOTUSet.addOTU(new OTU("otu-0"));
		trgOTUSet.addOTU(new OTU("otu-1"));
		trgOTUSet.addOTU(new OTU("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final IOTUSet srcOTUSet = new OTUSet();
		srcOTUSet.addOTU(new OTU("otu-0"));
		srcOTUSet.addOTU(new OTU("otu-1"));
		srcOTUSet.addOTU(new OTU("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final IDNASequence srcSeq0 =
				(IDNASequence) new DNASequence()
							.setSequence("ACT")
							.setAccession("jkjkj")
							.setName("jkejfke")
							.setDescription("jkfjeijf");
		final IDNASequence srcSeq1 =
				(IDNASequence) new DNASequence()
							.setSequence("TCA")
							.setAccession("jjijk")
							.setName("jefeji")
							.setDescription("ejfiejiji");
		final IDNASequence srcSeq2 =
				(IDNASequence) new DNASequence()
							.setSequence("ATC")
							.setAccession("jfje")
							.setName("jfifjiji")
							.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), srcSeq2);

		final IVersionInfo versionInfo = mock(IVersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final IMergeDNASequenceSets mergeSeqSets = new MergeDNASequenceSets(
				newVersionInfo);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		srcSeqSet.clearSequences();

		srcSeq0.setSequence("AC");
		srcSeq1.setSequence("TC");
		srcSeq2.setSequence("AT");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), srcSeq2);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);
	}

	@Test
	public void mergeOnBlankTarget() {
		final IDNASequenceSet srcSeqSet =
				new DNASequenceSet();
		srcSeqSet.setLabel("src-seq-set-0");
		final IDNASequenceSet trgSeqSet = new DNASequenceSet();

		final IOTUSet trgOTUSet = new OTUSet();
		trgOTUSet.addOTU(new OTU("otu-0"));
		trgOTUSet.addOTU(new OTU("otu-1"));
		trgOTUSet.addOTU(new OTU("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final IOTUSet srcOTUSet = new OTUSet();
		srcOTUSet.addOTU(new OTU("otu-0"));
		srcOTUSet.addOTU(new OTU("otu-1"));
		srcOTUSet.addOTU(new OTU("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final IDNASequence srcSeq0 =
				(IDNASequence) new DNASequence()
								.setSequence("ACT")
								.setAccession("jkjkj")
								.setName("jkejfke")
								.setDescription("jkfjeijf");
		final IDNASequence srcSeq1 =
				(IDNASequence) new DNASequence()
						.setSequence("TCA")
						.setAccession("jjijk")
						.setName("jefeji")
						.setDescription("ejfiejiji");
		final IDNASequence srcSeq2 =
				(IDNASequence) new DNASequence()
						.setSequence("ATC")
						.setAccession("jfje")
						.setName("jfifjiji")
						.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), srcSeq2);

		final IVersionInfo versionInfo = mock(IVersionInfo.class);
		final INewVersionInfo newVersionInfo = mock(INewVersionInfo.class);
		when(newVersionInfo.getNewVersionInfo()).thenReturn(versionInfo);

		final IMergeDNASequenceSets mergeSeqSets = new MergeDNASequenceSets(
				newVersionInfo);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);

	}
}
