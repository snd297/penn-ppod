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
 * Tests for {@link MolecularSequence}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.INIT, TestGroupDefs.FAST })
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
	public void setSequence() {
		final MolecularSequence<DNASequenceSet> sequence = dnaSequenceProvider
				.get();
		final String sequenceString = "ACGTAC-T-A";
		sequence.setSequence(sequenceString);
		assertEquals(sequence.getSequence(), sequenceString);
		assertTrue(sequence.isInNeedOfNewPPodVersionInfo());

		// Now set the same sequence - should have no affect on need for new
		// ppod version
		sequence.unsetInNeedOfNewPPodVersionInfo();
		sequence.setSequence(sequenceString);
		assertEquals(sequence.getSequence(), sequenceString);
		assertFalse(sequence.isInNeedOfNewPPodVersionInfo());
	}

	/**
	 * {@code setSequence(...)} with an illegal character. Note that since
	 * {@link DNASequenceSet.isLegal} is tested separately and should be testing
	 * all illegal characters, we don't need to test all illegal characters
	 * here. Note that the fact that {@code setSequence(...)} is calling {@code
	 * isLegal(...)} is part of its public contract, so it's all right for us to
	 * reason about it here.
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setSequenceWithAnIllegalCharacter() {
		final MolecularSequence<DNASequenceSet> sequence = dnaSequenceProvider
				.get();
		final String sequenceString = "ACGTlC-T-A";
		sequence.setSequence(sequenceString);
	}

	/**
	 * Make sure that {@link MolecularSequence#setInNeedOfNewPPodVersionInfo()}
	 * works on both the sequence and parent sequence set.
	 * <p>
	 * Also make sure that it works if no parent has been set.
	 */
	public void setInNeedOfNewPPodVersion() {
		final DNASequence sequence = dnaSequenceProvider
				.get();
		final OTUSet otuSet = otuSetProvider.get();
		final OTU otu0 = otuSet.addOTU(otuProvider.get());

		final MolecularSequenceSet<DNASequence> sequenceSet = dnaSequenceSetProvider
				.get();

		sequenceSet.setOTUSet(otuSet);

		sequenceSet.putSequence(otu0, sequence);

		sequence.unsetInNeedOfNewPPodVersionInfo();
		sequenceSet.unsetInNeedOfNewPPodVersionInfo();

		sequence.setInNeedOfNewPPodVersionInfo();

		assertTrue(sequence.isInNeedOfNewPPodVersionInfo());

		assertTrue(sequenceSet.isInNeedOfNewPPodVersionInfo());

		// Let's make sure it works if no parent has been set.
		sequence.setSequenceSet(null);
		sequence.unsetInNeedOfNewPPodVersionInfo();

		sequence.setInNeedOfNewPPodVersionInfo();

		assertTrue(sequence.isInNeedOfNewPPodVersionInfo());

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
	public void setName() {
		final MolecularSequence<?> sequence = dnaSequenceProvider.get();

		assertNull(sequence.getName());

		final String sequenceName = "SEQ0";
		sequence.setName(sequenceName);

		assertEquals(sequence.getName(), sequenceName);

		assertTrue(sequence.isInNeedOfNewPPodVersionInfo());

		sequence.unsetInNeedOfNewPPodVersionInfo();

		sequence.setName(sequenceName);

		assertFalse(sequence.isInNeedOfNewPPodVersionInfo());

		sequence.unsetInNeedOfNewPPodVersionInfo();

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
	public void setAccession() {
		final MolecularSequence<?> sequence = dnaSequenceProvider.get();

		assertNull(sequence.getAccession());

		final String accession = "ACC0";
		sequence.setAccession(accession);

		assertEquals(sequence.getAccession(), accession);

		assertTrue(sequence.isInNeedOfNewPPodVersionInfo());

		sequence.unsetInNeedOfNewPPodVersionInfo();

		sequence.setAccession(accession);

		assertFalse(sequence.isInNeedOfNewPPodVersionInfo());

		sequence.unsetInNeedOfNewPPodVersionInfo();

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
	public void setDescription() {
		final MolecularSequence<?> sequence = dnaSequenceProvider.get();

		assertNull(sequence.getDescription());

		final String description = "DESC0";
		sequence.setDescription(description);

		assertEquals(sequence.getDescription(), description);

		assertTrue(sequence.isInNeedOfNewPPodVersionInfo());

		sequence.unsetInNeedOfNewPPodVersionInfo();

		sequence.setDescription(description);

		assertFalse(sequence.isInNeedOfNewPPodVersionInfo());

		sequence.unsetInNeedOfNewPPodVersionInfo();

		sequence.setDescription(null);

		assertNull(sequence.getDescription());

	}
}
