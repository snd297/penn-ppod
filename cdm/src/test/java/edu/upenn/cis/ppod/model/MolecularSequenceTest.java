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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Tests for {@link Sequence}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class MolecularSequenceTest {

	@Inject
	private Provider<DNASequence> dnaSequenceProvider;

	@Inject
	private Provider<DNASequenceSet> dnaSequenceSetProvider;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	/**
	 * Tests the following:
	 * <ol>
	 * <li>straight setSequence and check isInNeedOfNewPPodVersion</li>
	 * <li>setSequence with same value and make sure isInNeedOfNewPPodVersion
	 * stays false</li>
	 * </ol>
	 */
	@Test
	public void setSequence() {
		final Sequence sequence = dnaSequenceProvider
				.get();
		final String sequenceString = "ACGTAC-T-A";
		sequence.setSequence(sequenceString);
		assertEquals(sequence.getSequence(), sequenceString);
		assertTrue(sequence.isInNeedOfNewVersion());

		// Now set the same sequence - should have no affect on need for new
		// ppod version
		sequence.unsetInNeedOfNewVersion();
		sequence.setSequence(sequenceString);
		assertEquals(sequence.getSequence(), sequenceString);
		assertFalse(sequence.isInNeedOfNewVersion());
	}

	/**
	 * {@code setSequence(...)} with an illegal character. Note that since
	 * {@link DNASequenceSet.isLegal} is tested separately and should be testing
	 * all illegal characters, we don't need to test all illegal characters
	 * here. Note that the fact that {@code setSequence(...)} is calling
	 * {@code isLegal(...)} is part of its public contract, so it's all right
	 * for us to reason about it here.
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setSequenceWithAnIllegalCharacter() {
		final Sequence sequence = dnaSequenceProvider
				.get();
		final String sequenceString = "ACGTlC-T-A";
		sequence.setSequence(sequenceString);
	}

	/**
	 * Make sure that {@link Sequence#setInNeedOfNewPPodVersionInfo()} works on
	 * both the sequence and parent sequence set.
	 * <p>
	 * Also make sure that it works if no parent has been set.
	 */
	@Test
	public void setInNeedOfNewPPodVersion() {
		final DNASequence sequence = dnaSequenceProvider
				.get();
		final OTUSet otuSet = otuSetProvider.get();
		final OTU otu0 = otuSet.addOTU(otuProvider.get());

		final SequenceSet<DNASequence> sequenceSet = dnaSequenceSetProvider
				.get();

		sequenceSet.setOTUSet(otuSet);

		sequence.setSequence("");

		sequenceSet.putSequence(otu0, sequence);

		sequence.unsetInNeedOfNewVersion();
		sequenceSet.unsetInNeedOfNewVersion();

		sequence.setInNeedOfNewVersion();

		assertTrue(sequence.isInNeedOfNewVersion());

		assertTrue(sequenceSet.isInNeedOfNewVersion());

		// Let's make sure it works if no parent has been set.
		sequence.setParent(null);
		sequence.unsetInNeedOfNewVersion();

		sequence.setInNeedOfNewVersion();

		assertTrue(sequence.isInNeedOfNewVersion());

	}

	/**
	 * Run setName through its paces:
	 * <ol>
	 * <li>straight set and verify in need of new pPOD version</li>
	 * <li>set w/ already-value and make sure its not in need of a new pPOD
	 * version</li>
	 * <li>set w/ a null value</li>
	 * </ol>
	 */
	@Test
	public void setName() {
		final Sequence sequence = dnaSequenceProvider.get();

		assertNull(sequence.getName());

		final String sequenceName = "SEQ0";
		sequence.setName(sequenceName);

		assertEquals(sequence.getName(), sequenceName);

		assertTrue(sequence.isInNeedOfNewVersion());

		sequence.unsetInNeedOfNewVersion();

		sequence.setName(sequenceName);

		assertFalse(sequence.isInNeedOfNewVersion());

		sequence.unsetInNeedOfNewVersion();

		sequence.setName(null);

		assertNull(sequence.getName());

	}

	/**
	 * Run setAccesion through its paces:
	 * <ol>
	 * <li>straight set and verify in need of new pPOD version</li>
	 * <li>set w/ already-value and make sure its not in need of a new pPOD
	 * version</li>
	 * <li>set w/ a null value</li>
	 * </ol>
	 */
	@Test
	public void setAccession() {
		final Sequence sequence = dnaSequenceProvider.get();

		assertNull(sequence.getAccession());

		final String accession = "ACC0";
		sequence.setAccession(accession);

		assertEquals(sequence.getAccession(), accession);

		assertTrue(sequence.isInNeedOfNewVersion());

		sequence.unsetInNeedOfNewVersion();

		sequence.setAccession(accession);

		assertFalse(sequence.isInNeedOfNewVersion());

		sequence.unsetInNeedOfNewVersion();

		sequence.setAccession(null);

		assertNull(sequence.getAccession());

	}

	/**
	 * Run setDescription through its paces:
	 * <ol>
	 * <li>straight set and verify in need of new pPOD version</li>
	 * <li>set w/ already-value and make sure its not in need of a new pPOD
	 * version</li>
	 * <li>set w/ a null value</li>
	 * </ol>
	 */
	@Test
	public void setDescription() {
		final Sequence sequence = dnaSequenceProvider.get();

		assertNull(sequence.getDescription());

		final String description = "DESC0";
		sequence.setDescription(description);

		assertEquals(sequence.getDescription(), description);

		assertTrue(sequence.isInNeedOfNewVersion());

		sequence.unsetInNeedOfNewVersion();

		sequence.setDescription(description);

		assertFalse(sequence.isInNeedOfNewVersion());

		sequence.unsetInNeedOfNewVersion();

		sequence.setDescription(null);

		assertNull(sequence.getDescription());

	}
}
