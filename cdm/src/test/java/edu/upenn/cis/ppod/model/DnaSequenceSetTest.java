package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

@Test(groups = TestGroupDefs.FAST)
public class DnaSequenceSetTest {
	@Test
	public void putSequence() {
		DnaSequenceSet sequenceSet = new DnaSequenceSet();
		DnaSequence sequence = new DnaSequence();
		OtuSet otuSet = new OtuSet();
		otuSet.addDnaSequenceSet(sequenceSet);
		Otu otu = new Otu();
		otuSet.addOtu(otu);

		sequenceSet.putSequence(otu, sequence);
		assertEquals(sequenceSet.getSequences().size(), 1);
		assertTrue(sequenceSet.getSequences().values().contains(sequence));
		assertSame(sequence.getParent(), sequenceSet);

	}

	/**
	 * When we replace an OTU's sequence with another, check that the previous
	 * sequence's sequence->sequenceSet relationship is severed.
	 */
	@Test
	public void putTestReplaceASequence() {
		final OtuSet otuSet = new OtuSet();
		final Otu otu = new Otu();
		otuSet.addOtu(otu);
		final DnaSequence sequence = new DnaSequence();
		final DnaSequenceSet sequenceSet = new DnaSequenceSet();
		otuSet.addDnaSequenceSet(sequenceSet);
		sequenceSet.putSequence(otu, sequence);

		final DnaSequence sequence2 = new DnaSequence();

		sequenceSet.putSequence(otu, sequence2);

		assertNull(sequence.getParent());
		assertEquals(sequenceSet.getSequences().size(), 1);
		assertTrue(sequenceSet.getSequences().values().contains(sequence2));

	}
}
