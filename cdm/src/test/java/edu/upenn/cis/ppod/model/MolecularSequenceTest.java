package edu.upenn.cis.ppod.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.INIT, TestGroupDefs.FAST, TestGroupDefs.SINGLE })
public class MolecularSequenceTest {

	@Inject
	private Provider<DNASequence> dnaSequenceProvider;

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
}
