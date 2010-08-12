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

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.TestObjectWithLongIdDAO;
import edu.upenn.cis.ppod.imodel.IDNASequence;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.INewVersionInfo;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.model.ModelAssert;

@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class MergeSequenceSetsTest {
	@Inject
	private IMergeSequenceSets.IFactory<IDNASequenceSet, IDNASequence> mergeDNASequenceSetsFactory;

	@Inject
	private Provider<IDNASequenceSet> dnaSequenceSetProvider;

	@Inject
	private Provider<IDNASequence> dnaSequenceProvider;

	@Inject
	private Provider<IOTUSet> otuSetProvider;

	@Inject
	private Provider<IOTU> otuProvider;

	@Inject
	private INewVersionInfo newVersionInfo;

	@Inject
	private TestObjectWithLongIdDAO dao;

	@Test
	public void modifySequencesKeepLength() {
		final IMergeSequenceSets<IDNASequenceSet, IDNASequence> mergeSeqSets =
					mergeDNASequenceSetsFactory.create(dao, newVersionInfo);

		final IDNASequenceSet srcSeqSet =
					dnaSequenceSetProvider.get();
		srcSeqSet.setLabel("src-seq-set-0");
		final IDNASequenceSet trgSeqSet = dnaSequenceSetProvider.get();

		final IOTUSet trgOTUSet = otuSetProvider.get();
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-0"));
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-1"));
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final IOTUSet srcOTUSet = otuSetProvider.get();
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-0"));
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-1"));
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final IDNASequence srcSeq0 =
					(IDNASequence) dnaSequenceProvider
							.get()
							.setSequence("ACT")
							.setAccession("jkjkj")
							.setName("jkejfke")
							.setDescription("jkfjeijf");
		final IDNASequence srcSeq1 =
					(IDNASequence) dnaSequenceProvider
							.get()
							.setSequence("TCA")
							.setAccession("jjijk")
							.setName("jefeji")
							.setDescription("ejfiejiji");
		final IDNASequence srcSeq2 =
					(IDNASequence) dnaSequenceProvider
							.get()
							.setSequence("ATC")
							.setAccession("jfje")
							.setName("jfifjiji")
							.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), srcSeq2);

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
		final IMergeSequenceSets<IDNASequenceSet, IDNASequence> mergeSeqSets =
					mergeDNASequenceSetsFactory.create(dao, newVersionInfo);

		final IDNASequenceSet srcSeqSet =
				dnaSequenceSetProvider.get();
		srcSeqSet.setLabel("src-seq-set-0");
		final IDNASequenceSet trgSeqSet = dnaSequenceSetProvider.get();

		final IOTUSet trgOTUSet = otuSetProvider.get();
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-0"));
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-1"));
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final IOTUSet srcOTUSet = otuSetProvider.get();
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-0"));
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-1"));
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final IDNASequence srcSeq0 =
					(IDNASequence) dnaSequenceProvider
							.get()
							.setSequence("ACT")
							.setAccession("jkjkj")
							.setName("jkejfke")
							.setDescription("jkfjeijf");
		final IDNASequence srcSeq1 =
					(IDNASequence) dnaSequenceProvider
							.get()
							.setSequence("TCA")
							.setAccession("jjijk")
							.setName("jefeji")
							.setDescription("ejfiejiji");
		final IDNASequence srcSeq2 =
					(IDNASequence) dnaSequenceProvider
							.get()
							.setSequence("ATC")
							.setAccession("jfje")
							.setName("jfifjiji")
							.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), srcSeq2);

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
		final IMergeSequenceSets<IDNASequenceSet, IDNASequence> mergeSeqSets =
				mergeDNASequenceSetsFactory.create(dao, newVersionInfo);

		final IDNASequenceSet srcSeqSet =
				dnaSequenceSetProvider.get();
		srcSeqSet.setLabel("src-seq-set-0");
		final IDNASequenceSet trgSeqSet = dnaSequenceSetProvider.get();

		final IOTUSet trgOTUSet = otuSetProvider.get();
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-0"));
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-1"));
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final IOTUSet srcOTUSet = otuSetProvider.get();
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-0"));
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-1"));
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final IDNASequence srcSeq0 =
				(IDNASequence) dnaSequenceProvider
						.get()
						.setSequence("ACT")
						.setAccession("jkjkj")
						.setName("jkejfke")
						.setDescription("jkfjeijf");
		final IDNASequence srcSeq1 =
				(IDNASequence) dnaSequenceProvider
						.get()
						.setSequence("TCA")
						.setAccession("jjijk")
						.setName("jefeji")
						.setDescription("ejfiejiji");
		final IDNASequence srcSeq2 =
				(IDNASequence) dnaSequenceProvider
						.get()
						.setSequence("ATC")
						.setAccession("jfje")
						.setName("jfifjiji")
						.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), srcSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), srcSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), srcSeq2);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);

	}
}
