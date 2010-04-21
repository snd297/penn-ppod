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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 * 
 */
@Test(groups = TestGroupDefs.FAST)
public class DNASequenceTest {

	private final ImmutableList<java.lang.Character> alpabet = ImmutableList
			.of('A', 'B',
					'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
					'O',
					'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

	private final ImmutableList<java.lang.Character> legalDNACharacters = ImmutableList
			.of('A',
					'C',
					'G',
					'R',
					'T',
					'Y',
					'K',
					'M',
					'S',
					'W',
					'B',
					'D',
					'H',
					'V',
					'N',
					'-');

	public void testIsLegal() {
		final MolecularSequence<?> sequence = new DNASequence();

		for (final java.lang.Character character : legalDNACharacters) {
			assertTrue(sequence.isLegal(character));
		}
	}

	public void testIsLegalShouldReturnFalse() {
		final MolecularSequence<?> sequence = new DNASequence();

		for (final java.lang.Character illegalCharacter : alpabet) {
			if (legalDNACharacters.contains(illegalCharacter)) {
				// it's legal don't see if it's false
			} else {
				assertFalse(sequence.isLegal(illegalCharacter));
			}
			final java.lang.Character lowerCaseIllegalCharacter = java.lang.Character
					.toLowerCase(illegalCharacter);
			assertFalse(sequence.isLegal(lowerCaseIllegalCharacter));
		}
	}
}
