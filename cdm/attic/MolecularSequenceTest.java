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

import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Tests for {@link Sequence}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST)
public class MolecularSequenceTest {

	@Test
	public void setSequence() {
		final Sequence<?> sequence = new DnaSequence();
		final String sequenceString = "ACGMTARC-T-A";
		sequence.setSequence(sequenceString);
		assertEquals(sequence.getSequence(), sequenceString);
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
		final Sequence<?> sequence = new DnaSequence();
		final String sequenceString = "ACGTlC-T-A";
		sequence.setSequence(sequenceString);
	}

}
