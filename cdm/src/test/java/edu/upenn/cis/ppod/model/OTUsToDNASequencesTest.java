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
package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IDnaSequence;

/**
 * Test {@link OTUsToDNASequences}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class OTUsToDNASequencesTest {

	/**
	 * Do a straight put with
	 * {@link OTUsToSequences#putHelper(OTU, Sequence, SequenceSet)} .
	 */
	@Test
	public void putTest() {

		final OtuSet otuSet = new OtuSet();
		final Otu otu = new Otu();
		otuSet.addOTU(otu);
		final IDnaSequence sequence = new DnaSequence();
		final DnaSequenceSet sequenceSet = new DnaSequenceSet();
		otuSet.addDNASequenceSet(sequenceSet);
		sequenceSet.getOTUKeyedSequences().put(otu, sequence);
		assertSame(sequenceSet.getOTUKeyedSequences().getValues()
				.get(otu),
				sequence);
	}

	/**
	 * When we replace an OTU's sequence with another, check that the previous
	 * sequence's sequence->sequenceSet relationship is severed.
	 */
	@Test
	public void putTestReplaceASequence() {
		final OtuSet otuSet = new OtuSet();
		final Otu otu = new Otu();
		otuSet.addOTU(otu);
		final DnaSequence sequence = new DnaSequence();
		final DnaSequenceSet sequenceSet = new DnaSequenceSet();
		otuSet.addDNASequenceSet(sequenceSet);
		sequenceSet.getOTUKeyedSequences().put(otu, sequence);

		final IDnaSequence sequence2 = new DnaSequence();

		sequenceSet.getOTUKeyedSequences().put(otu, sequence2);

		assertNull(sequence.getParent());

	}
}
