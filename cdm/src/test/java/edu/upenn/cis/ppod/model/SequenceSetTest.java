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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IDNASequence;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.ISequenceSet;
import edu.upenn.cis.ppod.util.TestVisitor;

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
	private Provider<IDNASequence> dnaSequenceProvider;

	@Inject
	private Provider<IOTUSet> otuSetProvider;

	@Inject
	private Provider<IOTU> otuProvider;

	@Inject
	private Provider<TestVisitor> testVisitorProvider;

	@Test
	public void checkSequenceSizesOnEmptySequenceSet() {
		final SequenceSet<IDNASequence> seqSet = dnaSequenceSetProvider.get();
		final IDNASequence seq0 = dnaSequenceProvider.get();

		final String seqStr0 = "ATACCCGACCGCTA";

		seq0.setSequence(seqStr0);

		// This should not throw an exception - that's the test
		seqSet.checkSequenceLength(seq0);
	}

	@Test
	public void checkSequenceSizesNonEmptySequenceSet() {
		final SequenceSet<IDNASequence> seqSet = dnaSequenceSetProvider.get();

		final IDNASequence seq0 = dnaSequenceProvider.get();
		final String seqStr0 = "ATACCCGACCGCTA";
		seq0.setSequence(seqStr0);

		final IDNASequence seq1 = dnaSequenceProvider.get();
		final String seqStr1 = "ATACACGTCCGCTG";
		seq1.setSequence(seqStr1);

		final IDNASequence seq2 = dnaSequenceProvider.get();
		final String seqStr2 = "TTCCTCGTCCGCTG";
		seq2.setSequence(seqStr2);

		final IDNASequence seq3 = dnaSequenceProvider.get();
		final String seqStr3 = "CTCCTCGTCAGCAG";
		seq3.setSequence(seqStr3);

		final IOTUSet otuSet0 = otuSetProvider.get();
		seqSet.setParent(otuSet0);

		final IOTU otu0 = otuProvider.get().setLabel("otu0");
		final IOTU otu1 = otuProvider.get().setLabel("otu1");
		final IOTU otu2 = otuProvider.get().setLabel("otu2");
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
		final SequenceSet<IDNASequence> seqSet = dnaSequenceSetProvider.get();

		final IDNASequence seq0 = dnaSequenceProvider.get();
		final String seqStr0 = "ATACCCGACCGCT";
		seq0.setSequence(seqStr0);

		final IDNASequence seq1 = dnaSequenceProvider.get();
		final String seqStr1 = "ATACACGTCCGCTG";
		seq1.setSequence(seqStr1);

		final IDNASequence seq2 = dnaSequenceProvider.get();
		final String seqStr2 = "TTCCTCGTCCGCTG";
		seq2.setSequence(seqStr2);

		final IDNASequence seq3 = dnaSequenceProvider.get();
		final String seqStr3 = "CTCCTCGTCAGCAG";
		seq3.setSequence(seqStr3);

		final IOTUSet otuSet0 = otuSetProvider.get();
		seqSet.setParent(otuSet0);

		final IOTU otu0 = otuProvider.get().setLabel("otu0");
		final IOTU otu1 = otuProvider.get().setLabel("otu1");
		final IOTU otu2 = otuProvider.get().setLabel("otu2");
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
		final DNASequenceSet seqSet = dnaSequenceSetProvider.get();
		assertFalse(seqSet.isInNeedOfNewVersion());
		seqSet.setInNeedOfNewVersion();
		assertTrue(seqSet.isInNeedOfNewVersion());

		final IOTUSet otuSet = otuSetProvider.get();
		otuSet.addDNASequenceSet(seqSet);
		seqSet.unsetInNeedOfNewVersion();
		otuSet.unsetInNeedOfNewVersion();

		seqSet.setInNeedOfNewVersion();
		assertTrue(seqSet.isInNeedOfNewVersion());
		assertTrue(otuSet.isInNeedOfNewVersion());
	}

	@Test
	public void setLabel() {
		final ISequenceSet<IDNASequence> seqSet = dnaSequenceSetProvider.get();
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
		final SequenceSet<?> seqSet = dnaSequenceSetProvider.get();
		final IOTUSet otuSet = otuSetProvider.get();
		seqSet.afterUnmarshal(null, otuSet);
		assertSame(seqSet.getParent(), otuSet);
	}

	@Test
	public void accept() {
		final IOTUSet otuSet = otuSetProvider.get();
		otuSet.addOTU(otuProvider.get().setLabel("otu-0"));
		otuSet.addOTU(otuProvider.get().setLabel("otu-1"));
		otuSet.addOTU(otuProvider.get().setLabel("otu-2"));

		final DNASequenceSet seqSet = dnaSequenceSetProvider.get();

		otuSet.addDNASequenceSet(seqSet);

		seqSet.putSequence(otuSet.getOTUs().get(0),
				(DNASequence) dnaSequenceProvider.get().setSequence("ATG"));
		seqSet.putSequence(otuSet.getOTUs().get(1),
				(DNASequence) dnaSequenceProvider.get().setSequence("CTA"));
		seqSet.putSequence(otuSet.getOTUs().get(2),
				(DNASequence) dnaSequenceProvider.get().setSequence("TTT"));

		final TestVisitor visitor = testVisitorProvider.get();

		seqSet.accept(visitor);

		final List<Object> visited = visitor.getVisited();

		assertEquals(visited.size(),
				seqSet.getSequences().values().size() + 1); // add
		// in
		// one
		// for
		// parent

		assertSame(visited.get(0), seqSet);

		// Order undefined for visiting the children
		final List<Object> visitedChildren = visited.subList(1, visited.size());
		for (final Object sequence : seqSet.getSequences().values()) {
			assertTrue(visitedChildren.contains(sequence));
		}

	}
}
