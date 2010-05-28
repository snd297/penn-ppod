package edu.upenn.cis.ppod.createorupdate;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.TestObjectWithLongIdDAO;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.modelinterfaces.INewVersionInfo;

@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class MergeSequenceSetsTest {
	@Inject
	private IMergeSequenceSets.IFactory<DNASequenceSet, DNASequence> mergeDNASequenceSetsFactory;

	@Inject
	private Provider<DNASequenceSet> dnaSequenceSetProvider;

	@Inject
	private Provider<DNASequence> dnaSequenceProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private INewVersionInfo newVersionInfo;

	@Inject
	private TestObjectWithLongIdDAO dao;

	public void mergeOnBlankTarget() {
		final IMergeSequenceSets<DNASequenceSet, DNASequence> mergeSeqSets =
				mergeDNASequenceSetsFactory.create(dao, newVersionInfo);

		final DNASequenceSet srcSeqSet =
				(DNASequenceSet) dnaSequenceSetProvider
						.get()
						.setLabel("src-seq-set-0");
		final DNASequenceSet trgSeqSet = dnaSequenceSetProvider.get();

		final OTUSet trgOTUSet = otuSetProvider.get();
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-0"));
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-1"));
		trgOTUSet.addOTU(otuProvider.get().setLabel("otu-2"));
		trgOTUSet.addDNASequenceSet(trgSeqSet);

		final OTUSet srcOTUSet = otuSetProvider.get();
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-0"));
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-1"));
		srcOTUSet.addOTU(otuProvider.get().setLabel("otu-2"));

		srcOTUSet.addDNASequenceSet(srcSeqSet);

		final DNASequence trgSeq0 =
				(DNASequence) dnaSequenceProvider
						.get()
						.setSequence("ACT")
						.setAccession("jkjkj")
						.setName("jkejfke")
						.setDescription("jkfjeijf");
		final DNASequence trgSeq1 =
				(DNASequence) dnaSequenceProvider
						.get()
						.setSequence("TCA")
						.setAccession("jjijk")
						.setName("jefeji")
						.setDescription("ejfiejiji");
		final DNASequence trgSeq2 =
				(DNASequence) dnaSequenceProvider
						.get()
						.setSequence("ATC")
						.setAccession("jfje")
						.setName("jfifjiji")
						.setDescription("fjijeifji");

		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(0), trgSeq0);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(1), trgSeq1);
		srcSeqSet.putSequence(srcOTUSet.getOTUs().get(2), trgSeq2);

		mergeSeqSets.mergeSequenceSets(trgSeqSet, srcSeqSet);

		ModelAssert.assertEqualsSequenceSets(trgSeqSet, srcSeqSet);
	}
}
