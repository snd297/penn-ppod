package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link OTUsToDNASequences}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.INIT, TestGroupDefs.FAST })
public class OTUsToDNASequencesTest {

	@Inject
	private Provider<DNASequenceSet> dnaSequenceSetProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<DNASequence> dnaSequenceProvider;

	/**
	 * Do a straight put with
	 * {@link OTUsToSequences#putHelper(OTU, Sequence, SequenceSet)} .
	 */
	public void putTest() {

		final OTUSet otuSet = new OTUSet();
		final OTU otu = otuSet.addOTU(new OTU());
		final DNASequence sequence = new DNASequence();
		final DNASequenceSet sequenceSet = dnaSequenceSetProvider.get();
		otuSet.addDNASequenceSet(sequenceSet);
		sequenceSet.getOTUsToSequences().put(otu, sequence);
		assertSame(sequenceSet.getOTUsToSequences().getOTUsToValues().get(otu),
				sequence);
	}

	/**
	 * When we replace an OTU's sequence with another, check that the previous
	 * sequence's sequence->sequenceSet relationship is severed.
	 */
	public void putTestReplaceASequence() {
		final OTUSet otuSet = otuSetProvider.get();
		final OTU otu = otuSet.addOTU(otuProvider.get());
		final DNASequence sequence = dnaSequenceProvider.get();
		final DNASequenceSet sequenceSet = dnaSequenceSetProvider.get();
		otuSet.addDNASequenceSet(sequenceSet);
		((OTUsToSequences<DNASequence>) sequenceSet.getOTUsToSequences())
				.put(otu, sequence);

		final DNASequence sequence2 = dnaSequenceProvider.get();

		sequenceSet.getOTUsToSequences().put(otu, sequence2);

		assertNull(sequence.getOTUsToSequences());

	}
}
