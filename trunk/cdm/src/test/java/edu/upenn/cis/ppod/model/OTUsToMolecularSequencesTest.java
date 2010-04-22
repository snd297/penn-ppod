package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 * 
 */
@Test(groups = { TestGroupDefs.INIT, TestGroupDefs.FAST, TestGroupDefs.SINGLE })
public class OTUsToMolecularSequencesTest {

	@Inject
	private Provider<DNASequenceSet> dnaSequenceSetProvider;

	public void putHelperTest() {
		final OTUsToMolecularSequences<DNASequence, DNASequenceSet> otusToSequences =
				new OTUsToDNASequences();
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
}
