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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Test {@link SequenceSet}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class SequenceSetTest {

	@Test
	public void checkSequenceSizesOnEmptySequenceSet() {
		final SequenceSet<DnaSequence> seqSet = new DnaSequenceSet();
		final DnaSequence seq0 = new DnaSequence();

		final String seqStr0 = "ATACCCGACCGCTA";

		seq0.setSequence(seqStr0);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test
	public void checkSequenceSizesNonEmptySequenceSet() {
		final SequenceSet<DnaSequence> seqSet = new DnaSequenceSet();

		final DnaSequence seq0 = new DnaSequence();
		final String seqStr0 = "ATACCCGACCGCTA";
		seq0.setSequence(seqStr0);

		final DnaSequence seq1 = new DnaSequence();
		final String seqStr1 = "ATACACGTCCGCTG";
		seq1.setSequence(seqStr1);

		final DnaSequence seq2 = new DnaSequence();
		final String seqStr2 = "TTCCTCGTCCGCTG";
		seq2.setSequence(seqStr2);

		final DnaSequence seq3 = new DnaSequence();
		final String seqStr3 = "CTCCTCGTCAGCAG";
		seq3.setSequence(seqStr3);

		final OtuSet otuSet0 = new OtuSet();
		seqSet.setParent(otuSet0);

		final Otu otu0 = new Otu("otu0");
		final Otu otu1 = new Otu("otu1");
		final Otu otu2 = new Otu("otu2");
		otuSet0.addOtu(otu0);
		otuSet0.addOtu(otu1);
		otuSet0.addOtu(otu2);

		seqSet.putSequence(otu0, seq1);
		seqSet.putSequence(otu1, seq2);
		seqSet.putSequence(otu2, seq3);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkSequenceSizesNonEmptySequenceSetWrongLength() {
		final SequenceSet<DnaSequence> seqSet = new DnaSequenceSet();

		final DnaSequence seq0 = new DnaSequence();
		final String seqStr0 = "ATACCCGACCGCT";
		seq0.setSequence(seqStr0);

		final DnaSequence seq1 = new DnaSequence();
		final String seqStr1 = "ATACACGTCCGCTG";
		seq1.setSequence(seqStr1);

		final DnaSequence seq2 = new DnaSequence();
		final String seqStr2 = "TTCCTCGTCCGCTG";
		seq2.setSequence(seqStr2);

		final DnaSequence seq3 = new DnaSequence();
		final String seqStr3 = "CTCCTCGTCAGCAG";
		seq3.setSequence(seqStr3);

		final OtuSet otuSet0 = new OtuSet();
		seqSet.setParent(otuSet0);

		final Otu otu0 = new Otu("otu0");
		final Otu otu1 = new Otu("otu1");
		final Otu otu2 = new Otu("otu2");
		otuSet0.addOtu(otu0);
		otuSet0.addOtu(otu1);
		otuSet0.addOtu(otu2);

		seqSet.putSequence(otu0, seq1);
		seqSet.putSequence(otu1, seq2);
		seqSet.putSequence(otu2, seq3);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test
	public void setInNeedOfNewVersion() {
		final DnaSequenceSet seqSet = new DnaSequenceSet();
		assertFalse(seqSet.isInNeedOfNewVersion());
		seqSet.setInNeedOfNewVersion();
		assertTrue(seqSet.isInNeedOfNewVersion());

		final OtuSet otuSet = new OtuSet();
		otuSet.addDnaSequenceSet(seqSet);
		seqSet.unsetInNeedOfNewVersion();
		otuSet.unsetInNeedOfNewVersion();

		seqSet.setInNeedOfNewVersion();
		assertTrue(seqSet.isInNeedOfNewVersion());
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	@Test
	public void setLabel() {
		final SequenceSet<DnaSequence> seqSet = new DnaSequenceSet();
		assertNull(seqSet.getLabel());

		assertFalse(seqSet.isInNeedOfNewVersion());
		final String label = "seq-set";
		seqSet.setLabel(label);
		assertTrue(seqSet.isInNeedOfNewVersion());
		assertEquals(seqSet.getLabel(), label);

		seqSet.unsetInNeedOfNewVersion();
		assertFalse(seqSet.isInNeedOfNewVersion());
		seqSet.setLabel(label);
		assertEquals(seqSet.getLabel(), label);
		assertFalse(seqSet.isInNeedOfNewVersion());
	}

	@Test
	public void accept() {
		final OtuSet otuSet = new OtuSet();
		otuSet.addOtu(new Otu("otu-0"));
		otuSet.addOtu(new Otu("otu-1"));
		otuSet.addOtu(new Otu("otu-2"));

		final DnaSequenceSet seqSet = new DnaSequenceSet();

		otuSet.addDnaSequenceSet(seqSet);

		final DnaSequence seq0 = (DnaSequence) new DnaSequence()
				.setSequence("ATG");
		final DnaSequence seq1 = (DnaSequence) new DnaSequence()
				.setSequence("CTA");
		final DnaSequence seq2 = (DnaSequence) new DnaSequence()
				.setSequence("TTT");

		seqSet.putSequence(otuSet.getOtus().get(0), seq0);

		seqSet.putSequence(otuSet.getOtus().get(1), seq1);

		seqSet.putSequence(otuSet.getOtus().get(2), seq2);

		final IVisitor visitor = mock(IVisitor.class);

		seqSet.accept(visitor);

		verify(visitor, times(1)).visitDnaSequenceSet(seqSet);

		verify(visitor, times(seqSet.getSequences().values().size()))
				.visitDnaSequence(any(DnaSequence.class));

		verify(visitor).visitDnaSequence(seq0);
		verify(visitor).visitDnaSequence(seq1);
		verify(visitor).visitDnaSequence(seq2);

	}
}
