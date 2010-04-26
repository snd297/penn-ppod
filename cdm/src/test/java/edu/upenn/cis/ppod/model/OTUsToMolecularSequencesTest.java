package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test methods specific to the abstract class {@link OTUsToMolecularSequences}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.INIT, TestGroupDefs.FAST, TestGroupDefs.SINGLE })
public class OTUsToMolecularSequencesTest {

	@Inject
	private Provider<DNASequenceSet> dnaSequenceSetProvider;

	@Inject
	private Provider<OTUsToDNASequences> otusToDNASequencesProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<DNASequence> dnaSequenceProvider;

	/**
	 * Do a straight put with
	 * {@link OTUsToMolecularSequences#putHelper(OTU, MolecularSequence, MolecularSequenceSet)}
	 * .
	 */
	public void putHelperTest() {
		final OTUsToMolecularSequences<DNASequence, DNASequenceSet> otusToSequences =
				otusToDNASequencesProvider.get();
		final OTUSet otuSet = new OTUSet();
		final OTU otu = otuSet.addOTU(new OTU());
		final DNASequence sequence = new DNASequence();
		final DNASequenceSet sequenceSet = dnaSequenceSetProvider.get();
		otuSet.addDNASequenceSet(sequenceSet);
		((OTUsToMolecularSequences<DNASequence, DNASequenceSet>) otusToSequences)
				.putHelper(otu,
						sequence, sequenceSet);
		assertSame(otusToSequences.getOTUsToValues().get(otu), sequence);
	}

	/**
	 * When we replace an OTU's sequence with another, check that the previous
	 * sequence's sequence->sequenceSet relationship is severed.
	 */
	public void putHelperTestReplaceASequence() {
		final OTUsToMolecularSequences<DNASequence, DNASequenceSet> otusToSequences =
				otusToDNASequencesProvider.get();
		final OTUSet otuSet = otuSetProvider.get();
		final OTU otu = otuSet.addOTU(otuProvider.get());
		final DNASequence sequence = dnaSequenceProvider.get();
		final DNASequenceSet sequenceSet = dnaSequenceSetProvider.get();
		otuSet.addDNASequenceSet(sequenceSet);
		((OTUsToMolecularSequences<DNASequence, DNASequenceSet>) otusToSequences)
				.putHelper(otu,
						sequence, sequenceSet);

		final DNASequence sequence2 = dnaSequenceProvider.get();

		((OTUsToMolecularSequences<DNASequence, DNASequenceSet>) otusToSequences)
				.putHelper(otu,
						sequence2, sequenceSet);

		assertNull(sequence.getSequenceSet());

	}
}
