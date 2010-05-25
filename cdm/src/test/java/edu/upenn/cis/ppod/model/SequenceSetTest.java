package edu.upenn.cis.ppod.model;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link SequenceSet}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class SequenceSetTest {

	@Inject
	private Provider<DNASequenceSet> dnaSequenceSetProvider;

	@Inject
	private Provider<DNASequence> dnaSequenceProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Test
	public void checkSequenceSizesOnEmptySequenceSet() {
		final SequenceSet<DNASequence> seqSet = dnaSequenceSetProvider.get();
		final DNASequence seq0 = dnaSequenceProvider.get();

		final String seqStr0 = "ATACCCGACCGCTA";

		seq0.setSequence(seqStr0);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test
	public void checkSequenceSizesNonEmptySequenceSet() {
		final SequenceSet<DNASequence> seqSet = dnaSequenceSetProvider.get();

		final DNASequence seq0 = dnaSequenceProvider.get();
		final String seqStr0 = "ATACCCGACCGCTA";
		seq0.setSequence(seqStr0);

		final DNASequence seq1 = dnaSequenceProvider.get();
		final String seqStr1 = "ATACACGTCCGCTG";
		seq1.setSequence(seqStr1);

		final DNASequence seq2 = dnaSequenceProvider.get();
		final String seqStr2 = "TTCCTCGTCCGCTG";
		seq2.setSequence(seqStr2);

		final DNASequence seq3 = dnaSequenceProvider.get();
		final String seqStr3 = "CTCCTCGTCAGCAG";
		seq3.setSequence(seqStr3);

		final OTUSet otuSet0 = otuSetProvider.get();
		seqSet.setOTUSet(otuSet0);

		final OTU otu0 = otuSet0.addOTU(otuProvider.get()).setLabel("otu-0");
		final OTU otu1 = otuSet0.addOTU(otuProvider.get()).setLabel("otu-1");
		final OTU otu2 = otuSet0.addOTU(otuProvider.get()).setLabel("otu-2");

		seqSet.putSequence(otu0, seq1);
		seqSet.putSequence(otu1, seq2);
		seqSet.putSequence(otu2, seq3);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkSequenceSizesNonEmptySequenceSetWrongLength() {
		final SequenceSet<DNASequence> seqSet = dnaSequenceSetProvider.get();

		final DNASequence seq0 = dnaSequenceProvider.get();
		final String seqStr0 = "ATACCCGACCGCT";
		seq0.setSequence(seqStr0);

		final DNASequence seq1 = dnaSequenceProvider.get();
		final String seqStr1 = "ATACACGTCCGCTG";
		seq1.setSequence(seqStr1);

		final DNASequence seq2 = dnaSequenceProvider.get();
		final String seqStr2 = "TTCCTCGTCCGCTG";
		seq2.setSequence(seqStr2);

		final DNASequence seq3 = dnaSequenceProvider.get();
		final String seqStr3 = "CTCCTCGTCAGCAG";
		seq3.setSequence(seqStr3);

		final OTUSet otuSet0 = otuSetProvider.get();
		seqSet.setOTUSet(otuSet0);

		final OTU otu0 = otuSet0.addOTU(otuProvider.get()).setLabel("otu-0");
		final OTU otu1 = otuSet0.addOTU(otuProvider.get()).setLabel("otu-1");
		final OTU otu2 = otuSet0.addOTU(otuProvider.get()).setLabel("otu-2");

		seqSet.putSequence(otu0, seq1);
		seqSet.putSequence(otu1, seq2);
		seqSet.putSequence(otu2, seq3);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}
}
