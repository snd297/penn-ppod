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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IDNASequence;
import edu.upenn.cis.ppod.imodel.IOtu;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.ISequenceSet;
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
		final SequenceSet<IDNASequence> seqSet = new DNASequenceSet();
		final IDNASequence seq0 = new DNASequence();

		final String seqStr0 = "ATACCCGACCGCTA";

		seq0.setSequence(seqStr0);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test
	public void checkSequenceSizesNonEmptySequenceSet() {
		final SequenceSet<IDNASequence> seqSet = new DNASequenceSet();

		final IDNASequence seq0 = new DNASequence();
		final String seqStr0 = "ATACCCGACCGCTA";
		seq0.setSequence(seqStr0);

		final IDNASequence seq1 = new DNASequence();
		final String seqStr1 = "ATACACGTCCGCTG";
		seq1.setSequence(seqStr1);

		final IDNASequence seq2 = new DNASequence();
		final String seqStr2 = "TTCCTCGTCCGCTG";
		seq2.setSequence(seqStr2);

		final IDNASequence seq3 = new DNASequence();
		final String seqStr3 = "CTCCTCGTCAGCAG";
		seq3.setSequence(seqStr3);

		final IOTUSet otuSet0 = new OTUSet();
		seqSet.setParent(otuSet0);

		final IOtu otu0 = new OTU().setLabel("otu0");
		final IOtu otu1 = new OTU().setLabel("otu1");
		final IOtu otu2 = new OTU().setLabel("otu2");
		otuSet0.addOTU(otu0);
		otuSet0.addOTU(otu1);
		otuSet0.addOTU(otu2);

		seqSet.putSequence(otu0, seq1);
		seqSet.putSequence(otu1, seq2);
		seqSet.putSequence(otu2, seq3);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkSequenceSizesNonEmptySequenceSetWrongLength() {
		final SequenceSet<IDNASequence> seqSet = new DNASequenceSet();

		final IDNASequence seq0 = new DNASequence();
		final String seqStr0 = "ATACCCGACCGCT";
		seq0.setSequence(seqStr0);

		final IDNASequence seq1 = new DNASequence();
		final String seqStr1 = "ATACACGTCCGCTG";
		seq1.setSequence(seqStr1);

		final IDNASequence seq2 = new DNASequence();
		final String seqStr2 = "TTCCTCGTCCGCTG";
		seq2.setSequence(seqStr2);

		final IDNASequence seq3 = new DNASequence();
		final String seqStr3 = "CTCCTCGTCAGCAG";
		seq3.setSequence(seqStr3);

		final IOTUSet otuSet0 = new OTUSet();
		seqSet.setParent(otuSet0);

		final IOtu otu0 = new OTU().setLabel("otu0");
		final IOtu otu1 = new OTU().setLabel("otu1");
		final IOtu otu2 = new OTU().setLabel("otu2");
		otuSet0.addOTU(otu0);
		otuSet0.addOTU(otu1);
		otuSet0.addOTU(otu2);

		seqSet.putSequence(otu0, seq1);
		seqSet.putSequence(otu1, seq2);
		seqSet.putSequence(otu2, seq3);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test
	public void setInNeedOfNewVersion() {
		final DNASequenceSet seqSet = new DNASequenceSet();
		assertFalse(seqSet.isInNeedOfNewVersion());
		seqSet.setInNeedOfNewVersion();
		assertTrue(seqSet.isInNeedOfNewVersion());

		final IOTUSet otuSet = new OTUSet();
		otuSet.addDNASequenceSet(seqSet);
		seqSet.unsetInNeedOfNewVersion();
		otuSet.unsetInNeedOfNewVersion();

		seqSet.setInNeedOfNewVersion();
		assertTrue(seqSet.isInNeedOfNewVersion());
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	@Test
	public void setLabel() {
		final ISequenceSet<IDNASequence> seqSet = new DNASequenceSet();
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
	public void afterUnmarshal() {
		final SequenceSet<?> seqSet = new DNASequenceSet();
		final IOTUSet otuSet = new OTUSet();
		seqSet.afterUnmarshal(null, otuSet);
		assertSame(seqSet.getParent(), otuSet);
	}

	@Test
	public void accept() {
		final IOTUSet otuSet = new OTUSet();
		otuSet.addOTU(new OTU().setLabel("otu-0"));
		otuSet.addOTU(new OTU().setLabel("otu-1"));
		otuSet.addOTU(new OTU().setLabel("otu-2"));

		final DNASequenceSet seqSet = new DNASequenceSet();

		otuSet.addDNASequenceSet(seqSet);

		final IDNASequence seq0 = (DNASequence) new DNASequence()
				.setSequence("ATG");
		final IDNASequence seq1 = (DNASequence) new DNASequence()
				.setSequence("CTA");
		final IDNASequence seq2 = (DNASequence) new DNASequence()
				.setSequence("TTT");

		seqSet.putSequence(otuSet.getOTUs().get(0), seq0);

		seqSet.putSequence(otuSet.getOTUs().get(1), seq1);

		seqSet.putSequence(otuSet.getOTUs().get(2), seq2);

		final IVisitor visitor = mock(IVisitor.class);

		seqSet.accept(visitor);

		verify(visitor, times(1)).visitDNASequenceSet(seqSet);

		verify(visitor, times(seqSet.getSequences().values().size()))
				.visitDNASequence(any(DNASequence.class));

		verify(visitor).visitDNASequence(seq0);
		verify(visitor).visitDNASequence(seq1);
		verify(visitor).visitDNASequence(seq2);

	}
}
